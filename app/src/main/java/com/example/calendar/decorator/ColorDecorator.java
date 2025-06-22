package com.example.calendar.decorator;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ColorDecorator implements DayViewDecorator {

    private final Set<CalendarDay> dates;
    private final Drawable background;

    public ColorDecorator(Set<CalendarDay> dates, Drawable background) {
        this.dates = dates;
        this.background = background;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(background);
    }
}


