package es.agustruiz.anclapp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "anclapp.db";
    private static final int DATABASE_VERSION = 1;

    //region [Public methods]

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //endregion

    //region [SQLiteHelper methods]

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AnchorDAO.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AnchorDAO.dropTableQuery());
        onCreate(db);
    }

    //endregion
}
