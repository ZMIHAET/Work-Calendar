package com.example.calendar;

import static android.content.ContentValues.TAG;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import com.vicmikhailau.maskededittext.MaskedEditText;


public class MainActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView textViewSelectedDate;
    private TextView resultShow;
    private MaskedEditText editTextDateInput;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Устанавливаем локаль на русский
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);

        // Инициализация UI-элементов
        calendarView = findViewById(R.id.calendarView);
        textViewSelectedDate = findViewById(R.id.textView_selected_date);
        resultShow = findViewById(R.id.resultShow);
        editTextDateInput = findViewById(R.id.editText_date_input);

        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        HashMap<String, String> days = new HashMap<>();
        LocalDate startDate = LocalDate.of(2024, 8, 12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        boolean checkWork = false;
        for (int i = 0; i <= 10 * 365; i += 2) {
            LocalDate date = startDate.plusDays(i);
            String formattedDate = date.format(formatter);

            if (!checkWork) {
                days.put(formattedDate, "Не работаю");
                days.put(date.plusDays(1).format(formatter), "Не работаю");
                checkWork = true;
            } else {
                days.put(formattedDate, "Работаю");
                days.put(date.plusDays(1).format(formatter), "Работаю");
                checkWork = false;
            }
        }
        Log.d(TAG, String.valueOf(days));

        // Установка текущей даты
        AtomicReference<SimpleDateFormat> sdf = new AtomicReference<>(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()));
        String currentDate = sdf.get().format(new Date(calendarView.getDate()));
        textViewSelectedDate.setText("Выбранная дата:");
        editTextDateInput.setText(currentDate);

        if ("Работаю".equals(days.get(currentDate))) {
            resultShow.setTextColor(Color.RED);
        } else if ("Не работаю".equals(days.get(currentDate))) {
            resultShow.setTextColor(Color.parseColor("#068c04"));
        }
        resultShow.setText(days.get(currentDate));

        // Обработка выбора даты на календаре
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate;
            if (dayOfMonth < 10 && month + 1 < 10)
                selectedDate = "0" + dayOfMonth + "/0" + (month + 1) + "/" + year;
            else if (dayOfMonth < 10)
                selectedDate = "0" + dayOfMonth + "/" + (month + 1) + "/" + year;
            else if (month + 1 < 10)
                selectedDate = dayOfMonth + "/0" + (month + 1) + "/" + year;
            else
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

            textViewSelectedDate.setText("Выбранная дата:");
            editTextDateInput.setText(selectedDate);

            if ("Работаю".equals(days.get(selectedDate))) {
                resultShow.setTextColor(Color.RED);
            } else if ("Не работаю".equals(days.get(selectedDate))) {
                resultShow.setTextColor(Color.parseColor("#068c04"));
            } else {
                resultShow.setTextColor(Color.BLACK);
            }
            resultShow.setText(days.getOrDefault(selectedDate, "-"));
        });

        // Обработка ручного ввода даты
        editTextDateInput.setOnEditorActionListener((v, actionId, event) -> {
            String input = editTextDateInput.getText().toString();
            textViewSelectedDate.setText("Выбранная дата:");

            if ("Работаю".equals(days.get(input))) {
                resultShow.setTextColor(Color.RED);
            } else if ("Не работаю".equals(days.get(input))) {
                resultShow.setTextColor(Color.parseColor("#068c04"));
            } else {
                resultShow.setTextColor(Color.BLACK);
            }
            resultShow.setText(days.getOrDefault(input, "-"));

            // Установить дату на календаре
            try {
                sdf.set(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()));
                Date date = sdf.get().parse(input);
                if (date != null) {
                    calendarView.setDate(date.getTime(), true, true);
                }
            } catch (Exception e) {
                Log.e(TAG, "Неверный формат даты: " + input);
            }

            // Скрыть клавиатуру и убрать фокус
            editTextDateInput.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editTextDateInput.getWindowToken(), 0);
            }

            return true; // Подтверждаем, что обработали действие
        });



    }
}
