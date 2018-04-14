package com.example.dcamo.robatatest.support;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dcamo.robatatest.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    public String finaltime;
    public TextView time;
    public TextView timeSorry;
    @SuppressLint("ValidFragment")
    public DatePicker(TextView textView, TextView textView1){
        this.time = textView;
        this.timeSorry = textView1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // определяем текущую дату
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // создаем DatePickerDialog и возвращаем его
        DatePickerDialog picker = new DatePickerDialog(getActivity(), this,
                year, month, day);

        picker.setTitle(getResources().getString(R.string.choose_date));
        picker.getDatePicker().setMinDate(c.getTimeInMillis());
        return picker;
    }
    @Override
    public void onStart() {
        super.onStart();
        // добавляем кастомный текст для кнопки
        Button nButton =  ((AlertDialog) getDialog())
                .getButton(DialogInterface.BUTTON_POSITIVE);
        nButton.setText("OK");

    }

    public String returnOfTheJedi(){
        return this.finaltime;
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year,
                          int month, int day) {
        try {
            String formattedString = new SimpleDateFormat("EEE, MMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + (month + 1) + "-" + day));
            this.time.setText(formattedString);
//            this.timeSorry.setText(day + "." + month + "." + year);
//            this.finaltime = day + "." + month + "." + year;
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}