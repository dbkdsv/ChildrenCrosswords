package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class SQLiteInteractor extends SQLiteOpenHelper {

    static final String answersTableName = "answers";
    static final String crosswordNumColumnName = "crossword_num";
    static final String orderNumColumnName = "order_num";
    static final String answerColumnName = "answer";

    static final String wordsTableName = "words";
    static final String questionColumnName = "question";
    static final String posXColumnName = "posX";
    static final String posYColumnName = "posY";
    static final String isHorisontalColumnName = "is_horizontal";

    SQLiteInteractor(Context context) {
        super(context, "crosswordsAppDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + answersTableName + " (" +
                "id integer primary key autoincrement, " +
                crosswordNumColumnName + " integer, " +
                orderNumColumnName + " integer, " +
                answerColumnName + " text" +
                ");");

        db.execSQL("CREATE TABLE " + wordsTableName + " (" +
                "id integer primary key autoincrement, " +
                crosswordNumColumnName + " integer, " +
                questionColumnName + " text, " +
                answerColumnName + " text, " +
                posXColumnName + " integer, " +
                posYColumnName + " integer, " +
                isHorisontalColumnName + " bit" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
