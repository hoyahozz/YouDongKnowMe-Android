package com.dongyang.android.youdongknowme.standard

import android.app.Application
import android.content.Context
import com.dongyang.android.youdongknowme.data.local.SharedPreference
import com.dongyang.android.youdongknowme.standard.di.databaseModule
import com.dongyang.android.youdongknowme.standard.di.networkModule
import com.dongyang.android.youdongknowme.standard.di.repositoryModule
import com.dongyang.android.youdongknowme.standard.di.viewModelModule
import com.dongyang.android.youdongknowme.standard.util.logd
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.core.context.GlobalContext.startKoin

/**
 * 앱 실행 시 가장 먼저 진입
 */
class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        logd("application create")
        super.onCreate()

        // SharedPreference 초기화
        SharedPreference.getInstance(this)

        startKoin {
            androidContext(this@MyApplication)
            androidFileProperties()
            modules(listOf(databaseModule, repositoryModule, viewModelModule, networkModule))
        }
    }
}