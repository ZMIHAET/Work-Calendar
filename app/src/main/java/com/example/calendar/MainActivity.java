package com.example.calendar;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView textViewSelectedDate;
    private TextView resultShow;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        textViewSelectedDate = findViewById(R.id.textView_selected_date);
        resultShow = findViewById(R.id.resultShow);

        HashMap<String, String> days = new HashMap<>();

        LocalDate startDate = LocalDate.of(2024,8,12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        boolean checkWork = false;
        for(int i = 0; i <= 10 * 365; i+=2){
            LocalDate date = startDate.plusDays(i);
            String formattedDate = date.format(formatter);


            if (!checkWork) {
                days.put(formattedDate, "Не работаю");
                days.put(date.plusDays(1).format(formatter), "Не работаю");
                checkWork = true;
            }
            else{
                days.put(formattedDate, "Работаю");
                days.put(date.plusDays(1).format(formatter), "Работаю");
                checkWork = false;
            }
        }
        Log.d(TAG, String.valueOf(days));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date(calendarView.getDate()));
        textViewSelectedDate.setText("Выбранная дата: " + currentDate);

        if (Objects.equals(days.get(currentDate), "Работаю"))
            resultShow.setTextColor(Color.RED);
        else if (Objects.equals(days.get(currentDate), "Не работаю"))
            resultShow.setTextColor(Color.parseColor("#068c04"));

        resultShow.setText(days.get(currentDate));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Обновляем TextView с выбранной датой
                String selectedDate;
                if (dayOfMonth < 10 && month + 1 < 10)
                    selectedDate = "0" + dayOfMonth + "/0" + (month + 1) + "/" + year;
                else if (dayOfMonth < 10)
                    selectedDate = "0" + dayOfMonth + "/" + (month + 1) + "/" + year;
                else if (month + 1 < 10)
                    selectedDate = dayOfMonth + "/0" + (month + 1) + "/" + year;
                else
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;


                if (Objects.equals(days.get(selectedDate), "Работаю"))
                    resultShow.setTextColor(Color.RED);
                else if (Objects.equals(days.get(selectedDate), "Не работаю"))
                    resultShow.setTextColor(Color.parseColor("#068c04"));
                textViewSelectedDate.setText("Выбранная дата: " + selectedDate);
                resultShow.setText(days.getOrDefault(selectedDate, "-"));
            }
        });
    }
}