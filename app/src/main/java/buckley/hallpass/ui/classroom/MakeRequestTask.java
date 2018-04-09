package buckley.hallpass.ui.classroom;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import buckley.hallpass.model.Model;
import buckley.hallpass.model.PeriodInfo;
import buckley.hallpass.model.Student;

/**
 * @author David Buckley
 * Updated 4/8/2018
 *
 * An asynchronous task that handles the Google Sheets API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, String> {
    // Keys and constants
    private static String GET_SUCCESS = "Successful download from sheets. Duplicate names skipped.";
    private static String GET_FAILURE = "Error in getting info from spreadsheet.\nCheck URL of spreadsheet and SheetID.";
    private static String POST_FAILURE = "Error in sending info to spreadsheet.\nCheck URL of spreadsheet and SheetID.";

    // Variables
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private ClassroomActivity parentActivity = null;
    private Student student = null;
    private PeriodInfo periodInfo = null;

    MakeRequestTask(ClassroomActivity parentActivity, GoogleAccountCredential credential, PeriodInfo periodInfo) {
        this.parentActivity = parentActivity;
        this.periodInfo = periodInfo;

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    /**
     * If this is called, this request is a POST request.
     * @param student
     */
    public void setStudentPost(Student student) {
        this.student = student;
    }

    /**
     * Background task to call Google Sheets API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            if (student != null) {
                // POST REQUEST
                return sendDataToApi();
            } else {
                // GET REQUEST
                return getDataFromApi();
            }
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Format of Timestamp: "yyyy.MM.dd HH:mm:ss"
     */
    private String getTimestamp() {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
    }

    /**
     * Get student list from spreadsheet via id
     * @return Result of getting data (Success or Error message)
     */
    private String getDataFromApi() {
        String spreadsheetId = periodInfo.getIdGet();
        String sheetId = periodInfo.getIdGetSheet();

        try {
            String range = sheetId + "!a1:E";
            List<String> results = new ArrayList<String>();
            ValueRange response = mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values != null) {
                // results.add("Last Name, First Name");
                for (List row : values) {
                    String name = row.get(1) + " " + row.get(0);
                    results.add(name);
                    // Add student to list
                    Model.getInstance().addStudent(periodInfo.getPeriod(), new Student(name));
                }
            }

            return GET_SUCCESS;
        } catch (Exception e) {
            return GET_FAILURE;
        }
    }

    /**
     * Send student timestamp to spreadsheet via id
     * @return Result of sending data. (Success or Error message)
     */
    private String sendDataToApi() {
        String spreadsheetId = periodInfo.getIdPost();
        String sheetId = periodInfo.getIdPostSheet();

        // Initialize data
        String status = "Status";
        String name = "Name";
        if (student != null) {
            status = student.getStatus();
            name = student.getName();
        }

        // Values to send
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        (Object)getTimestamp(),
                        status,
                        name
                )
        );

        ValueRange valueRange = new ValueRange();
        valueRange.setValues(values);

        try {
            AppendValuesResponse result = this.mService.spreadsheets().values().append(spreadsheetId, sheetId + "!A1:A3", valueRange)
                .setValueInputOption("RAW")
                .execute();

            result.getUpdates().getUpdatedCells();
            return status;
        } catch (Exception e) {
            // Any problems with connection or receiving data
            return POST_FAILURE;
        }
    }

    @Override
    protected void onPreExecute() {
        // Initialize progress (didn't work, not worrying about it)
    }

    @Override
    protected void onPostExecute(String output) {
        if (output == null || output.isEmpty()) {
            parentActivity.makeMessageDialog("No results returned.");
        } else {
            parentActivity.makeMessageDialog(output);
        }

        // Refresh fragment in ClassroomActivity
        parentActivity.startFragment();
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                parentActivity.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                parentActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        ClassroomActivity.REQUEST_AUTHORIZATION);
            } else {
                parentActivity.makeMessageDialog("The following error occurred:\n"
                        + mLastError.getMessage());
            }
        } else {
            parentActivity.makeMessageDialog("Request cancelled.");
        }
    }
}
