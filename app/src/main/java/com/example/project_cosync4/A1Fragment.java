package com.example.project_cosync4;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class A1Fragment extends Fragment {

    public static class Schedule {
        String subject, date;
        int startTime, endTime;

        public Schedule(String subject, String date, int startTime, int endTime) {
            this.subject = subject; this.date = date;
            this.startTime = startTime; this.endTime = endTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Schedule)) return false;
            Schedule s = (Schedule) o;
            return startTime == s.startTime && endTime == s.endTime && subject.equals(s.subject) && date.equals(s.date);
        }
        @Override
        public int hashCode() { return Objects.hash(subject, date, startTime, endTime); }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_add).setOnClickListener(v -> showAddScheduleDialog());
        refreshTimetable();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTimetable();
    }

    private void refreshTimetable() {
        View view = getView();
        if (view != null) renderTimetable(view, loadScheduleData());
    }

    private void renderTimetable(View view, List<Schedule> schedules) {
        int maxEndTime = 15;
        for (Schedule s : schedules) if (s.endTime > maxEndTime) maxEndTime = s.endTime;

        LinearLayout timeColumn = view.findViewById(R.id.col_time);
        if (timeColumn == null) return;
        timeColumn.removeAllViews();
        for (int hour = 9; hour < maxEndTime; hour++) {
            timeColumn.addView(createBlock(String.valueOf(hour), 1, "#FFFFFF", null));
        }

        int[] columnIds = {R.id.col_mon, R.id.col_tue, R.id.col_wed, R.id.col_thu, R.id.col_fri, R.id.col_sat, R.id.col_sun};
        String[] dateForColumn = {"260525", "260526", "260527", "260528", "260529", "260530", "260531"};

        for (int i = 0; i < 7; i++) {
            LinearLayout column = view.findViewById(columnIds[i]);
            if (column == null) continue;
            column.removeAllViews();
            String targetDate = dateForColumn[i];
            int currentHour = 9;

            while (currentHour < maxEndTime) {
                Schedule matched = null;
                for (Schedule s : schedules) if (s.date.equals(targetDate) && s.startTime == currentHour) { matched = s; break; }

                if (matched != null) {
                    column.addView(createBlock(matched.subject, matched.endTime - matched.startTime, "#D5E8FF", matched));
                    currentHour = matched.endTime;
                } else {
                    column.addView(createBlock("", 1, "#FFFFFF", null));
                    currentHour++;
                }
            }
        }
    }

    private TextView createBlock(String text, int duration, String colorHex, Schedule schedule) {
        TextView textView = new TextView(getContext());
        float density = getResources().getDisplayMetrics().density;
        int height = (int) (((60 * density) + 2) * duration - 2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.setMargins(1, 1, 1, 1);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(Color.parseColor(colorHex));
        if (schedule != null) textView.setOnClickListener(v -> showEditDeleteDialog(schedule));
        return textView;
    }

    private void showAddScheduleDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);
        Spinner daySpinner = v.findViewById(R.id.daySpinner);
        daySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"월","화","수","목","금","토","일"}));

        new AlertDialog.Builder(getContext()).setTitle("일정 추가").setView(v)
                .setPositiveButton("제출", (d, w) -> {
                    try {
                        String sub = ((EditText)v.findViewById(R.id.subjectInput)).getText().toString();
                        int st = Integer.parseInt(((EditText)v.findViewById(R.id.startInput)).getText().toString());
                        int en = Integer.parseInt(((EditText)v.findViewById(R.id.endInput)).getText().toString());

                        if (st >= en || st < 9 || en > 24) { Toast.makeText(getContext(), "시간 범위를 확인하세요!", Toast.LENGTH_SHORT).show(); return; }

                        String date = "2605" + (25 + daySpinner.getSelectedItemPosition());
                        List<Schedule> list = loadScheduleData();
                        if (isOverlapping(list, date, st, en, null)) Toast.makeText(getContext(), "시간이 겹칩니다!", Toast.LENGTH_SHORT).show();
                        else { list.add(new Schedule(sub, date, st, en)); saveScheduleList(list); refreshTimetable(); d.dismiss(); }
                    } catch (Exception e) { Toast.makeText(getContext(), "입력 오류", Toast.LENGTH_SHORT).show(); }
                }).show();
    }

    private void showEditDeleteDialog(Schedule schedule) {
        new AlertDialog.Builder(getContext()).setTitle("일정 관리")
                .setItems(new String[]{"수정", "삭제"}, (d, which) -> {
                    if (which == 0) showEditDialog(schedule); else deleteSchedule(schedule);
                }).show();
    }

    private void showEditDialog(Schedule schedule) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);
        ((EditText)v.findViewById(R.id.subjectInput)).setText(schedule.subject);
        ((EditText)v.findViewById(R.id.startInput)).setText(String.valueOf(schedule.startTime));
        ((EditText)v.findViewById(R.id.endInput)).setText(String.valueOf(schedule.endTime));

        new AlertDialog.Builder(getContext()).setTitle("일정 수정").setView(v)
                .setPositiveButton("수정", (d, w) -> {
                    try {
                        String sub = ((EditText)v.findViewById(R.id.subjectInput)).getText().toString();
                        int st = Integer.parseInt(((EditText)v.findViewById(R.id.startInput)).getText().toString());
                        int en = Integer.parseInt(((EditText)v.findViewById(R.id.endInput)).getText().toString());

                        if (st >= en || st < 9 || en > 24) { Toast.makeText(getContext(), "시간 범위를 확인하세요!", Toast.LENGTH_SHORT).show(); return; }

                        List<Schedule> list = loadScheduleData();
                        if (isOverlapping(list, schedule.date, st, en, schedule)) Toast.makeText(getContext(), "시간이 겹칩니다!", Toast.LENGTH_SHORT).show();
                        else { list.remove(schedule); list.add(new Schedule(sub, schedule.date, st, en)); saveScheduleList(list); refreshTimetable(); d.dismiss(); }
                    } catch (Exception e) { Toast.makeText(getContext(), "입력 오류", Toast.LENGTH_SHORT).show(); }
                }).show();
    }

    private void deleteSchedule(Schedule schedule) {
        List<Schedule> list = loadScheduleData();
        list.remove(schedule);
        saveScheduleList(list);
        refreshTimetable();
    }

    private void saveScheduleList(List<Schedule> list) {
        getActivity().getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE).edit()
                .putString("my_schedule_key", new Gson().toJson(list)).apply();
    }

    private List<Schedule> loadScheduleData() {
        String json = getActivity().getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE)
                .getString("my_schedule_key", null);
        return json == null ? new ArrayList<>() : new Gson().fromJson(json, new TypeToken<List<Schedule>>(){}.getType());
    }

    private boolean isOverlapping(List<Schedule> list, String date, int start, int end, Schedule ignore) {
        for (Schedule s : list) {
            if (ignore != null && s.equals(ignore)) continue;
            if (s.date.equals(date) && start < s.endTime && end > s.startTime) return true;
        }
        return false;
    }
}