package com.jamieadkins.nytimesdemo

import java.lang.reflect.Type

import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Converter
import retrofit2.Retrofit

class BufferedSourceConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?,
                                       retrofit: Retrofit?): Converter<ResponseBody, *>? {
        if (BufferedSource::class.java != type) {
            return null
        }
        return Converter<ResponseBody, BufferedSource> { value -> value.source() }
    }
}
