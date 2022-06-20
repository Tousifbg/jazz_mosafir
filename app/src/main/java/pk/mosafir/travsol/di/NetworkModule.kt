package pk.mosafir.travsol.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pk.mosafir.travsol.BuildConfig
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.model.LikeDataModal
import pk.mosafir.travsol.model.LoginModel
import pk.mosafir.travsol.network.ApiInterface
import pk.mosafir.travsol.utils.getToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { Repository(get(), get(), get(), get(), get(), get(), get()) }
    single {
        Retrofit.Builder()
            .baseUrl(ApiInterface.BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build().create(ApiInterface::class.java)
    }
    single {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(ApiInterface.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiInterface.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiInterface.REQUEST_TIMEOUT, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            okHttpClient.addInterceptor(interceptor)
        }
        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val httpURL = request.url
                .newBuilder()
                .build()
            chain.proceed(
                request.newBuilder()
                    .url(httpURL)
                    .build()
            )
        }
        okHttpClient
            .addInterceptor(authInterceptor)
            .build()
    }
}