package buckley.hallpass.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.Student;

/**
 * @author David Buckley
 * Updated 4/7/2018
 *
 * Methods related to the Student tables in the database
 */
public class StudentDB {
    public static final class StudentTable {
        public static String getTitle(ClassPeriod classPeriod) {
            return "Student" + classPeriod.ordinal();
        }

        public static final class Cols {
            public static final String NAME = "NAME";
            public static final String STATUS = "STATUS";
        }
    }

    public static ContentValues getContentValues(Student student) {
        ContentValues values = new ContentValues();
        values.put(StudentTable.Cols.NAME, student.getName());
        values.put(StudentTable.Cols.STATUS, student.getStatus());

        return values;
    }

    public static StudentCursorWrapper getAllStudents(ClassPeriod period, SQLiteDatabase database) {
        Cursor cursor = database.query(
                StudentDB.StudentTable.getTitle(period),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new StudentCursorWrapper(cursor);
    }

    public static String where(Student student) {
        return StudentTable.Cols.NAME + " = '" + student.getName() + "'";
    }
}
