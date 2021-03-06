
package uk.gov.eastlothian.gowalk.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import uk.gov.eastlothian.gowalk.data.WalksContract.RouteEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.AreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.RouteInAreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeOnRouteEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.LogEntry;

public class WalksDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = WalksDbHelper.class.getSimpleName();

    public static final String DB_NAME = "walks.db";
    private static final int DB_VERSION = 1;
    private static File DATABASE_FILE;

    private boolean mInvalidDatabaseFile = false;
    private Context mContext;

    public WalksDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        DATABASE_FILE = context.getDatabasePath(DB_NAME);

        SQLiteDatabase db;

        db = getReadableDatabase(); // this calls on create
        if (db != null) {
            db.close();
        }
        if (mInvalidDatabaseFile) {
            copyDatabase();
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void copyDatabase() {
        AssetManager assetManager = mContext.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(DB_NAME);
            out = new FileOutputStream(DATABASE_FILE);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        setDatabaseVersion();
        mInvalidDatabaseFile = false;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + DB_VERSION);
        } catch (SQLiteException e ) {
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        mInvalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        mInvalidDatabaseFile = true;
    }
}


/*
package uk.gov.eastlothian.gowalk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

import uk.gov.eastlothian.gowalk.data.WalksContract.RouteEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.AreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.RouteInAreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeOnRouteEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.LogEntry;

public class WalksDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = WalksDbHelper.class.getSimpleName();

    public static final String DB_NAME = "walks.db";
    private static final int DB_VERSION = 1;
    private Context mContext;

    public WalksDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        // Geographical Data
        final String SQL_CREATE_ROUTE_TABLE =
                new TableBuilder(RouteEntry.TABLE_NAME, RouteEntry._ID)
                        .addIntegerColumn(RouteEntry.COLUMN_ROUTE_NUMBER)
                        .addTextColumn(RouteEntry.COLUMN_COORDINATES)
                        .addTextColumn(RouteEntry.COLUMN_PATH_TYPE)
                        .addTextColumn(RouteEntry.COLUMN_LENGTH)
                        .addTextColumn(RouteEntry.COLUMN_SURFACE)
                        .addTextColumn(RouteEntry.COLUMN_DESCRIPTION)
                        .addTextColumn(RouteEntry.COLUMN_PRIMARY_AREA)
                        .buildQuery();

        final String SQL_CREATE_AREA_TABLE =
                new TableBuilder(AreaEntry.TABLE_NAME, AreaEntry._ID)
                        .addTextColumn(AreaEntry.COLUMN_AREA_NAME)
                        .buildQuery();

        final String SQL_CREATE_ROUTE_IN_AREA_TABLE =
                new TableBuilder(RouteInAreaEntry.TABLE_NAME, RouteInAreaEntry._ID)
                        .addIntegerColumn(RouteInAreaEntry.COLUMN_ROUTE_KEY)
                        .addIntegerColumn(RouteInAreaEntry.COLUMN_AREA_KEY)
                        .buildQuery();

        // Wildlife Data
        final String SQL_CREATE_WILDLIFE_TABLE =
                new TableBuilder(WildlifeEntry.TABLE_NAME, WildlifeEntry._ID)
                        .addTextColumn(WildlifeEntry.COLUMN_WILDLIFE_NAME)
                        .addTextColumn(WildlifeEntry.COLUMN_CATEGORY)
                        .addTextColumn(WildlifeEntry.COLUMN_DESCRIPTION)
                        .addTextColumn(WildlifeEntry.COLUMN_IMAGE_NAME)
                        .addTextColumn(WildlifeEntry.COLUMN_WHEN_SEEN)
                        .buildQuery();

        final String SQL_CREATE_WILDLIFE_ON_ROUTE_TABLE =
                new TableBuilder(WildlifeOnRouteEntry.TABLE_NAME, WildlifeOnRouteEntry._ID)
                        .addIntegerColumn(WildlifeOnRouteEntry.COLUMN_WILDLIFE_KEY)
                        .addIntegerColumn(WildlifeOnRouteEntry.COLUMN_ROUTE_KEY)
                        .addForeignKey( WildlifeOnRouteEntry.COLUMN_WILDLIFE_KEY,
                                WildlifeEntry.TABLE_NAME,
                                WildlifeEntry._ID)
                        .addForeignKey( WildlifeOnRouteEntry.COLUMN_ROUTE_KEY,
                                RouteEntry.TABLE_NAME,
                                RouteEntry._ID)
                        .buildQuery();

        // User Generated Content
        final String SQL_CREATE_LOG_ENTRY_TABLE =
                new TableBuilder(LogEntry.TABLE_NAME, LogEntry._ID)
                        .addIntegerColumn(LogEntry.COLUMN_WILDLIFE_KEY)
                        .addTextColumn(LogEntry.COLUMN_LAT)
                        .addTextColumn(LogEntry.COLUMN_LNG)
                        .addTextColumn(LogEntry.COLUMN_DATATIME)
                        .addTextColumn(LogEntry.COLUMN_WEATHER)
                        .addTextColumn(LogEntry.COLUMN_IMAGE)
                        .addForeignKey(LogEntry.COLUMN_WILDLIFE_KEY,
                                WildlifeEntry.TABLE_NAME,
                                WildlifeEntry._ID)
                        .buildQuery();

        // execute the creation of the tables
        database.execSQL(SQL_CREATE_ROUTE_TABLE);
        database.execSQL(SQL_CREATE_AREA_TABLE);
        database.execSQL(SQL_CREATE_ROUTE_IN_AREA_TABLE);
        database.execSQL(SQL_CREATE_WILDLIFE_TABLE);
        database.execSQL(SQL_CREATE_WILDLIFE_ON_ROUTE_TABLE);
        database.execSQL(SQL_CREATE_LOG_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        // drop the tables
        database.execSQL("DROP TABLE IF EXIST " + RouteEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXIST " + AreaEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXIST " + RouteInAreaEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXIST " + WildlifeEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXIST " + WildlifeOnRouteEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXIST " + LogEntry.TABLE_NAME);

        // create the database a new
        onCreate(database);
    }

}
*/