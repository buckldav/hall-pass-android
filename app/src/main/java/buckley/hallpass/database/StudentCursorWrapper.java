package buckley.hallpass.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import buckley.hallpass.model.Student;

/**
 * @author David Buckley
 * Updated 4/7/2018
 *
 * Methods related Student queries from the database.
 */
public class StudentCursorWrapper extends CursorWrapper {
    public StudentCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Student getStudent() {
        String name = getString(getColumnIndex(StudentDB.StudentTable.Cols.NAME));
        String status = getString(getColumnIndex(StudentDB.StudentTable.Cols.STATUS));

        Student student = new Student(name);
        if (status.equals(Student.STATUS_OUT)) {
            student.check_out();
        } else {
            student.check_in();
        }

        return student;
    }
}
