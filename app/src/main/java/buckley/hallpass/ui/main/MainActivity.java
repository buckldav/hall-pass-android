package buckley.hallpass.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import buckley.hallpass.R;
import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.Model;
import buckley.hallpass.model.PeriodInfo;
import buckley.hallpass.ui.classroom.ClassroomActivity;
import buckley.hallpass.ui.sheetsmanager.SheetsManagerActivity;

/**
 * @author David Buckley
 * Updated by David 4/5/2018
 *
 * Sample Google API Sign In code for reference: https://github.com/googlesamples/google-services/blob/master/android/signin/app/
 * This app uses Sheets v4 API from Google.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Model from the database
        Model.initialize(getApplicationContext());

        // Get the buttons working
        initializeButtons();
    }

    /**
     * Initialize each button for each class period.
     */
    private void initializeButtons() {
        new PeriodButton(ClassPeriod.A1, (Button) findViewById(R.id.period1));
        new PeriodButton(ClassPeriod.A2, (Button) findViewById(R.id.period2));
        new PeriodButton(ClassPeriod.A3, (Button) findViewById(R.id.period3));
        new PeriodButton(ClassPeriod.A4, (Button) findViewById(R.id.period4));
        new PeriodButton(ClassPeriod.B1, (Button) findViewById(R.id.period5));
        new PeriodButton(ClassPeriod.B2, (Button) findViewById(R.id.period6));
        new PeriodButton(ClassPeriod.B3, (Button) findViewById(R.id.period7));
        new PeriodButton(ClassPeriod.B4, (Button) findViewById(R.id.period8));
    }

    /**
     * Makes each Button into an application launcher for the ClassPeriod the button is associated with.
     */
    private class PeriodButton {
        final Button button;
        final ClassPeriod period;
        final Context context;

        PeriodButton(final ClassPeriod period, final Button button) {
            this.period = period;
            this.button = button;
            this.context = getApplicationContext();
            this.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // If the post fields (required) are not null, go straight to ClassroomActivity
                    PeriodInfo periodInfo = Model.getInstance().getPeriodInfo(period);
                    if (periodInfo.getIdPost() != null && periodInfo.getIdPostSheet() != null) {
                        startClassroomActivity();
                    } else {
                        startSheetsManagerActivity();
                    }
                }
            });
        }

        private void startSheetsManagerActivity() {
            Intent intent = new Intent(context, SheetsManagerActivity.class);
            intent.putExtra(ClassroomActivity.KEY_CLASS_PERIOD, period);
            startActivity(intent);
        }

        private void startClassroomActivity() {
            Intent intent = new Intent(context, ClassroomActivity.class);
            intent.putExtra(ClassroomActivity.KEY_CLASS_PERIOD, period);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
