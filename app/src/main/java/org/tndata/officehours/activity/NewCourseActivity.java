package org.tndata.officehours.activity;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import org.tndata.officehours.R;
import org.tndata.officehours.databinding.ActivityNewCourseBinding;

import java.util.Calendar;


/**
 * Activity used to create a course.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class NewCourseActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{
    private ActivityNewCourseBinding binding;

    private int expirationYear;
    private int expirationMonth;
    private int expirationDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_course);

        setSupportActionBar(binding.newCourseToolbar.toolbar);

        binding.newCourseTime.setOnClickListener(this);
        binding.newCourseExpiration.setOnClickListener(this);

        expirationYear = -1;
        expirationMonth = -1;
        expirationDay = -1;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.new_course_time:
                startActivity(RangeRecurrencePickerActivity.getIntent(this, true));
                break;

            case R.id.new_course_expiration:
                pickExpiration();
                break;
        }
    }

    private void pickExpiration(){
        Calendar calendar = Calendar.getInstance();
        int year = expirationYear != -1 ? expirationYear : calendar.get(Calendar.YEAR);
        int month = expirationMonth != -1 ? expirationMonth : calendar.get(Calendar.MONTH);
        int day = expirationDay != -1 ? expirationDay : calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, this, year, month, day).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth){
        expirationYear = year;
        expirationMonth = monthOfYear;
        expirationDay = dayOfMonth;

        String expiration = (expirationMonth+1) + "/" + expirationDay + "/" + expirationYear;
        binding.newCourseExpiration.setText(expiration);
    }
}
