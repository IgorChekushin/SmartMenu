package com.example.smartmenu

import android.app.Application

class MyApp:Application() {
    var isUpdate = false
    companion object {
        var INSTANCE: MyApp? = null


        fun getInstance(): MyApp? {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            val instance = MyApp()
            INSTANCE = instance
            return instance
        }
    }
}