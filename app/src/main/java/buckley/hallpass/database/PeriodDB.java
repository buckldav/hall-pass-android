package buckley.hallpass.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import buckley.hallpass.model.PeriodInfo;

/**
 * @author David Buckley
 * Updated 4/7/2018
 *
 * Methods related to the PeriodInfo tables in the database
 */
public class PeriodDB {
    public static final class PeriodTable {
        public static final String NAME = "PERIOD_INFO";

        public static final class Cols {
            public static final String ID_ENUM = "ID_ENUM";
            public static final String PERIOD = "PERIOD";
            public static final String ID_GET = "ID_GET";
            public static final String ID_GET_SHEET = "ID_GET_SHEET";
            public static final String ID_POST = "ID_POST";
            public static final String ID_POST_SHEET = "ID_POST_SHEET";
        }
    }

    public static ContentValues getContentValues(PeriodInfo periodInfo) {
        ContentValues values = new ContentValues();
        values.put(PeriodTable.Cols.ID_ENUM, periodInfo.getPeriodInt());
        values.put(PeriodTable.Cols.PERIOD, periodInfo.getPeriodStr());
        values.put(PeriodTable.Cols.ID_GET, periodInfo.getIdGet());
        values.put(PeriodTable.Cols.ID_GET_SHEET, periodInfo.getIdGetSheet());
        values.put(PeriodTable.Cols.ID_POST, periodInfo.getIdPost());
        values.put(PeriodTable.Cols.ID_POST_SHEET, periodInfo.getIdPostSheet());

        return values;
    }

    public static PeriodCursorWrapper getAllPeriodInfos(SQLiteDatabase database) {
        Cursor cursor = database.query(
                PeriodTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new PeriodCursorWrapper(cursor);
    }

    public static String where(PeriodInfo periodInfo) {
        return PeriodTable.Cols.ID_ENUM + " = " + periodInfo.getPeriodInt();
    }
}
