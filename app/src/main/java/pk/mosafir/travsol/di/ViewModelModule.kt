package pk.mosafir.travsol.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pk.mosafir.travsol.viewmodel.*

val viewModelModule = module {

    viewModel { PlanTourViewModel(get(),get()) }

    viewModel { OffersViewModel(get(), get()) }

    viewModel { BookHotelViewModel(get(),get()) }

    viewModel { FlightViewModel(get(), get(),get()) }

    viewModel { AccountViewModel(get()) }

    viewModel { LoggedInViewModel(get()) }

    viewModel { UserInfoViewModel(get(), get()) }
}