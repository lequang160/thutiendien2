package com.xep.thutiendien.module

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Loggable
import com.xep.thutiendien.Constrains
import com.xep.thutiendien.ElectricityApplication
import com.xep.thutiendien.services.AppApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object Network {

    private val file: File = ElectricityApplication.context.filesDir

    private val cache: Cache = Cache(file, 10 * 1024 * 1024)
    private val adapter: CoroutineCallAdapterFactory =
        CoroutineCallAdapterFactory()
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()

    private val okHttpClient: OkHttpClient =
        try { // Create a trust manager that does not validate certificate chains
            val trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {

                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                (trustAllCerts[0] as X509TrustManager)
            )
                .hostnameVerifier(HostnameVerifier { _, _ -> true })
                .addNetworkInterceptor(CurlInterceptor(Loggable { message: String ->
                    Timber.e(
                        message
                    )
                }))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .cache(cache)
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    private val  retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(Constrains.BASE_URL)
        .addConverterFactory(gsonFactory)
        .addCallAdapterFactory(adapter).build()

    val appApi = retrofit.create(AppApi::class.java)
}