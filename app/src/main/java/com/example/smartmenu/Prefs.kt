package com.example.smartmenu

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Prefs(context: Context) {
    private val APP_PREF_NAME = "mySettings"
    private val APP_PREF_STRING_FOODLIST = "actualFoodList"

    private val preferences: SharedPreferences =
        context.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE)

    var actualFoodList: List<String>
        get() {
            val gson = Gson()
            val json = preferences.getString(APP_PREF_STRING_FOODLIST, null)
            val type = object : TypeToken<ArrayList<String>>() {}.type//converting the json to list
            return gson.fromJson(json, type) ?: listOf()//returning the list
        }
        set(value) {
            val gson = Gson()
            val json = gson.toJson(value)//converting list to Json
            preferences.edit().putString(APP_PREF_STRING_FOODLIST, json).apply()
        }

}

