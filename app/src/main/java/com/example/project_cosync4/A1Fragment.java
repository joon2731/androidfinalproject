package com.example.project_cosync4; // ⚠️ 패키지명은 건드리지 마세요!

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class A1Fragment extends Fragment {

    // 자바용 Schedule 데이터 클래스
    public static class Schedule {
        String subject;
        String date;
        int startTime;
        int endTime;

        public Schedule(String subject, String date, int startTime, int endTime) {
            this.subject = subject;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    // 화면(XML)을 연결해주는 부분
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a1, container, false);
    }

    // 화면 연결이 끝난 직후 로직을 실행하는 부분 (코틀린의 onCreate와 비슷한 역할)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveScheduleData(); // 데이터 임시 저장
        List<Schedule> loadedSchedules = loadScheduleData(); // 데이터 불러오기
        renderTimetable(view, loadedSchedules); // 화면 렌더링
    }

    private void renderTimetable(View view, List<Schedule> schedules) {
        // 가장 늦게 끝나는 시간 계산 (기본 15시)
        int maxEndTime = 15;
        for (Schedule s : schedules) {
            if (s.endTime > maxEndTime) {
                maxEndTime = s.endTime;
            }
        }

        // 1. 시간 기둥 렌더링
        LinearLayout timeColumn = view.findViewById(R.id.col_time);
        timeColumn.removeAllViews();
        for (int hour = 9; hour < maxEndTime; hour++) {
            timeColumn.addView(createBlock(String.valueOf(hour), 1, "#FFFFFF"));
        }

        // 2. 월~일요일 기둥 렌더링
        int[] columnIds = {R.id.col_mon, R.id.col_tue, R.id.col_wed, R.id.col_thu, R.id.col_fri, R.id.col_sat, R.id.col_sun};
        String[] dateForColumn = {"260525", "260526", "260527", "260528", "260529", "260530", "260531"};

        for (int i = 0; i < 7; i++) {
            LinearLayout column = view.findViewById(columnIds[i]);
            column.removeAllViews();

            String targetDate = dateForColumn[i];
            int currentHour = 9;

            while (currentHour < maxEndTime) {
                Schedule matchedSchedule = null;
                // 현재 시간에 맞는 스케줄 찾기
                for (Schedule s : schedules) {
                    if (s.date.equals(targetDate) && s.startTime == currentHour) {
                        matchedSchedule = s;
                        break;
                    }
                }

                if (matchedSchedule != null) {
                    int duration = matchedSchedule.endTime - matchedSchedule.startTime;
                    column.addView(createBlock(matchedSchedule.subject, duration, "#D5E8FF"));
                    currentHour = matchedSchedule.endTime;
                } else {
                    column.addView(createBlock("", 1, "#FFFFFF"));
                    currentHour++;
                }
            }
        }
    }

    // 블록(픽셀 조립) 생성기
    private TextView createBlock(String text, int duration, String colorHex) {
        TextView textView = new TextView(getContext());
        float density = getResources().getDisplayMetrics().density;

        int baseHeightPx = (int) (60 * density);
        int marginPx = (int) (1 * density);
        int fullCellSpacePx = baseHeightPx + (marginPx * 2);
        int finalHeightPx = (fullCellSpacePx * duration) - (marginPx * 2);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, finalHeightPx);
        params.setMargins(marginPx, marginPx, marginPx, marginPx);
        textView.setLayoutParams(params);

        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(10f);
        textView.setBackgroundColor(Color.parseColor(colorHex));

        return textView;
    }

    // Gson으로 데이터 저장
    private void saveScheduleData() {
        // 프래그먼트에서는 getActivity()를 통해 메모장을 가져옵니다.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE);

        List<Schedule> myScheduleList = new ArrayList<>();
        myScheduleList.add(new Schedule("수학", "260528", 9, 10));
        myScheduleList.add(new Schedule("영어", "260528", 14, 16));

        String jsonString = new Gson().toJson(myScheduleList);
        sharedPreferences.edit().putString("my_schedule_key", jsonString).apply();
    }

    // Gson으로 데이터 불러오기
    private List<Schedule> loadScheduleData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString("my_schedule_key", null);

        if (jsonString == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Schedule>>() {}.getType();
        return new Gson().fromJson(jsonString, type);
    }
}