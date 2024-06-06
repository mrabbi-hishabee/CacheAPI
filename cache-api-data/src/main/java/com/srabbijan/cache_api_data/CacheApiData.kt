package com.srabbijan.cache_api_data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class CacheApiData {
    fun Buider(
        context: Context,
        cacheApiMapper: HashMap<String, CacheRefreshType>
    ){
        PreferenceManager.Builder(context = context)
        ApiCacheManager.Builder(cacheApiMapper)

         object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                "responseCachingInterceptor() called".log()
                val endPoint = chain.request().url.toUri().path.toString()
                val requestMethod = chain.request().method
                "requestMethod :$requestMethod endPoint: $endPoint".log()
                if (ApiCacheManager.shouldResponseLoadFromCache(endPoint) && requestMethod == Http_GET) {
                    "loading api response from cache: $endPoint".log("CachingInterceptor")
                    val cacheResponseBodyString = PreferenceManager.loadApiResponseFromCache(endPoint)
                    "responseString: $cacheResponseBodyString".log("CachingInterceptor")
                    try {
                        return Response.Builder()
                            .code(Http_200)
                            .protocol(Protocol.HTTP_2)
                            .message(cacheResponseBodyString)
                            .body(
                                cacheResponseBodyString
                                    .toByteArray()
                                    .toResponseBody("application/json".toMediaTypeOrNull())
                            )
                            .request(chain.request())
                            .addHeader("content-type", "application/json")
                            .build()
                    } catch (e: Exception) {
                        "Entered in Catch Section".log()
                        return chain.proceed(chain.request())
                    }
                } else {
                    val networkResponse = chain.proceed(chain.request())
                    if (networkResponse.code == Http_200 && ApiCacheManager.shouldCache(endPoint)) {
                        try {
                            val responseBodyString = networkResponse.peekBody(Long.MAX_VALUE).string()
                            ApiCacheManager.cacheTheResponse(endPoint, responseBodyString)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return networkResponse
                }
            }
        }
    }
    companion object {
        const val Http_GET = "GET"
        const val Http_200 = 200
    }

}