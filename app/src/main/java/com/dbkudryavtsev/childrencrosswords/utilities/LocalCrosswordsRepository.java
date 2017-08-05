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
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.ANSWER_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.ANSWERS_TABLE_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.CROSSWORD_NUM_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.IS_HORISONTAL_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.ORDER_NUM_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.POS_X_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.POS_Y_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.QUESTION_COLUMN_NAME;
import static com.dbkudryavtsev.childrencrosswords.utilities.SQLiteInteractor.WORDS_TABLE_NAME;

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
            db.delete(ANSWERS_TABLE_NAME, null, null);
        }
        updateCompletenesses(context);
    }

    public void putAnswers(String[] answers, int chosenCrosswordId, Context context) {
        for (int i=0; i<answers.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CROSSWORD_NUM_COLUMN_NAME, chosenCrosswordId);
            contentValues.put(ORDER_NUM_COLUMN_NAME, i);
            contentValues.put(ANSWER_COLUMN_NAME, answers[i]);
            try(SQLiteDatabase db = interactor.getWritableDatabase()) {
                db.insert(ANSWERS_TABLE_NAME, null, contentValues);
            }
        }
        updateCompleteness(chosenCrosswordId, context);
    }

    public String[] getAnswers(int chosenCrosswordId, Context context) {
        String[] answers;
        try (SQLiteDatabase db = interactor.getWritableDatabase();
             Cursor cursor = db.query(ANSWERS_TABLE_NAME, null, CROSSWORD_NUM_COLUMN_NAME + " = ?",
                     new String[]{Integer.toString(chosenCrosswordId)}, null, null,
                     ORDER_NUM_COLUMN_NAME + " ASC")) {
            answers = parseColumnFromSQL(cursor, ANSWER_COLUMN_NAME);
            if (answers == null) {
                answers = new String[getCrossword(chosenCrosswordId, context).getCwordsLength()];
                for (int i = 0; i < answers.length; i++)
                    answers[i] = "";
            }
        }
        return answers;
    }

    private void writeCrosswordsToSQL(Context context) {
        try (SQLiteDatabase db = interactor.getWritableDatabase()) {
            File filesDirectory = context.getFilesDir();
            String crosswordFilename = context.getString(R.string.crossword_file_name);
            String jsonExtension = context.getString(R.string.json_extension);
            if (filesDirectory.length() != 0) {
                int i = 0;
                for (File file : filesDirectory.listFiles()) {
                    final String fileName = file.getName();
                    if (fileName.substring(0, crosswordFilename.length()).equals(crosswordFilename) &&
                            fileName.substring(fileName.length() - jsonExtension.length(), fileName.length()).equals(jsonExtension)) {
                        String crosswordJSONString = loadJSONFromFile(fileName, context);
                        Crossword crossword = parseCrosswordFromJson(crosswordJSONString);
                        for (int j = 0; j < crossword.getCwordsLength(); j++) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(CROSSWORD_NUM_COLUMN_NAME, i);
                            final CrosswordWord cword = crossword.getCword(j);
                            contentValues.put(QUESTION_COLUMN_NAME, cword.getQuestion());
                            contentValues.put(ANSWER_COLUMN_NAME, cword.getAnswer());
                            contentValues.put(POS_X_COLUMN_NAME, cword.getPosX());
                            contentValues.put(POS_Y_COLUMN_NAME, cword.getPosY());
                            contentValues.put(IS_HORISONTAL_COLUMN_NAME, j < crossword.getHorCount() ? 1 : 0);
                            db.insert(WORDS_TABLE_NAME, null, contentValues);
                        }
                    }
                    if (!file.delete())
                        throw new RuntimeException("Can't delete crossword file");
                    i++;
                }
            }
        }
    }

    public Crossword getCrossword(int chosenCrosswordId, Context context) {
        Crossword crossword;
        ArrayList<CrosswordWord> crosswordWords = new ArrayList<>();
        int horCount = 0;
        try(SQLiteDatabase db = interactor.getWritableDatabase();
            Cursor cursor = db.query(WORDS_TABLE_NAME, null, CROSSWORD_NUM_COLUMN_NAME +" = ?",
                    new String[]{Integer.toString(chosenCrosswordId)}, null, null, null)) {
            if (cursor.moveToFirst()){
                int question = cursor.getColumnIndex(QUESTION_COLUMN_NAME);
                int answer = cursor.getColumnIndex(ANSWER_COLUMN_NAME);
                int posX = cursor.getColumnIndex(POS_Y_COLUMN_NAME);
                int posY = cursor.getColumnIndex(POS_X_COLUMN_NAME);
                int isHorizontal = cursor.getColumnIndex(IS_HORISONTAL_COLUMN_NAME);
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
            db.delete(WORDS_TABLE_NAME, null,null);
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
            Cursor cursor = db.query(WORDS_TABLE_NAME, new String[]{CROSSWORD_NUM_COLUMN_NAME},
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