package com.example.project_cosync4;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class A2Fragment extends Fragment {

    public static class Event {
        public String subject;
        public String date;
        public int startTime;
        public int endTime;
        public String groupId;
        public String color;
        public Event(String subject, String date, int startTime, int endTime, String groupId, String color) {
            this.subject = subject;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.groupId = groupId;
            this.color = color;
        }
    }

    private static final String GROUP_ME = "내 시간표";
    private static final String GROUP_T1 = "1팀";
    private static final String GROUP_T2 = "2팀";
    private static final String COLOR_ME = "#D5E8FF";
    private static final String COLOR_T1 = "#FFD9B3";
    private static final String COLOR_T2 = "#C8E6C9";
    private static final String COLOR_OFF = "#E0E0E0";
    private static final String COLOR_MODE_ON = "#444444";
    private static final String COLOR_MODE_OFF = "#E0E0E0";

    private static final String MODE_MONTH = "month";
    private static final String MODE_WEEK = "week";

    private final Set<String> selectedGroups = new HashSet<>(Arrays.asList(GROUP_ME));
    private Calendar displayMonth;
    private Calendar weekStart;
    private String viewMode = MODE_MONTH;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayMonth = Calendar.getInstance();
        displayMonth.set(2026, Calendar.MAY, 1, 0, 0, 0);
        weekStart = Calendar.getInstance();
        weekStart.set(2026, Calendar.MAY, 25, 0, 0, 0);

        view.findViewById(R.id.toggle_me).setOnClickListener(v -> toggleGroup(view, GROUP_ME));
        view.findViewById(R.id.toggle_team1).setOnClickListener(v -> toggleGroup(view, GROUP_T1));
        view.findViewById(R.id.toggle_team2).setOnClickListener(v -> toggleGroup(view, GROUP_T2));

        view.findViewById(R.id.btn_mode_month).setOnClickListener(v -> setViewMode(view, MODE_MONTH));
        view.findViewById(R.id.btn_mode_week).setOnClickListener(v -> setViewMode(view, MODE_WEEK));

        view.findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            displayMonth.add(Calendar.MONTH, -1);
            renderAll(view);
        });
        view.findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            displayMonth.add(Calendar.MONTH, 1);
            renderAll(view);
        });
        view.findViewById(R.id.btn_prev_week).setOnClickListener(v -> {
            weekStart.add(Calendar.DATE, -7);
            renderAll(view);
        });
        view.findViewById(R.id.btn_next_week).setOnClickListener(v -> {
            weekStart.add(Calendar.DATE, 7);
            renderAll(view);
        });

        renderAll(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) renderAll(getView());
    }

    private void toggleGroup(View root, String group) {
        if (selectedGroups.contains(group)) selectedGroups.remove(group);
        else selectedGroups.add(group);
        renderAll(root);
    }

    private void setViewMode(View root, String mode) {
        viewMode = mode;
        renderAll(root);
    }

    private void renderAll(View root) {
        updateToggleStyles(root);
        updateModeStyles(root);
        updateMonthLabel(root);
        updateWeekLabel(root);
        root.findViewById(R.id.monthSection).setVisibility(viewMode.equals(MODE_MONTH) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.weekSection).setVisibility(viewMode.equals(MODE_WEEK) ? View.VISIBLE : View.GONE);
        if (viewMode.equals(MODE_MONTH)) renderMonthCalendar(root);
        else renderWeekSchedule(root);
    }

    private void updateToggleStyles(View root) {
        styleToggle(root.findViewById(R.id.toggle_me), GROUP_ME, COLOR_ME);
        styleToggle(root.findViewById(R.id.toggle_team1), GROUP_T1, COLOR_T1);
        styleToggle(root.findViewById(R.id.toggle_team2), GROUP_T2, COLOR_T2);
    }

    private void styleToggle(Button btn, String group, String onColor) {
        boolean on = selectedGroups.contains(group);
        btn.setBackgroundColor(Color.parseColor(on ? onColor : COLOR_OFF));
        btn.setTextColor(on ? Color.BLACK : Color.parseColor("#888888"));
    }

    private void updateModeStyles(View root) {
        Button bM = root.findViewById(R.id.btn_mode_month);
        Button bW = root.findViewById(R.id.btn_mode_week);
        boolean monthOn = viewMode.equals(MODE_MONTH);
        bM.setBackgroundColor(Color.parseColor(monthOn ? COLOR_MODE_ON : COLOR_MODE_OFF));
        bM.setTextColor(monthOn ? Color.WHITE : Color.parseColor("#888888"));
        bW.setBackgroundColor(Color.parseColor(!monthOn ? COLOR_MODE_ON : COLOR_MODE_OFF));
        bW.setTextColor(!monthOn ? Color.WHITE : Color.parseColor("#888888"));
    }

    private void updateMonthLabel(View root) {
        TextView tv = root.findViewById(R.id.tv_month);
        tv.setText(new SimpleDateFormat("yyyy년 M월", Locale.KOREAN).format(displayMonth.getTime()));
    }

    private void updateWeekLabel(View root) {
        TextView tv = root.findViewById(R.id.tv_week);
        Calendar end = (Calendar) weekStart.clone();
        end.add(Calendar.DATE, 6);
        SimpleDateFormat f = new SimpleDateFormat("M/d", Locale.KOREAN);
        tv.setText(f.format(weekStart.getTime()) + " - " + f.format(end.getTime()));
    }

    private List<Event> getAllSelectedEvents() {
        List<Event> all = new ArrayList<>();
        if (selectedGroups.contains(GROUP_ME)) all.addAll(getMyEvents());
        if (selectedGroups.contains(GROUP_T1)) all.addAll(getTeam1Events());
        if (selectedGroups.contains(GROUP_T2)) all.addAll(getTeam2Events());
        return all;
    }

    private List<Event> getMyEvents() {
        List<Event> list = new ArrayList<>();
        if (getActivity() == null) return list;
        String json = getActivity().getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE)
                .getString("my_schedule_key", null);
        if (json != null) {
            List<A1Fragment.Schedule> mine = new Gson().fromJson(json, new TypeToken<List<A1Fragment.Schedule>>(){}.getType());
            if (mine != null) {
                for (A1Fragment.Schedule s : mine) {
                    list.add(new Event(s.subject, s.date, s.startTime, s.endTime, GROUP_ME, COLOR_ME));
                }
            }
        }
        if (list.isEmpty()) {
            list.add(new Event("알고리즘", "260525", 9, 11, GROUP_ME, COLOR_ME));
            list.add(new Event("데이터베이스", "260525", 13, 15, GROUP_ME, COLOR_ME));
            list.add(new Event("운영체제", "260526", 10, 12, GROUP_ME, COLOR_ME));
            list.add(new Event("알고리즘", "260527", 9, 11, GROUP_ME, COLOR_ME));
            list.add(new Event("모바일", "260527", 14, 16, GROUP_ME, COLOR_ME));
            list.add(new Event("운영체제", "260528", 10, 12, GROUP_ME, COLOR_ME));
            list.add(new Event("데이터베이스", "260529", 13, 15, GROUP_ME, COLOR_ME));
        }
        return list;
    }

    private List<Event> getTeam1Events() {
        List<Event> list = new ArrayList<>();
        list.add(new Event("팀 회의", "260525", 18, 19, GROUP_T1, COLOR_T1));
        list.add(new Event("자료 조사", "260526", 17, 19, GROUP_T1, COLOR_T1));
        list.add(new Event("발표 준비", "260527", 16, 18, GROUP_T1, COLOR_T1));
        list.add(new Event("진행 공유", "260528", 18, 20, GROUP_T1, COLOR_T1));
        list.add(new Event("코드 리뷰", "260529", 18, 19, GROUP_T1, COLOR_T1));
        return list;
    }

    private List<Event> getTeam2Events() {
        List<Event> list = new ArrayList<>();
        list.add(new Event("디자인 회의", "260526", 15, 16, GROUP_T2, COLOR_T2));
        list.add(new Event("기획 미팅", "260528", 16, 17, GROUP_T2, COLOR_T2));
        list.add(new Event("프로토타이핑", "260530", 10, 13, GROUP_T2, COLOR_T2));
        return list;
    }

    private void renderMonthCalendar(View root) {
        LinearLayout container = root.findViewById(R.id.calendarContainer);
        container.removeAllViews();

        Calendar c = (Calendar) displayMonth.clone();
        c.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, -(firstDayOfWeek - 1));

        int currentMonth = displayMonth.get(Calendar.MONTH);
        List<Event> events = getAllSelectedEvents();
        float density = getResources().getDisplayMetrics().density;
        int cellHeight = (int) (75 * density);

        for (int row = 0; row < 6; row++) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cellHeight);
            rowLayout.setLayoutParams(rlp);

            for (int col = 0; col < 7; col++) {
                LinearLayout cell = new LinearLayout(getContext());
                cell.setOrientation(LinearLayout.VERTICAL);
                cell.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                clp.setMargins(1, 1, 1, 1);
                cell.setLayoutParams(clp);
                cell.setPadding(4, 4, 4, 4);

                TextView dayNum = new TextView(getContext());
                dayNum.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
                dayNum.setTextSize(11);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                if (c.get(Calendar.MONTH) != currentMonth) {
                    dayNum.setTextColor(Color.parseColor("#BBBBBB"));
                } else if (dayOfWeek == Calendar.SUNDAY) {
                    dayNum.setTextColor(Color.parseColor("#FF0000"));
                } else if (dayOfWeek == Calendar.SATURDAY) {
                    dayNum.setTextColor(Color.parseColor("#0000FF"));
                } else {
                    dayNum.setTextColor(Color.BLACK);
                }
                cell.addView(dayNum);

                String cellDateStr = String.format(Locale.US, "%02d%02d%02d",
                        c.get(Calendar.YEAR) % 100,
                        c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.DAY_OF_MONTH));

                int shown = 0;
                for (Event e : events) {
                    if (!e.date.equals(cellDateStr)) continue;
                    if (shown >= 3) break;
                    TextView bar = new TextView(getContext());
                    bar.setText(e.subject);
                    bar.setTextSize(8);
                    bar.setSingleLine(true);
                    bar.setBackgroundColor(Color.parseColor(e.color));
                    bar.setPadding(3, 1, 3, 1);
                    LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    blp.setMargins(0, 1, 0, 0);
                    bar.setLayoutParams(blp);
                    cell.addView(bar);
                    shown++;
                }

                rowLayout.addView(cell);
                c.add(Calendar.DATE, 1);
            }
            container.addView(rowLayout);
        }
    }

    private void renderWeekSchedule(View root) {
        List<Event> events = getAllSelectedEvents();
        SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd", Locale.US);

        List<String> weekDates = new ArrayList<>();
        Calendar tc = (Calendar) weekStart.clone();
        for (int i = 0; i < 7; i++) {
            weekDates.add(fmt.format(tc.getTime()));
            tc.add(Calendar.DATE, 1);
        }

        int maxEnd = 18;
        for (Event e : events) {
            if (weekDates.contains(e.date) && e.endTime > maxEnd) maxEnd = e.endTime;
        }

        float density = getResources().getDisplayMetrics().density;
        int hourHeight = (int) (40 * density);
        int totalHeight = (maxEnd - 9) * hourHeight;

        LinearLayout weekRow = root.findViewById(R.id.weekScheduleRow);
        ViewGroup.LayoutParams wlp = weekRow.getLayoutParams();
        wlp.height = totalHeight;
        weekRow.setLayoutParams(wlp);

        LinearLayout timeCol = root.findViewById(R.id.week_col_time);
        timeCol.removeAllViews();
        for (int h = 9; h < maxEnd; h++) {
            TextView t = new TextView(getContext());
            t.setText(String.valueOf(h));
            t.setGravity(Gravity.CENTER);
            t.setTextSize(10);
            t.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, hourHeight);
            lp.setMargins(1, 1, 1, 1);
            t.setLayoutParams(lp);
            timeCol.addView(t);
        }

        int[] colIds = {
                R.id.week_col_sun, R.id.week_col_mon, R.id.week_col_tue,
                R.id.week_col_wed, R.id.week_col_thu, R.id.week_col_fri, R.id.week_col_sat
        };
        Calendar c = (Calendar) weekStart.clone();
        for (int i = 0; i < 7; i++) {
            String dateStr = fmt.format(c.getTime());
            int dow = c.get(Calendar.DAY_OF_WEEK);
            int colIdx = dow - 1;
            FrameLayout col = root.findViewById(colIds[colIdx]);
            col.removeAllViews();

            LinearLayout bg = new LinearLayout(getContext());
            bg.setOrientation(LinearLayout.VERTICAL);
            for (int h = 9; h < maxEnd; h++) {
                View hourCell = new View(getContext());
                LinearLayout.LayoutParams hcp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, hourHeight);
                hcp.setMargins(1, 1, 1, 1);
                hourCell.setLayoutParams(hcp);
                hourCell.setBackgroundColor(Color.WHITE);
                bg.addView(hourCell);
            }
            col.addView(bg, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            for (Event e : events) {
                if (!e.date.equals(dateStr)) continue;
                TextView block = new TextView(getContext());
                block.setText(e.subject);
                block.setTextSize(10);
                block.setGravity(Gravity.CENTER);
                block.setBackgroundColor(Color.parseColor(e.color));
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        hourHeight * (e.endTime - e.startTime) - 2);
                flp.topMargin = (e.startTime - 9) * hourHeight + 1;
                flp.leftMargin = 2;
                flp.rightMargin = 2;
                block.setLayoutParams(flp);
                col.addView(block);
            }
            c.add(Calendar.DATE, 1);
        }
    }
}
