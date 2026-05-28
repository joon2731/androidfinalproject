package com.example.project_cosync4

import android.os.Bundle
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment // 🌟 Fragment import 확인!

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutDashboard = findViewById<LinearLayout>(R.id.layout_dashboard)
        val layoutWorkspace = findViewById<LinearLayout>(R.id.layout_workspace)
        val layoutProfile = findViewById<LinearLayout>(R.id.layout_profile)

        val ivDashboard = findViewById<ImageView>(R.id.iv_dashboard)
        val ivWorkspace = findViewById<ImageView>(R.id.iv_workspace)
        val ivProfile = findViewById<ImageView>(R.id.iv_profile)

        val tvDashboard = findViewById<TextView>(R.id.tv_dashboard)
        val tvWorkspace = findViewById<TextView>(R.id.tv_workspace)
        val tvProfile = findViewById<TextView>(R.id.tv_profile)

        // 탭 선택 시 시각적 효과를 주는 헬퍼 함수
        fun selectTab(activeTab: Int) {
            // 모든 탭을 비활성화 상태 색상(#778099)으로 초기화
            val inactiveColor = Color.parseColor("#778099")
            val activeColor = Color.parseColor("#4F46E5")

            ivDashboard.imageTintList = ColorStateList.valueOf(inactiveColor)
            tvDashboard.setTextColor(inactiveColor)
            tvDashboard.setTypeface(null, Typeface.NORMAL)

            ivWorkspace.imageTintList = ColorStateList.valueOf(inactiveColor)
            tvWorkspace.setTextColor(inactiveColor)
            tvWorkspace.setTypeface(null, Typeface.NORMAL)

            ivProfile.imageTintList = ColorStateList.valueOf(inactiveColor)
            tvProfile.setTextColor(inactiveColor)
            tvProfile.setTypeface(null, Typeface.NORMAL)

            // 선택된 탭 활성화 상태 색상(#4F46E5) 및 볼드체 적용
            when (activeTab) {
                0 -> {
                    ivDashboard.imageTintList = ColorStateList.valueOf(activeColor)
                    tvDashboard.setTextColor(activeColor)
                    tvDashboard.setTypeface(null, Typeface.BOLD)
                }
                1 -> {
                    ivWorkspace.imageTintList = ColorStateList.valueOf(activeColor)
                    tvWorkspace.setTextColor(activeColor)
                    tvWorkspace.setTypeface(null, Typeface.BOLD)
                }
                2 -> {
                    ivProfile.imageTintList = ColorStateList.valueOf(activeColor)
                    tvProfile.setTextColor(activeColor)
                    tvProfile.setTypeface(null, Typeface.BOLD)
                }
            }
        }

        // 1. 앱이 처음 켜졌을 때 기본으로 대시보드(A1Fragment) 화면을 설정합니다.
        if (savedInstanceState == null) {
            replaceFragment(A1Fragment())
            selectTab(0)
        }

        // 2. 대시보드 탭 클릭 시 -> A1Fragment로 교체
        layoutDashboard.setOnClickListener {
            replaceFragment(A1Fragment())
            selectTab(0)
        }

        // 3. 워크스페이스 탭 클릭 시 -> A2Fragment로 교체
        layoutWorkspace.setOnClickListener {
            replaceFragment(A2Fragment())
            selectTab(1)
        }

        // 4. 프로필 탭 클릭 시 -> SettingsFragment로 교체
        layoutProfile.setOnClickListener {
            replaceFragment(SettingsFragment())
            selectTab(2)
        }
    }

    // 🌟 바구니 안의 내용물을 갈아 끼워주는 핵심 함수
    private fun replaceFragment(fragment: Fragment) {
        // FragmentManager가 교체 작업을 지휘합니다.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 빈 바구니(container)에 새 fragment를 끼워 넣음
            .commit() // "교체 실행!"
    }
}