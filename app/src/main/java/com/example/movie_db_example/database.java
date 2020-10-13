package com.example.movie_db_example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database extends SQLiteOpenHelper {

    String table="MOVIE_TABEL";
    String data="DATA";
    String movie_id="MOVIE_ID";

    public database( Context context) {
        super(context, "movie.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+table+"(DATA TEXT,MOVIE_ID TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+table);
        onCreate(db);
    }

    public boolean tbl_insert(String info,String id){
        SQLiteDatabase dbms=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(data,info);
        cv.put(movie_id,id);
        if(dbms.insert(table,null,cv)!=-1){
            return true;
        }
        return false;
    }

    public Cursor tbl_show () {
        SQLiteDatabase dblt = this.getWritableDatabase();
        String s = "SELECT * FROM " + table;
        Cursor cr = dblt.rawQuery(s, null);
        return cr;
    }

    public boolean tbl_delete(String name)
    {
        SQLiteDatabase dbms=this.getWritableDatabase();
        return dbms.delete(table, movie_id + "=" + name, null) > 0;
    }

}
