package kai.search.karaokebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjwon15 on 2014. 7. 17..
 */
public class DbAdapter {
    private static final String TABLE_SONG = "songs";
    private static final String COL_VENDOR = "vendor";
    private static final String COL_NUMBER = "number";
    private static final String COL_TITLE = "title";
    private static final String COL_SINGER = "singer";

    private static final String TABLE_INFO = "information";
    private static final String COL_UPDATED = "updated";

    private Context context;
    private DbHelper dbHelper;

    public DbAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }

    public long createSong(Song song) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_VENDOR, song.getVendor());
        values.put(COL_NUMBER, song.getNumber());
        values.put(COL_TITLE, song.getTitle());
        values.put(COL_SINGER, song.getSinger());

        long id = db.insert(TABLE_SONG, null, values);
        db.close();
        return id;
    }

    public boolean createSongs(List<Song> songs, String updated) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean succeed = false;
        db.beginTransaction();
        try {
            for (Song song : songs) {
                ContentValues values = new ContentValues();
                values.put(COL_VENDOR, song.getVendor());
                values.put(COL_NUMBER, song.getNumber());
                values.put(COL_TITLE, song.getTitle());
                values.put(COL_SINGER, song.getSinger());
                db.insert(TABLE_SONG, null, values);
            }

            ContentValues values = new ContentValues();
            values.put(COL_UPDATED, updated);
            db.update(TABLE_INFO, values, null, null);

            db.setTransactionSuccessful();
            succeed = true;
        } finally {
            db.endTransaction();
            db.close();
        }

        return succeed;
    }

    public List<Song> getSongs(String _vendor, String _title, String _number, String _singer) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> whereClauses = new ArrayList<String>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        ArrayList<Song> results = new ArrayList<Song>();

        if (_vendor != null) {
            whereClauses.add("vendor like ?");
            whereArgs.add('%' + _vendor + '%');
        }

        if (_title != null) {
            whereClauses.add("title like ?");
            whereArgs.add('%' + _title + '%');
        }

        if (_number != null) {
            whereClauses.add("number like ?");
            whereArgs.add('%' + _number + '%');
        }

        if (_singer != null) {
            whereClauses.add("singer like ?");
            whereArgs.add('%' + _singer + '%');
        }

        String[] args = whereArgs.toArray(new String[whereArgs.size()]);
        Cursor cursor = db.query(TABLE_SONG,
                new String[]{COL_VENDOR, COL_NUMBER, COL_TITLE, COL_SINGER},
                TextUtils.join(" and ", whereClauses.toArray()), args,
                null, null, COL_TITLE + " asc", "100");

        int indexVendor = cursor.getColumnIndex(COL_VENDOR);
        int indexTitle = cursor.getColumnIndex(COL_TITLE);
        int indexNumber = cursor.getColumnIndex(COL_NUMBER);
        int indexSinger = cursor.getColumnIndex(COL_SINGER);
        while (cursor.moveToNext()) {
            String vendor = cursor.getString(indexVendor);
            String title = cursor.getString(indexTitle);
            String number = cursor.getString(indexNumber);
            String singer = cursor.getString(indexSinger);
            results.add(new Song(vendor, number, title, singer));
        }

        return results;
    }

    public String getLastUpdated() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INFO,
                new String[]{COL_UPDATED},
                null,
                null, null, null, null);

        cursor.moveToFirst();
        String lastUpdated = cursor.getString(0);

        return lastUpdated;
    }


    private class DbHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "karaoke";
        private static final int DB_VERSION = 1;

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query;
            query = String.format(
                    "create table %s(" +
                            "%s text not null," +
                            "%s text not null," +
                            "%s text not null," +
                            "%s text not null" +
                            ");",
                    TABLE_SONG, COL_VENDOR, COL_NUMBER, COL_TITLE, COL_SINGER
            );
            db.execSQL(query);

            query = String.format(
                    "create table %s(" +
                            "%s date not null" +
                            ");",
                    TABLE_INFO, COL_UPDATED
            );

            // Insert zero last updated.
            ContentValues values = new ContentValues();
            values.put(COL_UPDATED, "1970-01-01");
            db.insert(TABLE_INFO, null, values);

            Log.i("DB", "Database created");
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("DB", "Database upgraded");
        }
    }
}
