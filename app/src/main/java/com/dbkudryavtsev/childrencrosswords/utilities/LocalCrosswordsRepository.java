package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import java.io.File;
import java.util.ArrayList;

import static com.dbkudryavtsev.childrencrosswords.utilities.AnswersParser.parseColumnFromSQL;
import static com.dbkudryavtsev.childrencrosswords.utilities.CrosswordsParser.parseCrosswordFromJson;

import static com.dbkudryavtsev.childrencrosswords.utilities.CrosswordsDownloader.downloadCrosswords;
import static com.dbkudryavtsev.childrencrosswords.utilities.LocalRepository.loadJSONFromFile;

public final class LocalCrosswordsRepository {

    private int crosswordsCount;
    private int[] completenesses;
    private final SQLiteInteractor interactor;

    public LocalCrosswordsRepository(Context context) {
        interactor = new SQLiteInteractor(context);
        updateCrosswordsCount(context);
    }

    public void deleteAnswers(Context context){
        try(SQLiteDatabase db = interactor.getWritableDatabase()) {
            db.delete(context.getString(R.string.answers_table_name), null, null);
        }
        updateCompletenesses(context);
    }

    public void putAnswers(String[] answers, int chosenCrosswordId, Context context) {
        for (int i=0; i<answers.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(context.getString(R.string.crossword_num_column_name), chosenCrosswordId);
            contentValues.put(context.getString(R.string.order_num_column_name), i);
            contentValues.put(context.getString(R.string.answer_column_name), answers[i]);
            try(SQLiteDatabase db = interactor.getWritableDatabase()) {
                db.insert(context.getString(R.string.answers_table_name), null, contentValues);
            }
        }
        updateCompleteness(chosenCrosswordId, context);
    }

    public String[] getAnswers(int chosenCrosswordId, Context context) {
        String[] answers;
        try(SQLiteDatabase db = interactor.getWritableDatabase();
            Cursor cursor = db.query(context.getString(R.string.answers_table_name), null,
                    context.getString(R.string.crossword_num_column_name) +" = ?",
                new String[]{Integer.toString(chosenCrosswordId)}, null, null,
                    context.getString(R.string.order_num_column_name)+" ASC")) {
            answers = parseColumnFromSQL(cursor, context.getString(R.string.answer_column_name));
            if (answers == null) {
                answers = new String[getCrossword(chosenCrosswordId, context).getCwordsLength()];
                for (int i = 0; i < answers.length; i++)
                    answers[i] = "";
            }
        }
        return answers;
    }

    private void writeCrosswordsToSQL(Context context){
        File filesDirectory = context.getFilesDir();
        String crosswordFilename = context.getString(R.string.crossword_file_name);
        String jsonExtension = context.getString(R.string.json_extension);
        if(filesDirectory.length()!=0) {
            int i=0;
            for (File file : filesDirectory.listFiles()) {
                final String fileName = file.getName();
                if (fileName.substring(0,crosswordFilename.length()).equals(crosswordFilename) &&
                        fileName.substring(fileName.length()-jsonExtension.length(), fileName.length()).equals(jsonExtension)) {
                    String crosswordJSONString = loadJSONFromFile(fileName, context);
                    Crossword crossword = parseCrosswordFromJson(crosswordJSONString);
                    for (int j=0; j<crossword.getCwordsLength();j++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(context.getString(R.string.crossword_num_column_name), i);
                        final CrosswordWord cword = crossword.getCword(j);
                        contentValues.put(context.getString(R.string.question_column_name), cword.getQuestion());
                        contentValues.put(context.getString(R.string.answer_column_name), cword.getAnswer());
                        contentValues.put(context.getString(R.string.posx_column_name), cword.getPosX());
                        contentValues.put(context.getString(R.string.posy_column_name), cword.getPosY());
                        contentValues.put(context.getString(R.string.is_horizontal_column_name),
                                j<crossword.getHorCount()?1:0);
                        try (SQLiteDatabase db = interactor.getWritableDatabase()) {
                            db.insert(context.getString(R.string.words_table_name), null, contentValues);
                        }
                    }
                    if (!file.delete())
                        throw new RuntimeException("Can't delete crossword file");
                }
                i++;
            }
        }
    }

    public Crossword getCrossword(int chosenCrosswordId, Context context) {
        Crossword crossword;
        ArrayList<CrosswordWord> crosswordWords = new ArrayList<>();
        int horCount = 0;
        try(SQLiteDatabase db = interactor.getWritableDatabase();
            Cursor cursor = db.query(context.getString(R.string.words_table_name), null,
                    context.getString(R.string.crossword_num_column_name) +" = ?",
                    new String[]{Integer.toString(chosenCrosswordId)}, null, null, null)) {
            if (cursor.moveToFirst()){
                int question = cursor.getColumnIndex(context.getString(R.string.question_column_name));
                int answer = cursor.getColumnIndex(context.getString(R.string.answer_column_name));
                int posX = cursor.getColumnIndex(context.getString(R.string.posx_column_name));
                int posY = cursor.getColumnIndex(context.getString(R.string.posy_column_name));
                int isHorizontal = cursor.getColumnIndex(context.getString(R.string.is_horizontal_column_name));
                do{
                    final CrosswordWord crosswordWord = new CrosswordWord(cursor.getString(question),
                            cursor.getString(answer), cursor.getInt(posX), cursor.getInt(posY));
                    crosswordWords.add(crosswordWord);
                    if(cursor.getInt(isHorizontal)==1)
                        horCount++;
                } while(cursor.moveToNext());
            }
        }
        crossword = new Crossword(crosswordWords, horCount);
        return crossword;
    }

    public void updateCrosswords(Context context){
        downloadCrosswords(context);
        try(SQLiteDatabase db = interactor.getWritableDatabase()){
            db.delete(context.getString(R.string.words_table_name), null,null);
        }
        writeCrosswordsToSQL(context);
        updateCrosswordsCount(context);
    }

    public int getCrosswordsCount() {
        return crosswordsCount;
    }

    private void updateCrosswordsCount(Context context){
        crosswordsCount = 0;
        try(SQLiteDatabase db = interactor.getWritableDatabase();
            Cursor cursor = db.query(true, context.getString(R.string.words_table_name),
                    new String[]{context.getString(R.string.crossword_num_column_name)},
                    null, null, null, null, null, null)) {
            cursor.moveToFirst();
            crosswordsCount = cursor.getCount();
        }
        updateCompletenesses(context);
    }

    public int getCompleteness(int crosswordId){
        return completenesses[crosswordId];
    }

    private void updateCompleteness(int crosswordId, Context context) {
        completenesses[crosswordId] = 0;
        String[] answers = getAnswers(crosswordId, context);
        int completed = 0;
        for (String answer : answers)
            completed += (answer.equals("")) ? 0 : 1;
        completenesses[crosswordId] = completed*100/answers.length;
    }

    private void updateCompletenesses(Context context) {
        completenesses = new int[crosswordsCount];
        for(int i=0; i<crosswordsCount; i++) {
            String[] answers = getAnswers(i, context);
            int completed = 0;
            for (String answer : answers)
                completed += (answer.equals("")) ? 0 : 1;
            completenesses[i] = completed*100/answers.length;
        }
    }
}