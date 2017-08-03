package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dbkudryavtsev.childrencrosswords.R;

class SQLiteInteractor extends SQLiteOpenHelper {

    private final String answersTableName;
    private final String crosswordNumColumnName;
    private final String orderNumColumnName;
    private final String answerColumnName;

    private final String wordsTableName;
    private final String questionColumnName;
    private final String posXColumnName;
    private final String posYColumnName;
    private final String isHorisontalColumnName;

    SQLiteInteractor(Context context) {
        super(context, context.getString(R.string.database_name), null, 1);

        answersTableName = context.getString(R.string.answers_table_name);
        crosswordNumColumnName = context.getString(R.string.crossword_num_column_name);
        orderNumColumnName = context.getString(R.string.order_num_column_name);
        answerColumnName = context.getString(R.string.answer_column_name);

        wordsTableName = context.getString(R.string.words_table_name);
        questionColumnName = context.getString(R.string.question_column_name);
        posXColumnName = context.getString(R.string.posx_column_name);
        posYColumnName = context.getString(R.string.posy_column_name);
        isHorisontalColumnName = context.getString(R.string.is_horizontal_column_name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+answersTableName+" (" +
                "id integer primary key autoincrement, " +
                crosswordNumColumnName+" integer, " +
                orderNumColumnName +" integer, " +
                answerColumnName+" text" +
                ");");

        db.execSQL("CREATE TABLE "+wordsTableName+" (" +
                "id integer primary key autoincrement, " +
                crosswordNumColumnName+" integer, " +
                questionColumnName +" text, " +
                answerColumnName+" text, " +
                posXColumnName+" integer, " +
                posYColumnName+" integer, " +
                isHorisontalColumnName +" bit"+
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
