package com.example.todolist.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.todolist.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {      //Handler함수로 SplashActivity의 딜레이 시간 지정
            startActivity(Intent(this, MainActivity::class.java))   //딜레이준 다음 어떤 엑티비티로 이동할건지(MainActivity로 이동)
            finish()    // 현재 엑티비티 종료 (SplashActivity는 이동한다음에는 쓸모없어지기 때문)
        },2000)  // 1500 = 1.5초 , 2000 = 2초

    }
}