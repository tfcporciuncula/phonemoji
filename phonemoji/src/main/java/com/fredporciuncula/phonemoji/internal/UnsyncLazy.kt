package com.fredporciuncula.phonemoji.internal

fun <T> unsyncLazy(initializer: () -> T) = lazy(mode = LazyThreadSafetyMode.NONE, initializer)
