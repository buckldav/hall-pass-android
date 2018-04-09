package buckley.hallpass.ui.classroom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import buckley.hallpass.model.Model;
import buckley.hallpass.model.Student;
import buckley.hallpass.ui.classroom.StudentFragment.OnListFragmentInteractionListener;

import java.util.List;
import buckley.hallpass.R;

/**
 * @author David Buckley
 * Updated by David 4/3/2018
 *
 * {@link RecyclerView.Adapter} that can display a {@link Student} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyStudentRecyclerViewAdapter extends RecyclerView.Adapter<MyStudentRecyclerViewAdapter.ViewHolder> {

    private final List<Student> students;
    private final OnListFragmentInteractionListener mListener;

    public MyStudentRecyclerViewAdapter(List<Student> students, OnListFragmentInteractionListener listener) {
        this.students = students;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mStudent = students.get(position);
        holder.mNameView.setText(students.get(position).getName());

        // Initialize toggle button, set checked to on if Student.status == OUT
        holder.mToggleButton.setTextOn(Student.STATUS_OUT);
        holder.mToggleButton.setTextOff(Student.STATUS_IN);
        holder.mToggleButton.setChecked(holder.mStudent.getStatus().equals(Student.STATUS_OUT));

        if (holder.mStudent.getName().equals(Model.NO_STUDENTS)) {
            // If no students, hide the toggle button
            holder.mNameView.setEnabled(false);
            holder.mToggleButton.setVisibility(View.GONE);
        } else {
            holder.mNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onListFragmentStudent(holder.mStudent);
                }
            });
            holder.mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.getText().equals(Student.STATUS_OUT)) {
                        // Checking in
                        holder.mStudent.check_in();
                    } else {
                        // Checking out
                        holder.mStudent.check_out();
                    }
                    // Post info via listener
                    mListener.onListFragmentPost(holder.mStudent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final ToggleButton mToggleButton;
        public Student mStudent;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.list_student_name);
            mToggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
