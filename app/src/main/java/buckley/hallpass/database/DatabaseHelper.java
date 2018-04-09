package buckley.hallpass.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import buckley.hallpass.model.ClassPeriod;

/**
 * @author David Buckley
 * Updated 4/7/2018
 *
 * Creates database and tables in the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "database.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PeriodDB.PeriodTable.NAME + "(" +
                PeriodDB.PeriodTable.Cols.ID_ENUM + " primary key , " +
                PeriodDB.PeriodTable.Cols.PERIOD + ", " +
                PeriodDB.PeriodTable.Cols.ID_GET + ", " +
                PeriodDB.PeriodTable.Cols.ID_GET_SHEET + ", " +
                PeriodDB.PeriodTable.Cols.ID_POST + ", " +
                PeriodDB.PeriodTable.Cols.ID_POST_SHEET +
                ")"
        );

        // Each period has its own student table
        for (ClassPeriod p : ClassPeriod.values()) {
            db.execSQL("create table " + StudentDB.StudentTable.getTitle(p) + "(" +
                    StudentDB.StudentTable.Cols.NAME + " primary key , " +
                    StudentDB.StudentTable.Cols.STATUS +
                    ")"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
