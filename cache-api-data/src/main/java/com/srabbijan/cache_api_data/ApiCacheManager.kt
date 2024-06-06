package com.srabbijan.cache_api_data

import android.util.Log
import java.util.concurrent.TimeUnit

object ApiCacheManager {

    /**
     * To cache any endpoint response need to just add end point and refresh time in cacheApiMapper
     * start api cache mapper -------------------*/
    private var cacheApiMapper :HashMap<String, CacheRefreshType> = hashMapOf()
    fun Builder(endPoints:HashMap<String, CacheRefreshType>){
        cacheApiMapper = endPoints
    }
    /** end api cache mapper*/

    fun shouldCache(apiEndpoint: String): Boolean {
        "shouldCache() called".log()
        return cacheApiMapper.keys.contains(apiEndpoint)
    }

    fun shouldResponseLoadFromCache(apiEndpoint: String): Boolean {
        "shouldResponseLoadFromCache() called: $apiEndpoint".log()
        if (cacheApiMapper.keys.contains(apiEndpoint).not()) return false
        "shouldResponseLoadFromCache() cacheApiMapper: $apiEndpoint".log()
        "${PreferenceManager.loadApiResponseFromCache(apiEndpoint)} $apiEndpoint".log()
        if (PreferenceManager.loadApiResponseFromCache(apiEndpoint).isEmpty()) return false
        "shouldResponseLoadFromCache() PreferenceManager: $apiEndpoint".log()
        val apiCacheTime = PreferenceManager.getLastApiCacheTime(
            apiEndPoint = apiEndpoint,
            defaultTime = 0L
        )
        "$apiEndpoint last cache time: $apiCacheTime".log()
        if (apiCacheTime == 0L) return false
        val now = System.currentTimeMillis()
        val diffInMs = now - apiCacheTime
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMs)
        return when (cacheApiMapper[apiEndpoint]) {
            CacheRefreshType.HOURLY -> {
                diffInHours < CacheRefreshType.HOURLY.valueInHours
            }

            CacheRefreshType.DAILY -> {
                diffInHours < CacheRefreshType.DAILY.valueInHours
            }

            CacheRefreshType.WEEKLY -> {
                diffInHours < CacheRefreshType.WEEKLY.valueInHours
            }

            CacheRefreshType.MONTHLY -> {
                diffInHours < CacheRefreshType.MONTHLY.valueInHours
            }

            else -> false
        }
    }

    fun cacheTheResponse(forEndpoint: String, responseBodyString: String) {
        "EndPoint: $forEndpoint response: $responseBodyString".log()
        PreferenceManager.cacheApiResponse(forEndpoint, responseBodyString)
        PreferenceManager.putLastApiCacheTime(forEndpoint, System.currentTimeMillis())
    }
}

enum class CacheRefreshType(val valueInHours: Int) {
    HOURLY(1), // 1 hour
    DAILY(24), // 24 hours
    WEEKLY(7 * 24), // 7 Days
    MONTHLY(30 * 24) // 30 Days
}
fun String.log(key: String = "srabbijan.cache_api") {
        Log.e(key, this)
}