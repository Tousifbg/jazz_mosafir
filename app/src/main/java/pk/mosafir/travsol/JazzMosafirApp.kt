package pk.mosafir.travsol

import android.annotation.SuppressLint
import android.app.Application
import pk.mosafir.travsol.di.databaseModule
import pk.mosafir.travsol.di.networkModule
import pk.mosafir.travsol.di.viewModelModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

@SuppressLint("Registered")
class JazzMosafirApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@JazzMosafirApp)
            modules(listOf(networkModule, viewModelModule, databaseModule))
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}