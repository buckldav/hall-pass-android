package buckley.hallpass.ui.sheetsmanager;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import buckley.hallpass.R;
import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.Model;
import buckley.hallpass.model.PeriodInfo;
import buckley.hallpass.ui.classroom.ClassroomActivity;

/**
 * @author David Buckley
 * Updated 4/8/2018
 *
 * Fragment superclass (not for use on its own)
 */
public class SheetFragment extends Fragment {
    // Variables
    protected ClassPeriod classPeriod = null;
    protected PeriodInfo periodInfo = null;

    // Views
    protected TextView sheetView = null;
    protected TextInputEditText sheetEdit = null;
    protected TextView sheetIDView = null;
    protected TextInputEditText sheetIDEdit = null;
    protected Button button_saveChanges = null;

    public SheetFragment() {
        // Required empty public constructor
    }

    // Called in implementing fragments
    protected void putArgs(ClassPeriod classPeriod) {
        Bundle args = new Bundle();
        args.putSerializable(ClassroomActivity.KEY_CLASS_PERIOD, classPeriod);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classPeriod = (ClassPeriod) getArguments().getSerializable(ClassroomActivity.KEY_CLASS_PERIOD);
            periodInfo = Model.getInstance().getPeriodInfo(classPeriod);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sheet, container, false);
        return view;
    }

    protected View inflateFragment(int resId, LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(resId, container, false);

        // Inflate shared layouts
        button_saveChanges = (Button) view.findViewById(R.id.button_saveChanges);
        button_saveChanges.setEnabled(false);

        sheetView = (TextView) view.findViewById(R.id.sheet);
        sheetEdit = (TextInputEditText) view.findViewById(R.id.input_sheet);
        sheetEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_saveChanges.setEnabled(true);
            }
        });

        sheetIDView = (TextView) view.findViewById(R.id.sheetID);
        sheetIDEdit = (TextInputEditText) view.findViewById(R.id.input_sheetID);
        sheetIDEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_saveChanges.setEnabled(true);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get most recently updated version of PeriodInfo
        periodInfo = Model.getInstance().getPeriodInfo(classPeriod);
    }

    /**
     * Called by onResume in children classes with accompanying text as parameters.
     * @param spreadsheet
     * @param sheetID
     */
    protected void refreshEditText(String spreadsheet, String sheetID) {
        // Refresh Text
        sheetEdit.setText(spreadsheet);
        sheetIDEdit.setText(sheetID);
    }
}
