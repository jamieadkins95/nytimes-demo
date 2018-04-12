package com.jamieadkins.nytimesdemo

import com.nytimes.android.external.fs3.PathResolver
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.middleware.GsonParserFactory
import com.google.gson.reflect.TypeToken
import com.nytimes.android.external.fs3.SourcePersisterFactory
import io.reactivex.Completable

import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import okio.BufferedSource
import timber.log.Timber

object StoreManager {
    private val storeMap = hashMapOf<BarCode, Store<*, BarCode>>()

    fun generateId(vararg args: Any): String {
        var id = ""
        for (obj in args) {
            id += obj
        }
        return id
    }

    fun <T> getData(barCode: BarCode, source: Single<ResponseBody>, type: Type = genericType<Class<T>>(), expirationTime: Long = 5): Flowable<T> {
        var store = storeMap[barCode] as? Store<T, BarCode>
        if (store == null) {
            val storeBuilder = StoreBuilder.parsedWithKey<BarCode, BufferedSource, T>()
                    .fetcher { source.map(ResponseBody::source) }
                    .memoryPolicy(MemoryPolicy
                            .builder()
                            .setExpireAfterWrite(expirationTime)
                            .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                            .build())
                    .parser(GsonParserFactory.createSourceParser<T>(ApiManager.getGson(), type))
            try {
                storeBuilder.persister(SourcePersisterFactory.create(CoreApplication.instance.cacheDir))
            } catch (e: Exception) {
                Timber.e(e, "Failed to get file store")
            }

            store = storeBuilder.open()
            storeMap.put(barCode, store)
        }

        return store.get(barCode)
                // store.get() emits onComplete before all data has been propagated to the view.
                // This means that the store.fetch starts, fails immediately, and then collapses the
                // observable chain before cached data can be shown to the user.
                // You can work round this by adding a 100ms delay to the fetch call to ensure that the
                // view gets the cached data before you attempt your network call.
                .concatWith(
                        Completable.timer(100, TimeUnit.MILLISECONDS)
                                .andThen(store.fetch(barCode))
                )
                .distinct()

    }

    fun <T> getDataAsObservable(barCode: BarCode, source: Single<ResponseBody>, type: Type = genericType<Class<T>>(), expirationTime: Long = 5): Observable<T> {
        return getData<T>(barCode, source, type, expirationTime).toObservable()
    }

    private class BarCodePathResolver : PathResolver<BarCode> {
        override fun resolve(key: BarCode): String {
            return key.toString()
        }
    }
}

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
