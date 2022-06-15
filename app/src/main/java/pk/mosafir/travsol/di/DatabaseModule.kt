package pk.mosafir.travsol.di

import pk.mosafir.travsol.dao.MosafirDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {MosafirDatabase.getDatabase(androidContext()) }

    single { get<MosafirDatabase>().getOffersDao() }

    single { get<MosafirDatabase>().getTourLocationDao() }

    single { get<MosafirDatabase>().getHotelLocationDao() }

    single { get<MosafirDatabase>().getTourCitiesDao() }

    single { get<MosafirDatabase>().getAirportDao() }

    single { get<MosafirDatabase>().getUserDetailDao() }
}