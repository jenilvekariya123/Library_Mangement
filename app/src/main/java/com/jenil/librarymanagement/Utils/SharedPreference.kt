package com.jenil.librarymanagement.Utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreference {

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return try {
            sharedPreferences?.getInt(key, defaultValue) ?: defaultValue
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences?.edit()?.apply {
            putInt(key, value)
            apply()
        }
    }

    fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences?.getString(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        sharedPreferences?.edit()?.apply {
            putString(key, value)
            apply()
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences?.getBoolean(key, defaultValue) ?: defaultValue
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences?.edit()?.apply {
            putBoolean(key, value)
            apply()
        }
    }

    fun clearSharedPreference() {
        sharedPreferences!!.edit().apply{
            clear()
            apply()
        }
    }
}
