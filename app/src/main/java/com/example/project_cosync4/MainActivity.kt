package com.example.project_cosync4

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment // 🌟 Fragment import 확인!

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnA1 = findViewById<Button>(R.id.btn_a1)
        val btnA2 = findViewById<Button>(R.id.btn_a2)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        // 1. 앱이 처음 켜졌을 때 기본으로 a1 화면(시간표)을 바구니에 채워 넣습니다.
        if (savedInstanceState == null) {
            replaceFragment(A1Fragment())
        }

        // 2. a1 버튼 클릭 시 -> A1Fragment로 교체
        btnA1.setOnClickListener {
            replaceFragment(A1Fragment())
        }

        // 3. a2 버튼 클릭 시 -> A2Fragment로 교체
        btnA2.setOnClickListener {
            replaceFragment(A2Fragment())
        }

        // 4. 설정 버튼 클릭 시 -> SettingsFragment로 교체
        btnSettings.setOnClickListener {
            replaceFragment(SettingsFragment())
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