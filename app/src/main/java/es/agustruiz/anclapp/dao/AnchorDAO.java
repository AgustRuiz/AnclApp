package es.agustruiz.anclapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private static final String COL_IS_DELETED = "deleted";
    private static final String COL_DETELED_TIMESTAMP = "deletedTimestamp";

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

    public static final int QUERY_GET_DELETED = 1;
    public static final int QUERY_GET_NOT_DELETED = 0;
    public static final int QUERY_GET_ALL = -1;

    public List<Anchor> getAll(int mode) {
        List<Anchor> result = new ArrayList<>();
        String query = "select * from " + TABLE_NAME;
        switch (mode) {
            case QUERY_GET_DELETED:
                query = query.concat(" where " + COL_IS_DELETED + "=1");
                break;
            case QUERY_GET_NOT_DELETED:
                query = query.concat(" where " + COL_IS_DELETED + "=0");
                break;
            case QUERY_GET_ALL:
                break;
        }
        String[] params = new String[]{};
        Cursor cursor = mDatabase.rawQuery(query, params);
        if (cursor.moveToFirst()) {
            do {
                result.add(getAnchorFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        orderList(result);
        return result;
    }

    public List<Anchor> getByTitle(String titleQuery, int mode) {
        titleQuery = titleQuery.trim();
        if (titleQuery.length() > 0) {
            List<Anchor> result = new ArrayList<>();
            String[] titleQueries = titleQuery.split(" ");
            String query = "select * from " + TABLE_NAME + " where 1=1";
            for (String q : titleQueries) {
                query = query.concat(" and " + COL_TITLE + " like '%" + q + "%'");
            }
            String[] params = new String[]{};
            Cursor cursor = mDatabase.rawQuery(query, params);
            if (cursor.moveToFirst()) {
                do {
                    result.add(getAnchorFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            orderList(result);
            return result;
        } else {
            return getAll(mode);
        }
    }

    public int countAll(int mode) {
        int result = 0;
        String query = "select count(*) from " + TABLE_NAME;
        switch (mode) {
            case QUERY_GET_DELETED:
                query = query.concat(" where " + COL_IS_DELETED + "=1");
                break;
            case QUERY_GET_NOT_DELETED:
                query = query.concat(" where " + COL_IS_DELETED + "=0");
                break;
            case QUERY_GET_ALL:
                break;
        }
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
        values.put(COL_IS_DELETED, (anchor.isDeleted() ? 1 : 0));
        values.put(COL_DETELED_TIMESTAMP, anchor.getDeletedTimestam());
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
        values.put(COL_IS_DELETED, (anchor.isDeleted() ? 1 : 0));
        values.put(COL_DETELED_TIMESTAMP, anchor.getDeletedTimestam());
        String whereClause = COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(anchor.getId())};
        int result = mDatabase.update(TABLE_NAME, values, whereClause, whereArgs);
        return result > 0;
    }

    public boolean moveToBin(Anchor anchor) {
        return moveToBin(anchor.getId());
    }

    public boolean moveToBin(long id) {
        ContentValues values = new ContentValues();
        //values.put(COL_ID, id);
        values.put(COL_IS_DELETED, true);
        values.put(COL_DETELED_TIMESTAMP, System.currentTimeMillis());
        String whereClause = COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        int result = mDatabase.update(TABLE_NAME, values, whereClause, whereArgs);
        return result > 0;
    }

    public boolean restore(Anchor anchor){
        return restore(anchor.getId());
    }

    public boolean restore(long id){
        ContentValues values = new ContentValues();
        //values.put(COL_ID, id);
        values.put(COL_IS_DELETED, false);
        values.put(COL_DETELED_TIMESTAMP, "null");
        String whereClause = COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        int result = mDatabase.update(TABLE_NAME, values, whereClause, whereArgs);
        return result > 0;
    }

    public boolean delete(Anchor anchor) {
        return delete(anchor.getId());
    }

    public boolean delete(long id) {
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
            result = getAnchorFromCursor(cursor);
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
                + COL_REMINDER + " short not null, "
                + COL_IS_DELETED + " boolean not null, "
                + COL_DETELED_TIMESTAMP + " integer null )";
    }

    public static String dropTableQuery() {
        return "drop table if exists " + TABLE_NAME;
    }

    //endregion

    //region [Private methods]

    private Anchor getAnchorFromCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        double latitude = cursor.getDouble(1);
        double longitude = cursor.getDouble(2);
        String title = cursor.getString(3);
        String description = cursor.getString(4);
        String color = cursor.getString(5);
        boolean reminder = cursor.getInt(6) != 0;
        boolean isDeleted = cursor.getInt(7) != 0;
        long deletedTimestam = cursor.getLong(8);
        return new Anchor(id, latitude, longitude, title, description, color, reminder, isDeleted, deletedTimestam);
    }

    private void orderList(List<Anchor> list) {
        Collections.sort(list, new Anchor.Comparator());
    }

    //endregion

}
