package buckley.hallpass.ui.sheetsmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import buckley.hallpass.R;
import buckley.hallpass.model.ClassPeriod;
import buckley.hallpass.model.Model;

/**
 * @author David Buckley
 * Updated 4/8/2018
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Variables inherited from {@link SheetFragment}
 */
public class ReadSheetFragment extends SheetFragment {

    public ReadSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReadSheetFragment.
     */
    public static ReadSheetFragment newInstance(ClassPeriod classPeriod) {
        ReadSheetFragment fragment = new ReadSheetFragment();
        fragment.putArgs(classPeriod);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflateFragment(R.layout.fragment_sheet, inflater, container);

        sheetView.setText(R.string.readSheet);
        sheetEdit.setHint(R.string.readHint);

        button_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                periodInfo.setIdGet(sheetEdit.getText().toString());
                periodInfo.setIdGetSheet(sheetIDEdit.getText().toString());
                // Update Database
                Model.getInstance().updatePeriodInfo(periodInfo);
                // Print success toast
                Toast.makeText(getContext(), "Changes saved.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshEditText(periodInfo.getIdGet(), periodInfo.getIdGetSheet());
    }
}
