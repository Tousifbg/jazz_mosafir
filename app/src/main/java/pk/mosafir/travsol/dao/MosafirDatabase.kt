package pk.mosafir.travsol.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pk.mosafir.travsol.response.*

@Database(
    entities = [Offer::class, TourCity::class, HotelLocation::class, TourLocation::class, UserDetails::class, DiscoverPakistanCity::class,FlyFrom10::class],
    version = 1,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(
//            from = 4, to = 5
//        )
//    ]
)
abstract class MosafirDatabase : RoomDatabase() {
    abstract fun getOffersDao(): OffersDao
    abstract fun getTourCitiesDao(): TourCityDao
    abstract fun getTourLocationDao(): TourLocationDao
    abstract fun getHotelLocationDao(): HotelLocationDao
    abstract fun getAirportDao(): AirportDao
    abstract fun getUserDetailDao(): UserDetailDao

    companion object {
        @Volatile
        private var INSTANCE: MosafirDatabase? = null

        fun getDatabase(context: Context): MosafirDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MosafirDatabase::class.java,
                    "movies_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}