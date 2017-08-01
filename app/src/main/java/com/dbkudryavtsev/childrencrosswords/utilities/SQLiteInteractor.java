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

    SQLiteInteractor(Context context) {
        super(context, context.getString(R.string.database_name), null, 1);
        answersTableName = context.getString(R.string.database_name);
        crosswordNumColumnName = context.getString(R.string.database_name);
        orderNumColumnName = context.getString(R.string.database_name);
        answerColumnName = context.getString(R.string.database_name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+answersTableName+" (" +
                "id integer primary key autoincrement, " +
                crosswordNumColumnName+" integer, " +
                orderNumColumnName +" integer, " +
                answerColumnName+" text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
