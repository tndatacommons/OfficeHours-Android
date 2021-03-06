package org.tndata.officehours.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import org.tndata.officehours.R;
import org.tndata.officehours.databinding.ActivityCourseEditorBinding;
import org.tndata.officehours.model.Course;
import org.tndata.officehours.model.ResultSet;
import org.tndata.officehours.parser.Parser;
import org.tndata.officehours.util.API;

import java.util.Calendar;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity used to create or edit a course.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CourseEditorActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                DatePickerDialog.OnDateSetListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    private static final String TAG = "CourseEditorActivity";
    
    private static final int TIME_SLOT_PICKER_RC = 6286;

    public static final String COURSE_KEY = "org.tndata.officehours.CourseEditor.Course";


    private ActivityCourseEditorBinding binding;

    private Course course;

    private String meetingTime;
    //private String lastMeetingDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_editor);

        setSupportActionBar(binding.courseEditorToolbar.toolbar);

        binding.courseEditorMeetingTime.setOnClickListener(this);
        binding.courseEditorLastMeetingDate.setOnClickListener(this);
        binding.courseEditorDone.setOnClickListener(this);

        course = getIntent().getParcelableExtra(COURSE_KEY);
        if (course == null){
            //If no course was delivered, initialize everything to zero
            meetingTime = "";
            //lastMeetingDate = "";

            binding.courseEditorToolbar.toolbar.setTitle(R.string.course_editor_label_new);
        }
        else{
            //If a course was delivered, populate all fields
            meetingTime = course.getMeetingTime();
            //lastMeetingDate = course.getLastMeetingDate();

            //binding.courseEditorCode.setText(course.getCode());
            binding.courseEditorName.setText(course.getName());
            binding.courseEditorLocation.setText(course.getLocation());
            binding.courseEditorMeetingTime.setText(course.getFormattedMeetingTime());
            //binding.courseEditorLastMeetingDate.setText(lastMeetingDate);

            binding.courseEditorToolbar.toolbar.setTitle(R.string.course_editor_label_edit);
            binding.courseEditorDone.setText(R.string.course_editor_save);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.course_editor_meeting_time:
                Intent slotPicker;
                if (meetingTime == null || meetingTime.isEmpty()){
                    slotPicker = TimeSlotPickerActivity.getIntent(this, true, true, false);
                }
                else{
                    slotPicker = TimeSlotPickerActivity.getIntent(this, meetingTime, true, true, false);
                }
                startActivityForResult(slotPicker, TIME_SLOT_PICKER_RC);
                break;

            case R.id.course_editor_last_meeting_date:
                //pickLastMeetingDate();
                break;

            case R.id.course_editor_done:
                if (areFieldsSet()){
                    binding.courseEditorError.setVisibility(View.GONE);
                    saveCourse();
                }
                else{
                    binding.courseEditorError.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == TIME_SLOT_PICKER_RC && resultCode == RESULT_OK){
            meetingTime = data.getStringExtra(TimeSlotPickerActivity.RESULT_KEY);
            String display = TimeSlotPickerActivity.get12HourFormattedString(meetingTime, false);
            binding.courseEditorMeetingTime.setText(display);
        }
    }

    /**
     * Fires the last meeting date picker.
     */
    /*private void pickLastMeetingDate(){
        int year, month, day;
        if (lastMeetingDate.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else{
            String date[] = lastMeetingDate.split("/");
            year = Integer.valueOf(date[2]);
            month = Integer.valueOf(date[0])-1; //Months start at 0
            day = Integer.valueOf(date[1]);
        }

        new DatePickerDialog(this, this, year, month, day).show();
    }*/

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth){
        //lastMeetingDate = (monthOfYear+1) + "/" + dayOfMonth + "/" + year;
        //binding.courseEditorLastMeetingDate.setText(lastMeetingDate);
    }

    /**
     * Checks for forms correctness. If it finds empty fields it will set the relevant error
     * string in the error feedback TextView and return false.
     *
     * @return true if the form is filled properly, false otherwise.
     */
    private boolean areFieldsSet(){
        /*if (binding.courseEditorCode.getText().toString().trim().isEmpty()){
            binding.courseEditorError.setText(R.string.course_editor_error_code);
            return false;
        }*/
        if (binding.courseEditorName.getText().toString().trim().isEmpty()){
            binding.courseEditorError.setText(R.string.course_editor_error_name);
            return false;
        }
        else if (binding.courseEditorLocation.getText().toString().trim().isEmpty()){
            binding.courseEditorError.setText(R.string.course_editor_error_location);
            return false;
        }
        else if (meetingTime == null || meetingTime.isEmpty()){
            binding.courseEditorError.setText(R.string.course_editor_error_meeting_time);
            return false;
        }
        /*else if (lastMeetingDate.isEmpty()){
            binding.courseEditorError.setText(R.string.course_editor_error_last_meeting_date);
            return false;
        }*/
        return true;
    }

    /**
     * Triggers the process that saves a course, new or edited.
     */
    private void saveCourse(){
        setFormState(true);
        if (course == null){
            //New
            course = new Course(
                    //binding.courseEditorCode.getText().toString().trim(),
                    binding.courseEditorName.getText().toString().trim(),
                    binding.courseEditorLocation.getText().toString().trim(),
                    meetingTime//,
                    //lastMeetingDate//,
                    //((OfficeHoursApp)getApplication()).getUser().getName()
            );
            HttpRequest.post(this, API.URL.courses(), API.BODY.postPutCourse(course));
        }
        else{
            //course.setCode(binding.courseEditorCode.getText().toString().trim());
            course.setName(binding.courseEditorName.getText().toString().trim());
            course.setMeetingTime(meetingTime);
            String formatted = TimeSlotPickerActivity.get12HourFormattedString(meetingTime, false);
            course.setFormattedMeetingTime(formatted);
            //course.setLastMeetingDate(lastMeetingDate);
            HttpRequest.put(null, API.URL.courses(course.getId()), API.BODY.postPutCourse(course));
            setResult(RESULT_OK, new Intent().putExtra(COURSE_KEY, course));
            finish();
        }
    }

    /**
     * Changes the state of the form, enabling, disabling, hiding, or showing relevant fields.
     *
     * @param loading true if the activity is in load state, false otherwise.
     */
    private void setFormState(boolean loading){
        binding.courseEditorCode.setEnabled(!loading);
        binding.courseEditorName.setEnabled(!loading);
        binding.courseEditorLocation.setEnabled(!loading);
        binding.courseEditorMeetingTime.setEnabled(!loading);
        binding.courseEditorLastMeetingDate.setEnabled(!loading);
        if (loading){
            binding.courseEditorProgress.setVisibility(View.VISIBLE);
        }
        else{
            binding.courseEditorProgress.setVisibility(View.GONE);
        }
        binding.courseEditorDone.setEnabled(!loading);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        Log.d(TAG, result);
        Parser.parse(result, Course.class, this);
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        Log.d(TAG, error.toString());
        Log.d(TAG, error.getMessage());
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){
        if (result instanceof Course){
            Course course = (Course)result;
            String formatted = TimeSlotPickerActivity.get12HourFormattedString(meetingTime, false);
            course.setFormattedMeetingTime(formatted);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof Course){
            setResult(RESULT_OK, new Intent().putExtra(COURSE_KEY, (Course)result));
            finish();
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }
}
