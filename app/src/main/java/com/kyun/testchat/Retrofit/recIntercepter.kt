package com.kyun.testchat.Retrofit

import com.kyun.testchat.Realm.Cookie
import com.kyun.testchat.Util.Singleton
import io.realm.Realm
import okhttp3.Interceptor
import okhttp3.Response

class recIntercepter : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = HashSet<String>()

            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }

            val realm = Realm.getInstance(Singleton.mConfig)
            realm.executeTransaction {
                for(set in cookies)
                    it.insert(Cookie().set(set))
            }
            realm.close()

        }

        return originalResponse
    }
}