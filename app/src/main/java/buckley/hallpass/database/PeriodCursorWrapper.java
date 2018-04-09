package buckley.hallpass.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.PeriodInfo;

/**
 * @author David Buckley
 * Updated 4/7/2018
 *
 * Methods related PeriodInfo queries from the database.
 */
public class PeriodCursorWrapper extends CursorWrapper {
    public PeriodCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public PeriodInfo getPeriodInfo() {
        ClassPeriod classPeriod = ClassPeriod.getFromInt(getInt(getColumnIndex(PeriodDB.PeriodTable.Cols.ID_ENUM)));
        String periodStr = getString(getColumnIndex(PeriodDB.PeriodTable.Cols.PERIOD));
        PeriodInfo periodInfo = new PeriodInfo(classPeriod, periodStr);

        periodInfo.setIdGet(getString(getColumnIndex(PeriodDB.PeriodTable.Cols.ID_GET)));
        periodInfo.setIdGetSheet(getString(getColumnIndex(PeriodDB.PeriodTable.Cols.ID_GET_SHEET)));
        periodInfo.setIdPost(getString(getColumnIndex(PeriodDB.PeriodTable.Cols.ID_POST)));
        periodInfo.setIdPostSheet(getString(getColumnIndex(PeriodDB.PeriodTable.Cols.ID_POST_SHEET)));

        return periodInfo;
    }
}
