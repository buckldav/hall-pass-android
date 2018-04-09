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
 * Use the {@link WriteSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Variables inherited from {@link SheetFragment}
 */
public class WriteSheetFragment extends SheetFragment {
    public WriteSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WriteSheetFragment.
     */
    public static WriteSheetFragment newInstance(ClassPeriod classPeriod) {
        WriteSheetFragment fragment = new WriteSheetFragment();
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

        sheetView.setText(R.string.writeSheet);
        sheetEdit.setHint(R.string.writeHint);

        button_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                periodInfo.setIdPost(sheetEdit.getText().toString());
                periodInfo.setIdPostSheet(sheetIDEdit.getText().toString());
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
        refreshEditText(periodInfo.getIdPost(), periodInfo.getIdPostSheet());
    }
}
