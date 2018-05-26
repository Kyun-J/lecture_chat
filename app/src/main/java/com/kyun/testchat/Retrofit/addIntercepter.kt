package com.kyun.testchat.Retrofit

import com.kyun.testchat.Realm.Cookie
import com.kyun.testchat.Util.Singleton
import io.realm.Realm
import okhttp3.Interceptor
import okhttp3.Response

class addIntercepter() : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        //Preference에서 cookies를 가져오는 작업을 수행
        val realm = Realm.getInstance(Singleton.mConfig)

        val cookies = realm.where(Cookie::class.java).findAll()

        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie.Contents)
        }

        // Web,Android,iOS 구분을 위해 User-Agent세팅
        builder.removeHeader("User-Agent").addHeader("User-Agent", "Android")

        realm.close()

        return chain.proceed(builder.build())
    }
}