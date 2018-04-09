package buckley.hallpass.model;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import buckley.hallpass.R;
import buckley.hallpass.database.DatabaseHelper;
import buckley.hallpass.database.PeriodCursorWrapper;
import buckley.hallpass.database.PeriodDB;
import buckley.hallpass.database.StudentCursorWrapper;
import buckley.hallpass.database.StudentDB;

/**
 * @author David Buckley
 * Updated by David 4/3/2018
 *
 * Contains the students mapped to their class period and info (namely sheet id's)
 * related to each class period. Local data retrieved from/sent to local database.
 */

public class Model {
    private static Model ourInstance = null;
    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {}

    public static void initialize(Context context) {
        ourInstance = new Model();
        ourInstance.initializeDatabase(context);
        ourInstance.initializeClassPeriods(context);
        ourInstance.initializeStudents();
    }

    // ************* DATABASE ***************
    private SQLiteDatabase database = null;
    private void initializeDatabase(Context context) {
        database = new DatabaseHelper(context).getWritableDatabase();
    }


    // ************* CLASS PERIODS **********

    private HashMap<ClassPeriod, PeriodInfo> classPeriodMap;
    public PeriodInfo getPeriodInfo(ClassPeriod classPeriod) {
        return classPeriodMap.get(classPeriod);
    }

    private void initializeClassPeriods(Context context) {
        classPeriodMap = new HashMap<>();
        classPeriodMap.put(ClassPeriod.A1, new PeriodInfo(ClassPeriod.A1, context.getString(R.string.A1)));
        classPeriodMap.put(ClassPeriod.A2, new PeriodInfo(ClassPeriod.A2, context.getString(R.string.A2)));
        classPeriodMap.put(ClassPeriod.A3, new PeriodInfo(ClassPeriod.A3, context.getString(R.string.A3)));
        classPeriodMap.put(ClassPeriod.A4, new PeriodInfo(ClassPeriod.A4, context.getString(R.string.A4)));
        classPeriodMap.put(ClassPeriod.B1, new PeriodInfo(ClassPeriod.B1, context.getString(R.string.B1)));
        classPeriodMap.put(ClassPeriod.B2, new PeriodInfo(ClassPeriod.B2, context.getString(R.string.B2)));
        classPeriodMap.put(ClassPeriod.B3, new PeriodInfo(ClassPeriod.B3, context.getString(R.string.B3)));
        classPeriodMap.put(ClassPeriod.B4, new PeriodInfo(ClassPeriod.B4, context.getString(R.string.B4)));

        try {
            // Empty database, insert new PeriodInfo objects
            for (PeriodInfo periodInfo : classPeriodMap.values()) {
                addPeriodInfoDB(periodInfo);
            }
        } finally {
            // Load period info already in database
            getPeriodInfoDB();
        }

    }

    /**
     * Loads PeriodInfo from database to classPeriodMap to be accessed by ClassroomActivity
     */
    private void getPeriodInfoDB() {
        PeriodCursorWrapper periodCursorWrapper = PeriodDB.getAllPeriodInfos(database);

        try {
            periodCursorWrapper.moveToFirst();
            while (!periodCursorWrapper.isAfterLast()) {
                PeriodInfo periodInfo = periodCursorWrapper.getPeriodInfo();
                classPeriodMap.remove(periodInfo.getPeriod());
                classPeriodMap.put(periodInfo.getPeriod(), periodInfo);
                periodCursorWrapper.moveToNext();
            }
        } finally {
            periodCursorWrapper.close();
        }
    }

    /**
     * Adds PeriodInfo object to database
     * @param periodInfo
     */
    private void addPeriodInfoDB(PeriodInfo periodInfo) {
        database.insert(PeriodDB.PeriodTable.NAME, null, PeriodDB.getContentValues(periodInfo));
    }

    /**
     * Updates existing PeriodInfo object in database
     * @param periodInfo
     */
    public void updatePeriodInfo(PeriodInfo periodInfo) {
        database.update(
                PeriodDB.PeriodTable.NAME,
                PeriodDB.getContentValues(periodInfo),
                PeriodDB.where(periodInfo),
                null
        );
    }

    // ************* STUDENTS ***************

    /**
     * Map of a String (ClassPeriod) to a Set of Students
     */
    private TreeMap<ClassPeriod, TreeSet<Student>> students;

    private void initializeStudents() {
        students = new TreeMap<>();
    }

    public static final String NO_STUDENTS = "No Students";

    /**
     * @param classPeriod
     * @return List of Students related to classPeriod
     */
    public ArrayList<Student> getStudentList(ClassPeriod classPeriod) {
        TreeSet<Student> studentSet = students.get(classPeriod);
        if (studentSet == null) {
            studentSet = new TreeSet<>();
            studentSet.add(new Student(NO_STUDENTS));
        }
        return new ArrayList<>(Arrays.asList(studentSet.toArray(new Student[studentSet.size()])));
    }

    /**
     * When student's toggleButton is clicked and their status is updated, change in database.
     * @param classPeriod
     * @param student
     */
    public void updateStatus(ClassPeriod classPeriod, Student student) {
        database.update(
                StudentDB.StudentTable.getTitle(classPeriod),
                StudentDB.getContentValues(student),
                StudentDB.where(student),
                null
        );
    }

    /**
     * Removes student from Map of students and table in database related to classPeriod.
     * @param classPeriod student table
     * @param student to be removed
     */
    public void removeStudent(ClassPeriod classPeriod, Student student) {
        TreeSet<Student> studentSet = students.remove(classPeriod);
        if (studentSet == null) {
            studentSet = new TreeSet<>();
        }

        // Only add to database if the student was added to the set.
        try {
            if (studentSet.remove(student)) {
                database.delete(StudentDB.StudentTable.getTitle(classPeriod), StudentDB.where(student), null);
            }
        } catch (SQLiteConstraintException e) {
            // Do nothing
        }

        // Put back in model
        students.put(classPeriod, studentSet);
    }

    /**
     * Adds student to Map of students and table in database related to classPeriod.
     * @param classPeriod student table
     * @param student to be added
     */
    public void addStudent(ClassPeriod classPeriod, Student student) {
        TreeSet<Student> studentSet = students.remove(classPeriod);
        if (studentSet == null) {
            studentSet = new TreeSet<>();
        }

        // Only add to database if the student was added to the set.
        try {
            if (studentSet.add(student)) {
                database.insert(StudentDB.StudentTable.getTitle(classPeriod), null, StudentDB.getContentValues(student));
            }
        } catch (SQLiteConstraintException e) {
            // Do nothing
        }

        // Put back in model
        students.put(classPeriod, studentSet);
    }

    /**
     * Called when ClassroomActivity is opened to put student information in the
     * {@link buckley.hallpass.ui.classroom.MyStudentRecyclerViewAdapter}
     * @param classPeriod to find associated Student table.
     */
    public void syncWithStudentDB(ClassPeriod classPeriod) {
        StudentCursorWrapper studentCursorWrapper = StudentDB.getAllStudents(classPeriod, database);
        try {
            studentCursorWrapper.moveToFirst();
            while (!studentCursorWrapper.isAfterLast()) {
                addStudent(classPeriod, studentCursorWrapper.getStudent());
                studentCursorWrapper.moveToNext();
            }
        } finally {
            studentCursorWrapper.close();
        }
    }
}
