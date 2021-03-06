package com.jamieadkins.nytimesdemo

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface PulseliveApi {

    @GET("contentList.json")
    fun getArticles(): Single<ResponseBody>
}
