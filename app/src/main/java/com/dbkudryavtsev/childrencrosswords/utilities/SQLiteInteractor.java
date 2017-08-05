package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class SQLiteInteractor extends SQLiteOpenHelper {

    static final String ANSWERS_TABLE_NAME = "answers";
    static final String CROSSWORD_NUM_COLUMN_NAME = "crossword_num";
    static final String ORDER_NUM_COLUMN_NAME = "order_num";
    static final String ANSWER_COLUMN_NAME = "answer";

    static final String WORDS_TABLE_NAME = "words";
    static final String QUESTION_COLUMN_NAME = "question";
    static final String POS_X_COLUMN_NAME = "posX";
    static final String POS_Y_COLUMN_NAME = "posY";
    static final String IS_HORISONTAL_COLUMN_NAME = "is_horizontal";

    SQLiteInteractor(Context context) {
        super(context, "crosswordsAppDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ANSWERS_TABLE_NAME + " (" +
                "id integer primary key autoincrement, " +
                CROSSWORD_NUM_COLUMN_NAME + " integer, " +
                ORDER_NUM_COLUMN_NAME + " integer, " +
                ANSWER_COLUMN_NAME + " text" +
                ");");

        db.execSQL("CREATE TABLE " + WORDS_TABLE_NAME + " (" +
                "id integer primary key autoincrement, " +
                CROSSWORD_NUM_COLUMN_NAME + " integer, " +
                QUESTION_COLUMN_NAME + " text, " +
                ANSWER_COLUMN_NAME + " text, " +
                POS_X_COLUMN_NAME + " integer, " +
                POS_Y_COLUMN_NAME + " integer, " +
                IS_HORISONTAL_COLUMN_NAME + " bit" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
