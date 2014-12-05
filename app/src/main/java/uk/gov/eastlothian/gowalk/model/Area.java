package uk.gov.eastlothian.gowalk.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.data.WalksContract.AreaEntry;

/**
 * Created by davidmorrison on 05/12/14.
 */
public class Area extends BaseRecord {

    private long id;
    private String name;

    public Area(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Area> fromCursor(Cursor cursor) {
        List<Area> rtnList = new ArrayList<Area>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long id = getLong(cursor, AreaEntry._ID, -1);
            String name = getString(cursor, AreaEntry.COLUMN_AREA_NAME, "");
            Area area = new Area(id, name);
            rtnList.add(area);
        }
        return rtnList;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
