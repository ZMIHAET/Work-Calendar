package com.example.calendar.decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class SelectedDateDecorator implements DayViewDecorator {

    private CalendarDay selectedDate;
    private Date date = null;

    public SelectedDateDecorator(CalendarDay selectedDate) {
        this.selectedDate = selectedDate;
    }

    public SelectedDateDecorator(Date date) {
        this.date = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (date != null)
            selectedDate = CalendarDay.from(date);
        return day.equals(selectedDate);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE)); // Белый цвет текста
    }
}

