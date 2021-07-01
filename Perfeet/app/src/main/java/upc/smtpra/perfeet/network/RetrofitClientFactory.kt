package upc.smtpra.perfeet.network

import com.google.gson.GsonBuilder
import upc.smtpra.perfeet.PerFeetApp.Companion.getAppInstance
import upc.smtpra.perfeet.PerFeetApp.Companion.okHttpClient
import upc.smtpra.perfeet.utils.Constants.Config.BASE_URL
import upc.smtpra.perfeet.utils.Constants.Config.DEFAULT_URL
import upc.smtpra.perfeet.utils.Constants.Config.consumerKey
import upc.smtpra.perfeet.utils.Constants.Config.consumerSecret
import upc.smtpra.perfeet.utils.Constants.Config.token
import upc.smtpra.perfeet.utils.Constants.Config.tokenSecret
import upc.smtpra.perfeet.utils.oauthInterceptor.OAuthInterceptor
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class RetrofitClientFactory {

    fun getRetroFitClient(b: Boolean = true): Retrofit {
        val oauth1WooCommerce = OAuthInterceptor.Builder().consumerKey(consumerKey).consumerSecret(consumerSecret).token(token).tokenSecret(tokenSecret).build()

        val builder = OkHttpClient().newBuilder().connectTimeout(3, TimeUnit.MINUTES).readTimeout(3, TimeUnit.MINUTES)

        okHttpClient = if (b) {
            builder.addInterceptor(ResponseInterceptor()).addInterceptor(oauth1WooCommerce).build()
        } else {
            builder.addInterceptor(ResponseInterceptor()).build()
        }

        val gson = GsonBuilder().setLenient().disableHtmlEscaping().create()

        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient!!).addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
}

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), response.body()!!.bytes())).build()
    }
}

fun cancelAllApi() {
    okHttpClient?.dispatcher()?.cancelAll()
}