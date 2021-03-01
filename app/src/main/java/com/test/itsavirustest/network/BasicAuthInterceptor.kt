package com.test.itsavirustest.network

import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class BasicAuthInterceptor(user: String?, password: String?) :
    Interceptor {
    private val credentials: String

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

    init {
        credentials = user?.let { password?.let { it1 -> Credentials.basic(it, it1) } }.toString()
    }
}