package com.sprtcoding.tourizal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateSelector;
import com.google.android.material.datepicker.OnSelectionChangedListener;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

@SuppressLint({"ParcelCreator", "RestrictedApi"})
public class CustomDateSelector implements DateSelector<Long> {
    private final List<Long> bookedDates;

    public CustomDateSelector(List<Long> bookedDates) {
        this.bookedDates = bookedDates;
    }

    @Nullable
    @Override
    public Long getSelection() {
        if (!bookedDates.isEmpty()) {
            return bookedDates.get(0); // Return the first booked date, or null if none
        } else {
            return null;
        }
    }

    @Override
    public boolean isSelectionComplete() {
        return true;
    }

    @Override
    public void setSelection(@NonNull Long selection) {

    }

    @Override
    public void select(long selection) {

    }

    @NonNull
    @Override
    public Collection<Long> getSelectedDays() {
        return bookedDates;
    }

    @NonNull
    @Override
    public Collection<Pair<Long, Long>> getSelectedRanges() {
        return null;
    }

    @NonNull
    @Override
    public String getSelectionDisplayString(Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getSelectionContentDescription(@NonNull Context context) {
        return null;
    }

    @Nullable
    @Override
    public String getError() {
        return null;
    }

    @Override
    public int getDefaultTitleResId() {
        return 0;
    }

    @Override
    public int getDefaultThemeResId(Context context) {
        return 0;
    }

    @Override
    public void setTextInputFormat(@Nullable SimpleDateFormat format) {

    }

    public boolean isCalendarDaysOfWeekDisabled() {
        return false; // Set to true if you want to disable specific days of the week
    }

    public boolean isDateSelectable(Long date) {
        return !bookedDates.contains(date); // Disable booked dates
    }

    @NonNull
    @Override
    public View onCreateTextInputView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle, @NonNull CalendarConstraints constraints, @NonNull OnSelectionChangedListener<Long> listener) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {

    }
}
