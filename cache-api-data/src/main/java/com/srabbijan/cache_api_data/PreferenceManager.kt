package com.srabbijan.cache_api_data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object PreferenceManager {

    private const val PREFERENCE_NAME = "com.srabbijan.cache_api_data"
    var preferences: SharedPreferences? = null
    fun Builder(context: Context){
          preferences =
            try {
                context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
    }


    private val editor = preferences?.edit()

    private fun setString(key: String, value: String) {
        editor?.putString(key, value)
        editor?.apply()
    }

    fun setBoolean(key: String, value: Boolean) {
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun setInt(key: String, value: Int) {
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun setDouble(key: String, value: Double) {
        editor?.putFloat(key, value.toFloat())
        editor?.apply()
    }

    fun getString(key: String): String {
        return preferences?.getString(key, "") ?: ""
    }

    fun getBoolean(key: String): Boolean {
        return preferences?.getBoolean(key, true) ?: true
    }

    fun getInt(key: String): Int {
        return preferences?.getInt(key, 0) ?: 0
    }

    fun getIntWithDefaultValue(key: String, defaultValue: Int = 0): Int {
        return preferences?.getInt(key, defaultValue) ?: defaultValue
    }

    fun getDouble(key: String): Double {
        return preferences?.getFloat(key, 0.0f)?.toDouble() ?: 0.0
    }

    fun getFloatWithDefaultValue(key: String, defaultValue: Float = 0f): Float {
        return preferences?.getFloat(key, defaultValue) ?: defaultValue
    }

    private fun setLong(key: String, value: Long) {
        preferences?.edit()?.putLong(key, value)?.apply()
    }

    private fun getLong(key: String, defaultValue: Long = 0): Long {
        return preferences?.getLong(key, defaultValue) ?: defaultValue
    }

    fun setIntArrayList(key: String, list: ArrayList<Int>) {
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor?.putString(key, json)
        editor?.apply()
    }

    fun getIntArrayList(key: String): ArrayList<Int>? {
        val gson = Gson()
        val json: String = preferences?.getString(key, "") ?: ""
        val type: Type = object : TypeToken<ArrayList<Int>>() {}.type
        return gson.fromJson(json, type)
    }

    // caching data start
    fun putLastApiCacheTime(apiEndPoint: String, time: Long) {
        setLong("${apiEndPoint}_cache_time", time)
    }

    fun getLastApiCacheTime(apiEndPoint: String, defaultTime: Long = 0L): Long {
        "getLastApiCacheTime() called: $apiEndPoint".log("PreferenceManager")
        return getLong(
            "${apiEndPoint}_cache_time",
            defaultTime
        )
    }

    fun cacheApiResponse(apiEndPoint: String, response: String) {
        "cacheApiResponse() called: $apiEndPoint".log("PreferenceManager")
        setString(apiEndPoint, response)
    }

    fun loadApiResponseFromCache(apiEndPoint: String): String {
        "loadApiResponseFromCache() called: $apiEndPoint".log("PreferenceManager")
        return getString(apiEndPoint)
    }
    // caching data end

    fun clearPreference() {
        editor?.clear()
        editor?.apply()
    }
}