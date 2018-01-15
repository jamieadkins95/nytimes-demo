package com.jamieadkins.nytimesdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.nytimes.android.external.store3.base.impl.BarCode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    val disposable = CompositeDisposable()
    var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
    }

    override fun onStart() {
        super.onStart()
        val reqId = StoreManager.generateId("News")
        val barcode = BarCode("Demo", reqId)
        val subscriber = StoreManager.getData<ApiResponse>(barcode, ApiManager.getPulseliveApi().getArticles(),
                genericType<ApiResponse>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<ApiResponse>() {
                    override fun onNext(t: ApiResponse?) {
                        Timber.d("onNext")
                        val message = "Items count: " + t?.items?.size.toString()
                        textView?.text = message
                        Timber.d(message)
                    }

                    override fun onComplete() {
                        Timber.d("OnComplete")
                    }

                    override fun onError(t: Throwable?) {
                        Timber.e(t, "onError")
                        Toast.makeText(this@MainActivity, t?.message, Toast.LENGTH_LONG).show()
                    }
                })
        disposable.add(subscriber)
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}
