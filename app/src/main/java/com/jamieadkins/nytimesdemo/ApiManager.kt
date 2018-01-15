package com.jamieadkins.nytimesdemo

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiManager {
    private var apiInstance: PulseliveApi? = null
    private var gson: Gson? = null

    fun getPulseliveApi(): PulseliveApi {
        if (apiInstance == null) {
            val service = Retrofit.Builder()
                    .baseUrl("http://dynamic.pulselive.com/test/native/")
                    .addConverterFactory(BufferedSourceConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttpClientBuilder().build())
                    .build()
            apiInstance = service.create(PulseliveApi::class.java)
        }
        return apiInstance as PulseliveApi
    }

    fun getGson(): Gson {
        if (gson == null) {
            gson = GsonBuilder()
                    .create()
        }
        return gson as Gson
    }

    private fun getOkHttpClientBuilder(vararg interceptors: Interceptor): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()

        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)

        //Add interceptors first, so that they will be included on the curl request output (debug)
        for (i in interceptors) {
            builder.addInterceptor(i)
        }
        //Log curl requests when in debug mode
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(CurlInterceptor { message -> Log.d("Ok2Curl", message) })
        }

        return builder
    }
}


