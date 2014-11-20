package uk.gov.eastlothian.gowalk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmorrison on 20/11/14.
 */
public class TableBuilder {
    private String name;
    private Column primaryKey;
    private List<Column> columns = new ArrayList<Column>();
    private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();

    private class Column {
        private String name;
        private String type;
        private Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
        public String asQuery() {
            return name + " " + type + " NOT NULL";
        }
    }
    private class ForeignKey {
        private String key;
        private String refTable;
        private String refKey;

        private ForeignKey(String key, String refTable, String refKey) {
            this.key = key;
            this.refTable = refTable;
            this.refKey = refKey;
        }
        public String asQuery() {
            return "FOREIGN KEY (" + key + ") REFERENCE " + refTable + " (" + refKey + ")";
        }
    }

    public TableBuilder(String name, String primaryKey) {
        this.name = name;
        addPrimaryKey(primaryKey);
    }

    private void addPrimaryKey(String name) {
        primaryKey = new Column(name, "INTEGER PRIMARY KEY AUTOINCREMENT");
    }
    public TableBuilder addIntegerColumn(String name) {
        columns.add(new Column(name, "INTEGER"));
        return this;
    }
    public TableBuilder addTextColumn(String name) {
        columns.add(new Column(name, "TEXT"));
        return this;
    }
    public TableBuilder addForeignKey(String key, String refTable, String refKey) {
        foreignKeys.add(new ForeignKey(key, refTable, refKey));
        return this;
    }

    public String buildQuery() {
        String rtn = "CREATE TABLE " + name + "(";
        rtn += primaryKey + " INTEGER PRIMARY KEY";
        String separator = ",";
        for(Column col : columns) {
            rtn += separator + col.asQuery();
        }
        for(ForeignKey key : foreignKeys) {
            rtn += separator + key.asQuery();
        }
        rtn += ");";
        return rtn;
    }
}