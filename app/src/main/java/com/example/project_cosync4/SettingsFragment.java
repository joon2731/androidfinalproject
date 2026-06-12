package com.example.project_cosync4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSync = view.findViewById(R.id.btn_sync_everytime);
        if (btnSync != null) {
            btnSync.setOnClickListener(v -> showSyncGuideDialog());
        }
    }

    private void showSyncGuideDialog() {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("학교 시간표 연동 안내")
                .setMessage("목원대학교 종합정보시스템에 로그인하신 후,\n[수업] -> [강의정보] -> [수강신청내역조회] 메뉴로 이동하시면 자동으로 시간표가 연동됩니다.")
                .setPositiveButton("이동하기", (dialog, which) -> openWebViewDialog())
                .setNegativeButton("취소", null)
                .show();
    }

    private void openWebViewDialog() {
        if (getContext() == null) return;

        // Container Layout
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Top Bar
        LinearLayout topBar = new LinearLayout(getContext());
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(32, 24, 32, 24);
        topBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(getContext());
        title.setText("학교 시간표 연동");
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        );
        title.setLayoutParams(titleParams);
        topBar.addView(title);

        Button btnClose = new Button(getContext());
        btnClose.setText("닫기");
        btnClose.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        topBar.addView(btnClose);

        container.addView(topBar);

        // Separator
        View separator = new View(getContext());
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
        ));
        separator.setBackgroundColor(Color.parseColor("#E0E0E0"));
        container.addView(separator);

        // WebView
        WebView webView = new WebView(getContext());
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        container.addView(webView);

        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(container);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(dialog), "AndroidBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Inject JS to override XMLHttpRequest and look for crossurl.jsp
                String js = "(function() {\n" +
                        "    var open = XMLHttpRequest.prototype.open;\n" +
                        "    XMLHttpRequest.prototype.open = function(method, url) {\n" +
                        "        this.addEventListener('load', function() {\n" +
                        "            if (url.indexOf('crossurl.jsp') !== -1) {\n" +
                        "                var responseText = this.responseText;\n" +
                        "                if (responseText.indexOf('SUGANG_YY') !== -1 && responseText.indexOf('SUBJECT_NAME') !== -1) {\n" +
                        "                    AndroidBridge.onScheduleDataReceived(responseText);\n" +
                        "                }\n" +
                        "            }\n" +
                        "        });\n" +
                        "        open.apply(this, arguments);\n" +
                        "    };\n" +
                        "})();";
                webView.evaluateJavascript(js, null);
            }
        });

        webView.loadUrl("https://v.mokwon.ac.kr/");
        dialog.show();
    }

    public static class SimpleSchedule {
        public String subject;
        public String date;
        public int startTime;
        public int endTime;

        public SimpleSchedule(String subject, String date, int startTime, int endTime) {
            this.subject = subject;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    class WebAppInterface {
        private final Dialog dialog;

        WebAppInterface(Dialog dialog) {
            this.dialog = dialog;
        }

        @JavascriptInterface
        public void onScheduleDataReceived(String rawData) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<SimpleSchedule> schedules = parseScheduleData(rawData);
                        dialog.dismiss(); // 웹뷰 다이얼로그 닫기

                        if (!schedules.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("총 ").append(schedules.size()).append("개의 일정이 추출되었습니다.\n\n");
                            for (SimpleSchedule s : schedules) {
                                sb.append("과목: ").append(s.subject).append("\n")
                                        .append("날짜: ").append(s.date).append("\n")
                                        .append("시간: ").append(s.startTime).append("시 ~ ").append(s.endTime).append("시\n")
                                        .append("--------------------\n");
                            }

                            if (getContext() != null) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("시간표 추출 결과 (테스트용)")
                                        .setMessage(sb.toString().trim())
                                        .setPositiveButton("확인", null)
                                        .show();
                            }
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "시간표 데이터를 찾았으나 파싱에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }

    private List<SimpleSchedule> parseScheduleData(String rawData) {
        List<SimpleSchedule> schedules = new ArrayList<>();
        String[] rows = rawData.split("\u000c"); // \f (ASCII 12)
        if (rows.length <= 1) return schedules;

        for (int i = 1; i < rows.length; i++) {
            String row = rows[i].trim();
            if (row.isEmpty()) continue;

            String[] columns = row.split("\u0008"); // \b (ASCII 8)
            if (columns.length > 11) {
                String subject = columns[9].trim();     // 과목명
                String timeRoom = columns[11].trim();    // 시간/강실 (예: "금2,3,4(D423)")

                List<SimpleSchedule> parsedTimes = parseTimeRoom(subject, timeRoom);
                schedules.addAll(parsedTimes);
            }
        }
        return schedules;
    }

    private List<SimpleSchedule> parseTimeRoom(String subject, String timeRoom) {
        List<SimpleSchedule> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("([월화수목금토일])([\\d,]+)(?:\\(([^)]+)\\))?");
        Matcher matcher = pattern.matcher(timeRoom);
        while (matcher.find()) {
            String day = matcher.group(1);
            String periodsStr = matcher.group(2);

            String date = mapDayToDate(day);
            if (date == null) continue;

            String[] periods = periodsStr.split(",");
            if (periods.length == 0) continue;

            int minPeriod = Integer.MAX_VALUE;
            int maxPeriod = Integer.MIN_VALUE;
            for (String pStr : periods) {
                try {
                    int p = Integer.parseInt(pStr.trim());
                    if (p < minPeriod) minPeriod = p;
                    if (p > maxPeriod) maxPeriod = p;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            if (minPeriod != Integer.MAX_VALUE && maxPeriod != Integer.MIN_VALUE) {
                int startTime = minPeriod + 8;
                int endTime = maxPeriod + 9;
                list.add(new SimpleSchedule(subject, date, startTime, endTime));
            }
        }
        return list;
    }

    private String mapDayToDate(String day) {
        switch (day) {
            case "월": return "260525";
            case "화": return "260526";
            case "수": return "260527";
            case "목": return "260528";
            case "금": return "260529";
            case "토": return "260530";
            case "일": return "260531";
            default: return null;
        }
    }
}