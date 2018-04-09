package buckley.hallpass.ui.sheetsmanager;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import buckley.hallpass.R;
import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.Model;
import buckley.hallpass.model.PeriodInfo;
import buckley.hallpass.ui.main.MainActivity;
import buckley.hallpass.ui.classroom.ClassroomActivity;

/**
 * @author David Buckley
 * Updated 4/8/2018
 *
 * This is where the client stores the id's of the Google Sheets used by the app for reading and
 * writing data. The "WRITE" spreadsheet (post) is required for minimum functionality.
 *
 * Future updates: Optimize getting id's directly from Google Drive. Share id's between classes.
 */
public class SheetsManagerActivity extends AppCompatActivity {
    // Global variables
    private ClassPeriod classPeriod = null;
    private PeriodInfo periodInfo = null;
    private FragmentPagerAdapter fragmentPagerAdapter = null;

    // Views
    private Button button_toMain = null;
    private Button button_toClassroom = null;
    private ViewPager viewPager = null;
    private TabLayout tabLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheets_manager);
        try {
            // Get things from the intent
            Intent intent = getIntent();
            classPeriod = (ClassPeriod) intent.getSerializableExtra(ClassroomActivity.KEY_CLASS_PERIOD);
            periodInfo = Model.getInstance().getPeriodInfo(classPeriod);
            setTitle(getString(R.string.sm_title) + periodInfo.getPeriodStr());

            // Inflate views
            inflate();
        } catch (Exception e) {
            Log.e("Sheets Manager onCreate", e.getMessage());
        }
    }

    /**
     * Inflate the views and set onClickListeners
     */
    private void inflate() {
        button_toMain = (Button) findViewById(R.id.button_toMain);
        button_toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start main activity
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        button_toClassroom = (Button) findViewById(R.id.button_toClassroom);
        button_toClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start classroom activity
                Intent intent = new Intent(view.getContext(), ClassroomActivity.class);
                intent.putExtra(ClassroomActivity.KEY_CLASS_PERIOD, classPeriod);
                startActivity(intent);
            }
        });

        // ViewPager with tabs
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                // get the fragment
                if (position == 0) {
                    return WriteSheetFragment.newInstance(classPeriod);
                } else {
                    return ReadSheetFragment.newInstance(classPeriod);
                }
            }

            @Override
            public int getCount() {
                // Show 2 total pages.
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.writeTab);
                    case 1:
                        return getString(R.string.readTab);
                }
                return null;
            }
        };
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);

        // TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
