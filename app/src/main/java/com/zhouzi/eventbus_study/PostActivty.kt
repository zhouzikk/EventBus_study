package com.zhouzi.eventbus_study

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhouzi.eventbus_study.bus.ZhouziBus
import com.zhouzi.eventbus_study.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class PostActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.text = "上一页"
        binding.btn.setOnClickListener {
          thread {
              ZhouziBus.instance.post(TestEvent("11111111111111"))
          }
        }

    }

}