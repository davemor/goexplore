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

/**
 * Created by davidmorrison on 19/11/14.
 */
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
