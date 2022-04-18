package com.zhouzi.eventbus_study.bus

import java.lang.reflect.Method

data class SubscriberMethod(
    val method:Method,
    val threadMode:ThreadMode,
    val eventType:Class<*>
)