package buckley.hallpass.ui.classroom;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Arrays;
import java.util.List;

import buckley.hallpass.R;
import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.Model;
import buckley.hallpass.model.Student;
import buckley.hallpass.ui.sheetsmanager.SheetsManagerActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author David Buckley
 * Updated by David 4/8/2018
 *
 * This activity loads a list of students from GoogleSheets depending on the ClassPeriod.
 * You can add a student to the local data using the "add student" button.
 */
public class ClassroomActivity extends AppCompatActivity
    implements
        StudentFragment.OnListFragmentInteractionListener,
        EasyPermissions.PermissionCallbacks {

    // Keys and constants
    private static final String TAG = "Classroom Activity";
    public static final String KEY_CLASS_PERIOD = "KEY_CLASS_PERIOD";

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    // Global variables for ClassroomActivity
    private ClassPeriod classPeriod = ClassPeriod.A1;
    private String periodString;
    GoogleAccountCredential mCredential;
    private Student studentClicked = null;

    // Views
    private Button addStudent = null;
    private Button sheetsManager = null;
    private Button loadStudents = null;
    private StudentFragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Intent intent = getIntent();
        if (intent != null) {
            // Set enum identifier
            // Set title to the class period
            classPeriod = (ClassPeriod) intent.getSerializableExtra(KEY_CLASS_PERIOD);
            periodString = Model.getInstance().getPeriodInfo(classPeriod).getPeriodStr();
            setTitle(periodString);
        } else {
            // Bad juju
        }

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // Initialize list of students
        Model.getInstance().syncWithStudentDB(classPeriod);

        // Inflate UI
        inflate();
    }

    /**
     * Inflate all the views
     */
    private void inflate() {
        // Inflate buttons
        addStudent = (Button) findViewById(R.id.add_student);
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudentDialog(view.getContext());
            }
        });

        sheetsManager = (Button) findViewById(R.id.button_toSM);
        sheetsManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start SheetsManagerActivity
                Intent intent = new Intent(getApplicationContext(), SheetsManagerActivity.class);
                intent.putExtra(ClassroomActivity.KEY_CLASS_PERIOD, classPeriod);
                startActivity(intent);
            }
        });

        loadStudents = (Button) findViewById(R.id.button_getStudents);
        loadStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStudentListFromApi();
            }
        });

        startFragment();
    }

    /**
     * Refresh the StudentFragment
     */
    public void startFragment() {
        final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        currentFragment = StudentFragment.newInstance(0, classPeriod);
        fm.beginTransaction().replace(R.id.frag_container, currentFragment).commit();
    }

    /**
     * This adds a student strictly to the LOCAL DATA (not GoogleSheets).
     * @param context Where the Dialog is launched from
     */
    private void addStudentDialog(Context context) {
        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Make the dialog
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.add_student))
                .setView(input)
                .setPositiveButton(context.getString(R.string.OK), null) // OK listener is overridden below
                .setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();

        // Set up the OK listener
        // This way allows for input validation
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            Student student = new Student(name);
                            Model.getInstance().addStudent(classPeriod, student);
                            // Update list
                            startFragment();
                            // Dismiss once everything is OK.
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_name_student, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * User can edit student. Changes are saved to the database.
     * @param context
     * @param student
     */
    private void editStudentDialog(Context context, final Student student) {
        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(student.getName());

        // Make the dialog
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.edit_student))
                .setView(input)
                .setPositiveButton(context.getString(R.string.OK), null) // OK listener is overridden below
                .setNeutralButton("REMOVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Remove old student
                        Model.getInstance().removeStudent(classPeriod, student);
                        // Update list
                        startFragment();
                    }
                })
                .setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();

        // Set up the OK listener
        // This way allows for input validation
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            // Remove old student
                            Model.getInstance().removeStudent(classPeriod, student);
                            // Add updated student
                            Student newStudent = new Student(name, student.getStatus());
                            Model.getInstance().addStudent(classPeriod, newStudent);
                            // Update list
                            startFragment();
                            // Dismiss once everything is OK.
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_name_student, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * Right now, prints messages to Logcat
     * @param message
     */
    void makeMessageDialog(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            makeMessageDialog("No network connection available.");
        } else {
            MakeRequestTask task = new MakeRequestTask(
                    this,
                    mCredential,
                    Model.getInstance().getPeriodInfo(classPeriod)
            );
            if (studentClicked != null) {
                task.setStudentPost(studentClicked);
                studentClicked = null;
            }
            task.execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    makeMessageDialog(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ClassroomActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Get student list from Google Drive and store locally.
     */
    private void getStudentListFromApi() {
        // Get request
        studentClicked = null;
        getResultsFromApi();
    }

    /**
     * Sends post request with student information received from RecyclerView list.
     * @param student
     */
    public void onListFragmentPost(Student student) {
        // Update database
        Model.getInstance().updateStatus(classPeriod, student);
        // Post request
        studentClicked = student;
        getResultsFromApi();
    }

    /**
     * Starts EditStudentDialog with Student
     * @param student Student
     */
    @Override
    public void onListFragmentStudent(Student student) {
        editStudentDialog(this, student);
    }
}
