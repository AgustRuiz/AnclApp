package es.agustruiz.anclapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.model.Anchor;

public class AnchorDAO {

    public static final String LOG_TAG = AnchorDAO.class.getName() + "[A]";

    private static final String TABLE_NAME = "anchor";
    private static final String COL_ID = "id";
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_COLOR = "color";
    private static final String COL_REMINDER = "reminder";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DatabaseManager mDatabaseManager;

    //region [Constructor]

    public AnchorDAO(Context context) {
        mContext = context;
        mDatabase = null;
        mDatabaseManager = new DatabaseManager(mContext);
    }

    //endregion

    //region [Public methods]

    public void openWritable() {
        mDatabase = mDatabaseManager.getWritableDatabase();
    }

    public void openReadOnly() {
        mDatabase = mDatabaseManager.getReadableDatabase();
    }

    public void close() {
        mDatabase.close();
        mDatabase = null;
    }

    public List<Anchor> getAll() {
        List<Anchor> result = new ArrayList<>();
        String query = "select * from " + TABLE_NAME;
        String[] params = new String[]{};
        Cursor cursor = mDatabase.rawQuery(query, params);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                double latitude = cursor.getDouble(1);
                double longitude = cursor.getDouble(2);
                String title = cursor.getString(3);
                String description = cursor.getString(4);
                String color = cursor.getString(5);
                boolean reminder = cursor.getInt(6) != 0;
                result.add(new Anchor(id, latitude, longitude, title, description, color, reminder));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public int countAll(){
        int result = 0;
        String query = "select count(*) from " + TABLE_NAME;
        String[] params = new String[]{};
        Cursor cursor = mDatabase.rawQuery(query, params);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public long add(Anchor anchor) {
        ContentValues values = new ContentValues();
        values.put(COL_LATITUDE, anchor.getLatitude());
        values.put(COL_LONGITUDE, anchor.getLongitude());
        values.put(COL_TITLE, anchor.getTitle());
        values.put(COL_DESCRIPTION, anchor.getDescription());
        values.put(COL_COLOR, anchor.getColor());
        values.put(COL_REMINDER, (anchor.isReminder() ? 1 : 0));
        return mDatabase.insert(TABLE_NAME, null, values);
    }

    public boolean update(Anchor anchor) {
        ContentValues values = new ContentValues();
        //values.put(COL_ID, id);
        values.put(COL_LATITUDE, anchor.getLatitude());
        values.put(COL_LONGITUDE, anchor.getLongitude());
        values.put(COL_TITLE, anchor.getTitle());
        values.put(COL_DESCRIPTION, anchor.getDescription());
        values.put(COL_COLOR, anchor.getColor());
        values.put(COL_REMINDER, (anchor.isReminder() ? 1 : 0));
        String whereClause = COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(anchor.getId())};
        int result = mDatabase.update(TABLE_NAME, values, whereClause, whereArgs);
        return result > 0;
    }

    public boolean remove(Anchor anchor) {
        return remove(anchor.getId());
    }

    public boolean remove(long id) {
        String whereClause = COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        int result = mDatabase.delete(TABLE_NAME, whereClause, whereArgs);
        return result > 0;
    }


    public Anchor get(long id) {
        Anchor result = null;
        String query = "select * from " + TABLE_NAME + " where id=?";
        String[] params = new String[]{String.valueOf(id)};
        Cursor cursor = mDatabase.rawQuery(query, params);
        if (cursor.moveToFirst()) {
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            String title = cursor.getString(3);
            String description = cursor.getString(4);
            String color = cursor.getString(5);
            boolean reminder = cursor.getInt(6) != 0;
            result = new Anchor(id, latitude, longitude, title, description, color, reminder);
        }
        cursor.close();
        return result;
    }

    //endregion

    //region [Public static methods]

    public static String createTableQuery() {
        return "create table if not exists " + TABLE_NAME + " ( "
                + COL_ID + " integer not null primary key autoincrement, "
                + COL_LATITUDE + " real not null, "
                + COL_LONGITUDE + " real not null, "
                + COL_TITLE + " varchar(255) not null, "
                + COL_DESCRIPTION + " varchar(255) not null, "
                + COL_COLOR + " varchar(7) not null, "
                + COL_REMINDER + " short not null )";
    }

    public static String dropTableQuery() {
        return "drop table if exists " + TABLE_NAME;
    }

    //endregion

}
