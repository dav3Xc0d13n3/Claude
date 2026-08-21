package com.example

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class AiWorkspaceApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
