package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dbkudryavtsev.childrencrosswords.R;

public class SQLiteInteractor extends SQLiteOpenHelper {

    public SQLiteInteractor(Context context) {
        super(context, context.getString(R.string.database_name), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE answers (" +
                "id integer primary key autoincrement," +
                "crossword_num integer," +
                "order_num integer," +
                "answer text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
