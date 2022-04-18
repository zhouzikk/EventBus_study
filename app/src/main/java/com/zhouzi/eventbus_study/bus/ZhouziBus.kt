package com.zhouzi.eventbus_study.bus

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import java.lang.RuntimeException
import kotlin.concurrent.thread

class ZhouziBus {

    private val cacheMap = mutableMapOf<Any, List<SubscriberMethod>>()

    companion object {
        //单例、双重校验锁
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ZhouziBus()
        }
    }

    fun register(subscriber: Any) {
        var list = cacheMap[subscriber]
        if (list == null) {
            list = getSubscribeMethodReflection(subscriber)
            if (list.isNotEmpty()) {
                cacheMap[subscriber] = list
            }
        }

    }

    private fun getSubscribeMethodReflection(subscriber: Any): List<SubscriberMethod> {
        return mutableListOf<SubscriberMethod>().apply {
            var clazz: Class<*>? = subscriber.javaClass
            while (clazz != null) {
                //过滤系统类
                if (clazz.name.startsWith("java.") || clazz.name.startsWith("javax.") ||
                    clazz.name.startsWith("android.") || clazz.name.startsWith("androidx.")
                )
                    break
                clazz.let {
                    //获取subscriber中所有的方法
                    it.declaredMethods
                }.forEach {
                    //获取注解的方法
                    val subscribe = it.getAnnotation(Subscribe::class.java) ?: return@forEach
                    val types = it.parameterTypes.also {
                        if (it.size != 1)
                            throw RuntimeException("参数必须唯一")
                    }
                    add(SubscriberMethod(it, subscribe.threadMode, types[0]))
                }
                //获取父类中的注解方法
                clazz = clazz.superclass
            }
        }
    }

    fun post(event: Any) {
        cacheMap.forEach {
            it.value.forEach { value ->
                if (value.eventType.isAssignableFrom(event.javaClass))
                    if (value.threadMode == ThreadMode.MAIN)
                        if (Looper.myLooper() == Looper.getMainLooper())
                            invoke(value, it, event)
                        else
//                            CoroutineScope(Dispatchers.Main).launch {
//                                invoke(value, it, event)
//                            }
                            Handler(Looper.getMainLooper()).post {
                                invoke(value, it, event)
                            }
                    else
                        CoroutineScope(Dispatchers.IO).launch {
                            invoke(value, it, event)
                        }
            }
        }
    }

    private fun invoke(
        value: SubscriberMethod,
        it: Map.Entry<Any, List<SubscriberMethod>>,
        any: Any
    ) {
        value.method.run {
            //可对private进行操作
            isAccessible = true
            invoke(it.key, any)
        }
    }


    fun unRegister(subscriber: Any) {
        cacheMap.remove(subscriber)
    }
}