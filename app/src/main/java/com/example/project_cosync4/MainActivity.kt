package com.example.project_cosync4 // ⚠️ 패키지명 건드리지 마세요!

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 임시 데이터 저장
        saveScheduleData()

        // 2. 저장소에서 데이터 불러오기
        val loadedSchedules = loadScheduleData()

        // 3. 화면 렌더링
        renderTimetable(loadedSchedules)
    }

    private fun renderTimetable(schedules: List<Schedule>) {
        val maxEndTime = schedules.maxOfOrNull { it.endTime } ?: 15
        val finalEndTime = maxOf(15, maxEndTime)

        // [변경점] 1. 시간 기둥(9, 10, 11...) 렌더링 (duration을 1로 전달)
        val timeColumn = findViewById<LinearLayout>(R.id.col_time)
        timeColumn.removeAllViews()
        for (hour in 9 until finalEndTime) {
            val timeBlock = createBlock(hour.toString(), 1, "#FFFFFF")
            timeColumn.addView(timeBlock)
        }

        // 2. 월~일요일 기둥 렌더링
        val columnIds = arrayOf(R.id.col_mon, R.id.col_tue, R.id.col_wed, R.id.col_thu, R.id.col_fri, R.id.col_sat, R.id.col_sun)
        val dateForColumn = arrayOf("260525", "260526", "260527", "260528", "260529", "260530", "260531")

        for (i in 0..6) {
            val column = findViewById<LinearLayout>(columnIds[i])
            column.removeAllViews()

            val targetDate = dateForColumn[i]
            var currentHour = 9

            while (currentHour < finalEndTime) {
                val schedule = schedules.find { it.date == targetDate && it.startTime == currentHour }

                if (schedule != null) {
                    // [변경점] 2. 연강 시간(duration)을 그대로 전달합니다.
                    val duration = schedule.endTime - schedule.startTime
                    val block = createBlock(schedule.subject, duration, "#D5E8FF")
                    column.addView(block)
                    currentHour = schedule.endTime
                } else {
                    // 빈 시간도 duration 1을 전달
                    val blankBlock = createBlock("", 1, "#FFFFFF")
                    column.addView(blankBlock)
                    currentHour++
                }
            }
        }
    }

    // 🌟 [핵심 변경점] 픽셀 오차가 없도록 블록 생성 공식을 완전히 바꿨습니다!
    private fun createBlock(text: String, duration: Int, colorHex: String): TextView {
        val textView = TextView(this)
        val density = resources.displayMetrics.density

        // 1시간짜리 기본 픽셀값 계산 (여기서 생긴 오차를 끝까지 유지시켜 어긋남 방지)
        val baseHeightPx = (60 * density).toInt()
        val marginPx = (1 * density).toInt()

        // 블록 하나가 차지하는 전체 세로 공간 (위아래 마진 포함)
        val fullCellSpacePx = baseHeightPx + (marginPx * 2)

        // 최종 높이 픽셀 = (1시간 전체 공간 * 2시간 연강) - 위아래 마진 빼기
        val finalHeightPx = (fullCellSpacePx * duration) - (marginPx * 2)

        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, finalHeightPx)
        params.setMargins(marginPx, marginPx, marginPx, marginPx)
        textView.layoutParams = params

        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.textSize = 10f
        textView.setBackgroundColor(Color.parseColor(colorHex))

        return textView
    }

    // 데이터 임시 저장
    private fun saveScheduleData() {
        val sharedPreferences = getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE)
        val myScheduleList = listOf(
            Schedule("수학", "260528", 9, 10),
            Schedule("영어", "260528", 14, 16)
        )
        val jsonString = Gson().toJson(myScheduleList)
        sharedPreferences.edit().putString("my_schedule_key", jsonString).apply()
    }

    // 데이터 불러오기
    private fun loadScheduleData(): List<Schedule> {
        val sharedPreferences = getSharedPreferences("TimeTablePrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("my_schedule_key", null) ?: return emptyList()
        val type = object : TypeToken<List<Schedule>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}

// 데이터 클래스
data class Schedule(
    val subject: String,
    val date: String,
    val startTime: Int,
    val endTime: Int
)