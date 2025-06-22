package com.example.calendar;

import static android.content.ContentValues.TAG;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.example.calendar.decorator.ColorDecorator;
import com.example.calendar.decorator.SelectedDateDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.vicmikhailau.maskededittext.MaskedEditText;

import com.prolificinteractive.materialcalendarview.CalendarDay;


public class MainActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
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

        // Даты "работаю"
        Set<CalendarDay> workDays = new HashSet<>();
        Set<CalendarDay> freeDays = new HashSet<>();

        for (Map.Entry<String, String> entry : days.entrySet()) {
            Date date;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(entry.getKey());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            CalendarDay calendarDay = CalendarDay.from(cal);

            if ("Работаю".equals(entry.getValue())) {
                workDays.add(calendarDay);
            } else if ("Не работаю".equals(entry.getValue())) {
                freeDays.add(calendarDay);
            }
        }

        // Декораторы
        calendarView.addDecorator(new ColorDecorator(workDays, ContextCompat.getDrawable(this, R.drawable.date_red_border)));
        calendarView.addDecorator(new ColorDecorator(freeDays, ContextCompat.getDrawable(this, R.drawable.date_green_border)));


       // Установка текущей даты
        AtomicReference<SimpleDateFormat> sdf = new AtomicReference<>(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()));
        String currentDate = sdf.get().format(new Date());
        final Date[] date = new Date[1];
        try {
            date[0] = sdf.get().parse(currentDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        final SelectedDateDecorator[] selectedDecorator = {null};

        calendarView.setDateSelected(date[0], true);
        textViewSelectedDate.setText("Выбранная дата:");
        editTextDateInput.setText(currentDate);

        // Создаём и добавляем новый декоратор
        final SelectedDateDecorator[] newDecorator = {new SelectedDateDecorator(date[0])};
        calendarView.addDecorator(newDecorator[0]);
        selectedDecorator[0] = newDecorator[0]; // Сохраняем для будущего удаления


        if ("Работаю".equals(days.get(currentDate))) {
            resultShow.setTextColor(Color.RED);
        } else if ("Не работаю".equals(days.get(currentDate))) {
            resultShow.setTextColor(Color.parseColor("#068c04"));
        }
        resultShow.setText(days.get(currentDate));

        // Обработка выбора даты на календаре
        calendarView.setOnDateChangedListener((widget, Date, selected) -> {
            // Удаляем предыдущий декоратор, если был
            if (selectedDecorator[0] != null) {
                calendarView.removeDecorator(selectedDecorator[0]);
            }

            // Создаём и добавляем новый декоратор
            newDecorator[0] = new SelectedDateDecorator(Date);
            calendarView.addDecorator(newDecorator[0]);
            selectedDecorator[0] = newDecorator[0]; // Сохраняем для будущего удаления

            // date — объект CalendarDay, откуда можно получить год, месяц и день
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                    Date.getDay(), Date.getMonth() + 1, Date.getYear());

            textViewSelectedDate.setText("Выбранная дата:");
            editTextDateInput.setText(selectedDate);

            String status = days.get(selectedDate);
            if ("Работаю".equals(status)) {
                resultShow.setTextColor(Color.RED);
            } else if ("Не работаю".equals(status)) {
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
                Toast.makeText(this, "Неверный формат даты: \"дд/мм/гггг\" ", Toast.LENGTH_SHORT).show();
                resultShow.setTextColor(Color.BLACK);
                resultShow.setText(days.getOrDefault(input, "-"));
                return false;
            }
            resultShow.setText(days.getOrDefault(input, "-"));

            // Установить дату на календаре
            try {
                sdf.set(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()));
                date[0] = sdf.get().parse(input);
                if (date[0] != null) {
                    calendarView.setCurrentDate(CalendarDay.from(date[0]), true);
                    calendarView.setDateSelected(CalendarDay.from(date[0]), true);
                }
                if (selectedDecorator[0] != null) {
                    calendarView.removeDecorator(selectedDecorator[0]);
                }

                // Создаём и добавляем новый декоратор
                newDecorator[0] = new SelectedDateDecorator(date[0]);
                calendarView.addDecorator(newDecorator[0]);
                selectedDecorator[0] = newDecorator[0]; // Сохраняем для будущего удаления

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
