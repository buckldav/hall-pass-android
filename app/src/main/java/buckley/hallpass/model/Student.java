package buckley.hallpass.model;

import android.content.ContentValues;
import android.view.View;

/**
 * @author David Buckley
 * Updated by David 4/3/2018
 *
 * Student contains the name (first and last combined) of the student.
 * Also contains status of Student (checked IN or OUT of class).
 */

public class Student implements Comparable {
    private String name;
    private String status;

    public final static String STATUS_IN = "IN";
    public final static String STATUS_OUT = "OUT";

    public Student(String name) {
        setName(name);
        setStatus(STATUS_IN);
    }

    public Student(String name, String status) {
        setName(name);
        setStatus(status);
    }

    private void setName(String name) {
        // Get rid off hanging spaces
        while (name.endsWith(" ")) {
            name = name.substring(0, name.length() - 1);
        }
        this.name = name;
    }

    private void setStatus(String status) {
        if (!status.equals(STATUS_OUT) && !status.equals(STATUS_IN)) {
            status = STATUS_IN;
        }
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() { return status; }

    public void check_in() { status = STATUS_IN; }
    public void check_out() { status = STATUS_OUT; }

    /**
     * Compares the names of two students. For organizing Students in Sets, etc.
     * @param o Student to be compared
     * @return compareTo of Student's names (sorts alphabetically)
     */
    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(((Student)o).name);
    }

    @Override
    public String toString() {
        return name + ", " + status;
    }
}
