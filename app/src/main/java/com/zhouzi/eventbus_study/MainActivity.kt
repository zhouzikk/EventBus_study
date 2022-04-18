package com.zhouzi.eventbus_study

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import com.zhouzi.eventbus_study.bus.Subscribe
import com.zhouzi.eventbus_study.bus.ThreadMode
import com.zhouzi.eventbus_study.bus.ZhouziBus
import com.zhouzi.eventbus_study.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.btn.setOnClickListener {
            startActivity(Intent(this, PostActivty::class.java))
        }

        ZhouziBus.instance.register(this)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun test(event: TestEvent) {
        println("${Thread.currentThread()}------${event.a}")
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun test2(event: TestEvent) {
        println("${Thread.currentThread()}------${event.a}")
    }
    override fun onDestroy() {
        super.onDestroy()
        ZhouziBus.instance.unRegister(this)
    }

}