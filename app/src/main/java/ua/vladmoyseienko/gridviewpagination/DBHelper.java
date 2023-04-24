package ua.vladmoyseienko.gridviewpagination;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private final String TABLE_NAME = "mytable";
    private final String COLUMN_ONE_NAME = "firstColumn";
    private final String COLUMN_TWO_NAME = "secondColumn";
    private static final int DATABASE_VERSION = 1;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS mytable");

        sqLiteDatabase.execSQL("CREATE TABLE mytable (id INTEGER PRIMARY KEY AUTOINCREMENT,data INTEGER, moreData INTEGER)");    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS mytable");
        // Создание таблицы заново
        onCreate(sqLiteDatabase);
    }
    public String getTableName(){
        return TABLE_NAME;
    }
}
