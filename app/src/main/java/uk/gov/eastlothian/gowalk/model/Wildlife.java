package uk.gov.eastlothian.gowalk.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeEntry;

/**
 * Created by davidmorrison on 10/12/14.
 */
public class Wildlife extends BaseRecord {

    private static String LOG_TAG = Wildlife.class.getSimpleName();

    private long id;
    private String name;
    private String category;
    private String description;
    private String imageName;
    private String whenSeen;

    public int getNumLogEntries() {
        return numLogEntries;
    }

    private int numLogEntries;

    private Wildlife(Cursor cursor) {
        this.id = getLong(cursor, WildlifeEntry._ID, -1);
        this.name = getString(cursor, WildlifeEntry.COLUMN_WILDLIFE_NAME, "unknown name");
        this.category = getString(cursor, WildlifeEntry.COLUMN_CATEGORY, "unknown category");
        this.description = getString(cursor, WildlifeEntry.COLUMN_DESCRIPTION, "unknown description");
        this.imageName = getString(cursor, WildlifeEntry.COLUMN_IMAGE_NAME, "unknown image");
        this.whenSeen = getString(cursor, WildlifeEntry.COLUMN_WHEN_SEEN, "unknown when seen");
        this.numLogEntries = getInt(cursor, "num_log_entries", 0);
    }

    public static List<Wildlife> fromCursor(Cursor cursor) {
        List<Wildlife> rtnList = new ArrayList<Wildlife>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Wildlife wildlife = new Wildlife(cursor);
            rtnList.add(wildlife);
        }
        return rtnList;
    }

    public String getWhenSeen() {
        return whenSeen;
    }

    public String getImageName() {
        return imageName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public int getImageResourceId(Context context) {
        String [] parts = imageName.split("\\.");
        String packageName = context.getPackageName();
        int rtnId = context.getResources().getIdentifier(parts[0], "drawable", packageName);
        if (rtnId == 0) {
            //Log.d(LOG_TAG, "Unable to find resource " + parts[0] + ".");
        }
        return rtnId;
    }

    public String getCapitalisedName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
