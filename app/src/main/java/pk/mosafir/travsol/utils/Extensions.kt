package pk.mosafir.travsol.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import pk.mosafir.travsol.R
import pk.mosafir.travsol.model.LikeDataModal
import pk.mosafir.travsol.response.UserDetails
import pk.mosafir.travsol.ui.MainActivity.Companion.sharedPreferences

var loggedIn: Boolean = false
var temp_key: String = ""
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun Context.loadJSONFromAssets(fileName: String): String {
    return applicationContext.assets.open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}
fun saveUserDetails(userDetails: UserDetails){
    sharedPreferences.edit().putString("mobile", userDetails.mobile).apply()
    sharedPreferences.edit().putLong("user_id", userDetails.user_id!!.toLong()).apply()
}
fun setFirebaseToken(token: String){
    sharedPreferences.edit().putString("firebase_token", token).apply()
}

fun setTempKey(token: String){
    sharedPreferences.edit().putString("temp_key", token).apply()
}

fun getTempKey():String{
    return sharedPreferences.getString("temp_key", "12").toString()
}

fun getFirebaseToken(): String{
    return sharedPreferences.getString("firebase_token", "").toString()
}

fun getMobile(): String{
    return sharedPreferences.getString("mobile", "").toString()
}
fun getToken(): String{
    return sharedPreferences.getString("token", "").toString()
}

fun getUserId(): Long {
    return sharedPreferences.getLong("user_id", 0L)
}
fun getIfLoggedIn(): Boolean{
    return sharedPreferences.getBoolean("loggedin", false)
}
fun loggedInUser(id: String){
    loggedIn = true
    sharedPreferences.edit().putString("token", id).apply()
    sharedPreferences.edit().putBoolean("loggedin", true).apply()
}
fun loggedOutUser(){
    loggedIn = false
    sharedPreferences.edit().putString("token", "").apply()
    sharedPreferences.edit().putBoolean("loggedin", false).apply()
}

//flight 1, hotel 3, tour 2, discover 12, blog 14
@SuppressLint("CutPasteId", "SetTextI18n")
fun View.initialize(number: Int, context: Context, dataModal: LikeDataModal) {
    when (number) {
        1 -> {
            val dtModal = dataModal.dataList?.get(0)
            //val banner = findViewById<ImageView>(R.id.banner)
            val city = findViewById<TextView>(R.id.city)
            val moduleIcon = findViewById<ImageView>(R.id.moduleIcon)
            val heading1 = findViewById<TextView>(R.id.heading1)
            val heading2 = findViewById<TextView>(R.id.heading2)
            val price = findViewById<TextView>(R.id.price)
            val cardModule = findViewById<CardView>(R.id.cardModule)
            val cardDetail = findViewById<ConstraintLayout>(R.id.cardDetail)
            val currency = findViewById<TextView>(R.id.currency)

            city.text = dataModal.city
            when (dtModal?.id) {
                1 -> {
                    moduleIcon.setImageResource(R.drawable.flight)
                    heading1.text = "Flight To ${dtModal.to}"
                    heading2.text = "From ${dtModal.from}"
                    price.text = dtModal.price.toString()
                }
                2 -> {
                    moduleIcon.setImageResource(R.drawable.trip)
                    heading1.text = "Tour To ${dtModal.to}"
                    heading2.text = "Pakistan"
                    price.text = dtModal.price.toString()
                }
                3 -> {
                    moduleIcon.setImageResource(R.drawable.hotel)
                    heading1.text = "Hotel In ${dtModal.to}"
                    heading2.text = dtModal.to
                    price.text = dtModal.price.toString()
                }
                12 -> {
                    moduleIcon.setImageResource(R.drawable.ic_discover_icon)
                    heading1.text = "Discover ${dtModal.to}"
                    heading2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
                14 -> {
                    moduleIcon.setImageResource(R.drawable.ic_blog_icon)
                    heading1.text = "${dtModal.to} Blogs"
                    heading2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
            }
            cardModule.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardDetail.setOnClickListener {
                context.toast("card1 clicked")
            }
            //moduleIcon.setImageResource(R.drawable.ic_hotel_icon)
        }
        2 -> {
            val dtModal1 = dataModal.dataList?.get(0)
            val dtModal2 = dataModal.dataList?.get(1)
            //val banner = findViewById<ImageView>(R.id.banner)
            val city = findViewById<TextView>(R.id.city)
            val moduleIcon = findViewById<ImageView>(R.id.image1)
            val heading1 = findViewById<TextView>(R.id.heading1)
            val heading2 = findViewById<TextView>(R.id.heading2)
            val price = findViewById<TextView>(R.id.price)
            val cardModule = findViewById<CardView>(R.id.icon1)
            val cardDetail = findViewById<CardView>(R.id.card1)

            val moduleIcon2 = findViewById<ImageView>(R.id.image2)
            val heading3 = findViewById<TextView>(R.id.heading12)
            val heading4 = findViewById<TextView>(R.id.heading22)
            val price2 = findViewById<TextView>(R.id.price2)
            val cardModule2 = findViewById<CardView>(R.id.icon2)
            val cardDetail2 = findViewById<CardView>(R.id.card2)
            val currency = findViewById<TextView>(R.id.currency)
            val currency2 = findViewById<TextView>(R.id.currency2)
            city.text = dataModal.city
            when (dtModal1?.id) {
                1 -> {
                    moduleIcon.setImageResource(R.drawable.flight)
                    heading1.text = "Flight To ${dtModal1.to}"
                    heading2.text = "From ${dtModal1.from}"
                    price.text = dtModal1.price.toString()
                }
                2 -> {
                    moduleIcon.setImageResource(R.drawable.trip)
                    heading1.text = "Tour To ${dtModal1.to}"
                    heading2.text = "Pakistan"
                    price.text = dtModal1.price.toString()
                }
                3 -> {
                    moduleIcon.setImageResource(R.drawable.hotel)
                    heading1.text = "Hotel In ${dtModal1.to}"
                    heading2.text = dtModal1.to
                    price.text = dtModal1.price.toString()
                }
                12 -> {
                    moduleIcon.setImageResource(R.drawable.ic_discover_icon)
                    heading1.text = "Discover ${dtModal1.to}"
                    heading2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
                14 -> {
                    moduleIcon.setImageResource(R.drawable.ic_blog_icon)
                    heading1.text = "${dtModal1.to} Blogs"
                    heading2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }

            }
            when (dtModal2?.id) {
                1 -> {
                    moduleIcon2.setImageResource(R.drawable.flight)
                    heading3.text = "Flight To ${dtModal2.to}"
                    heading4.text = "From ${dtModal2.from}"
                    price2.text = dtModal2.price.toString()
                }
                2 -> {
                    moduleIcon2.setImageResource(R.drawable.trip)
                    heading3.text = "Tour To ${dtModal2.to}"
                    heading4.text = "Pakistan"
                    price2.text = dtModal2.price.toString()
                }
                3 -> {
                    moduleIcon2.setImageResource(R.drawable.hotel)
                    heading3.text = "Hotel In ${dtModal2.to}"
                    heading4.text = dtModal2.to
                    price2.text = dtModal2.price.toString()
                }
                12 -> {
                    moduleIcon2.setImageResource(R.drawable.ic_discover_icon)
                    heading3.text = "Discover ${dtModal2.to}"
                    heading4.text = "Pakistan"
                    currency2.invisible()
                    price2.invisible()
                }
                14 -> {
                    moduleIcon2.setImageResource(R.drawable.ic_blog_icon)
                    heading3.text = "${dtModal2.to} Blogs"
                    heading4.text = "Pakistan"
                    currency2.invisible()
                    price2.invisible()
                }

            }
            cardModule.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardDetail.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardModule2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardDetail2.setOnClickListener {
                context.toast("card2 clicked")
            }
            //moduleIcon.setImageResource(R.drawable.ic_hotel_icon)
        }
        3 -> {


            //val banner = findViewById<ImageView>(R.id.banner)
            val city = findViewById<TextView>(R.id.city)

            val moduleIcon = findViewById<ImageView>(R.id.image1)
            val heading1c1 = findViewById<TextView>(R.id.heading1)
            val heading2c1 = findViewById<TextView>(R.id.heading2)
            val price = findViewById<TextView>(R.id.price)
            val cardModule = findViewById<CardView>(R.id.icon1)
            val cardDetail = findViewById<CardView>(R.id.card1)

            val moduleIcon2 = findViewById<ImageView>(R.id.image2)
            val heading1c2 = findViewById<TextView>(R.id.heading12)
            val heading2c2 = findViewById<TextView>(R.id.heading22)
            val price2 = findViewById<TextView>(R.id.price2)
            val cardModule2 = findViewById<CardView>(R.id.icon2)
            val cardDetail2 = findViewById<CardView>(R.id.card2)

            val moduleIcon3 = findViewById<ImageView>(R.id.image3)
            val heading1c3 = findViewById<TextView>(R.id.heading13)
            val heading2c3 = findViewById<TextView>(R.id.heading23)
            val price3 = findViewById<TextView>(R.id.price3)
            val cardModule3 = findViewById<CardView>(R.id.icon3)
            val cardDetail3 = findViewById<CardView>(R.id.card3)
            val currency = findViewById<TextView>(R.id.currency)
            val currency2 = findViewById<TextView>(R.id.currency2)
            val currency3 = findViewById<TextView>(R.id.currency3)


            city.text = dataModal.city
            val dtModal1 = dataModal.dataList?.get(0)
            val dtModal2 = dataModal.dataList?.get(1)
            val dtModal3 = dataModal.dataList?.get(2)
            when (dtModal1?.id) {
                1 -> {
                    moduleIcon.setImageResource(R.drawable.flight)
                    heading1c1.text = "Flight To ${dtModal1.to}"
                    heading2c1.text = "From ${dtModal1.from}"
                    price.text = dtModal1.price.toString()
                }
                2 -> {
                    moduleIcon.setImageResource(R.drawable.trip)
                    heading1c1.text = "Tour To ${dtModal1.to}"
                    heading1c2.text = "Pakistan"
                    price.text = dtModal1.price.toString()
                }
                3 -> {
                    moduleIcon.setImageResource(R.drawable.hotel)
                    heading1c1.text = "Hotel In ${dtModal1.to}"
                    heading1c2.text = dtModal1.to
                    price.text = dtModal1.price.toString()
                }
                12 -> {
                    moduleIcon.setImageResource(R.drawable.ic_discover_icon)
                    heading1c1.text = "Discover ${dtModal1.to}"
                    heading1c2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
                14 -> {
                    moduleIcon.setImageResource(R.drawable.ic_blog_icon)
                    heading1c1.text = "${dtModal1.to} Blogs"
                    heading2c1.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }

            }
            when (dtModal2?.id) {
                1 -> {
                    moduleIcon2.setImageResource(R.drawable.flight)
                    heading1c2.text = "Flight To ${dtModal2.to}"
                    heading2c2.text = "From ${dtModal2.from}"
                    price2.text = dtModal2.price.toString()
                }
                2 -> {
                    moduleIcon2.setImageResource(R.drawable.trip)
                    heading1c2.text = "Tour To ${dtModal2.to}"
                    heading2c2.text = "Pakistan"
                    price2.text = dtModal2.price.toString()
                }
                3 -> {
                    moduleIcon2.setImageResource(R.drawable.hotel)
                    heading1c2.text = "Hotel In ${dtModal2.to}"
                    heading2c2.text = dtModal2.to
                    price2.text = dtModal2.price.toString()
                }
                12 -> {
                    moduleIcon2.setImageResource(R.drawable.ic_discover_icon)
                    heading1c2.text = "Discover ${dtModal2.to}"
                    heading2c2.text = "Pakistan"
                    currency2.invisible()
                    price2.invisible()
                }
                14 -> {
                    moduleIcon2.setImageResource(R.drawable.ic_blog_icon)
                    heading1c2.text = "${dtModal2.to} Blogs"
                    heading2c2.text = "Pakistan"
                    currency2.invisible()
                    price2.invisible()
                }
            }
            when (dtModal3?.id) {
                1 -> {
                    moduleIcon3.setImageResource(R.drawable.flight)
                    heading1c3.text = "Flight To ${dtModal3.to}"
                    heading2c3.text = "From ${dtModal3.from}"
                    price3.text = dtModal3.price.toString()
                }
                2 -> {
                    moduleIcon3.setImageResource(R.drawable.trip)
                    heading1c3.text = "Tour To ${dtModal3.to}"
                    heading2c3.text = "Pakistan"
                    price3.text = dtModal3.price.toString()
                }
                3 -> {
                    moduleIcon3.setImageResource(R.drawable.hotel)
                    heading1c3.text = "Hotel In ${dtModal3.to}"
                    heading2c3.text = dtModal3.to
                    price3.text = dtModal3.price.toString()
                }
                12 -> {
                    moduleIcon3.setImageResource(R.drawable.ic_discover_icon)
                    heading1c3.text = "Discover ${dtModal3.to}"
                    heading2c3.text = "Pakistan"
                    currency3.invisible()
                    price3.invisible()
                }
                14 -> {
                    moduleIcon3.setImageResource(R.drawable.ic_blog_icon)
                    heading1c3.text = "${dtModal3.to} Blogs"
                    heading2c3.text = "Pakistan"
                    currency3.invisible()
                    price3.invisible()
                }
            }
            cardModule.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardDetail.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardModule2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardDetail2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardModule3.setOnClickListener {
                context.toast("card3 clicked")
            }
            cardDetail3.setOnClickListener {
                context.toast("card3 clicked")
            }
            moduleIcon.setImageResource(R.drawable.ic_hotel_icon)
        }
        4 -> {
            //val banner = findViewById<ImageView>(R.id.banner)
            val city = findViewById<TextView>(R.id.city)

            val moduleIcon = findViewById<ImageView>(R.id.image1)
            val heading1c1 = findViewById<TextView>(R.id.heading1)
            val heading2c1 = findViewById<TextView>(R.id.heading2)
            val price = findViewById<TextView>(R.id.price)
            val cardModule = findViewById<CardView>(R.id.icon1)
            val cardDetail = findViewById<CardView>(R.id.card1)

            val moduleIcon2 = findViewById<ImageView>(R.id.image2)
            val heading1c2 = findViewById<TextView>(R.id.heading12)
            val heading2c2 = findViewById<TextView>(R.id.heading22)
            val price2 = findViewById<TextView>(R.id.price2)
            val cardModule2 = findViewById<CardView>(R.id.icon2)
            val cardDetail2 = findViewById<CardView>(R.id.card2)

            val moduleIcon3 = findViewById<ImageView>(R.id.image3)
            val heading1c3 = findViewById<TextView>(R.id.heading13)
            val heading2c3 = findViewById<TextView>(R.id.heading23)
            val price3 = findViewById<TextView>(R.id.price3)
            val cardModule3 = findViewById<CardView>(R.id.icon3)
            val cardDetail3 = findViewById<CardView>(R.id.card3)

            val moduleIcon4 = findViewById<ImageView>(R.id.image4)
            val heading1c4 = findViewById<TextView>(R.id.heading14)
            val heading2c4 = findViewById<TextView>(R.id.heading24)
            val price4 = findViewById<TextView>(R.id.price4)
            val cardModule4 = findViewById<CardView>(R.id.icon4)
            val cardDetail4 = findViewById<CardView>(R.id.card4)

            val currency = findViewById<TextView>(R.id.currency)
            val currency2 = findViewById<TextView>(R.id.currency2)
            val currency3 = findViewById<TextView>(R.id.currency3)
            val currency4 = findViewById<TextView>(R.id.currency4)

            city.text = dataModal.city

            val dtModal1 = dataModal.dataList?.get(0)
            val dtModal2 = dataModal.dataList?.get(1)
            val dtModal3 = dataModal.dataList?.get(2)
            val dtModal4 = dataModal.dataList?.get(3)
            when (dtModal1?.id) {
                1 -> {
                    moduleIcon.setImageResource(R.drawable.flight)
                    heading1c1.text = "Flight To ${dtModal1.to}"
                    heading2c1.text = "From ${dtModal1.from}"
                    price.text = dtModal1.price.toString()
                }
                2 -> {
                    moduleIcon.setImageResource(R.drawable.trip)
                    heading1c1.text = "Tour To ${dtModal1.to}"
                    heading1c2.text = "Pakistan"
                    price.text = dtModal1.price.toString()
                }
                3 -> {
                    moduleIcon.setImageResource(R.drawable.hotel)
                    heading1c1.text = "Hotel In ${dtModal1.to}"
                    heading1c2.text = dtModal1.to
                    price.text = dtModal1.price.toString()
                }
                12 -> {
                    moduleIcon.setImageResource(R.drawable.ic_discover_icon)
                    heading1c1.text = "Discover ${dtModal1.to}"
                    heading1c2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
                14 -> {
                    moduleIcon.setImageResource(R.drawable.ic_blog_icon)
                    heading1c1.text = "${dtModal1.to} Blogs"
                    heading2c1.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
            }
            when (dtModal2?.id) {
                1 -> {
                    moduleIcon2.setImageResource(R.drawable.flight)
                    heading1c2.text = "Flight To ${dtModal2.to}"
                    heading2c2.text = "From ${dtModal2.from}"
                    price2.text = dtModal2.price.toString()
                }
                2 -> {
                    moduleIcon2.setImageResource(R.drawable.trip)
                    heading1c2.text = "Tour To ${dtModal2.to}"
                    heading2c2.text = "Pakistan"
                    price2.text = dtModal2.price.toString()
                }
                3 -> {
                    moduleIcon2.setImageResource(R.drawable.hotel)
                    heading1c2.text = "Hotel In ${dtModal2.to}"
                    heading2c2.text = dtModal2.to
                    price2.text = dtModal2.price.toString()
                }
                12 -> {
                    moduleIcon2.setImageResource(R.drawable.ic_discover_icon)
                    heading1c2.text = "Discover ${dtModal2.to}"
                    heading2c2.text = "Pakistan"
                    currency2.invisible()
                    price2.invisible()
                }
                14 -> {
                    currency2.invisible()
                    price2.invisible()
                    moduleIcon2.setImageResource(R.drawable.ic_blog_icon)
                    heading1c2.text = "${dtModal2.to} Blogs"
                    heading2c2.text = "Pakistan"
                }
            }
            when (dtModal3?.id) {
                1 -> {
                    moduleIcon3.setImageResource(R.drawable.flight)
                    heading1c3.text = "Flight To ${dtModal3.to}"
                    heading2c3.text = "From ${dtModal3.from}"
                    price3.text = dtModal3.price.toString()
                }
                2 -> {
                    moduleIcon3.setImageResource(R.drawable.trip)
                    heading1c3.text = "Tour To ${dtModal3.to}"
                    heading2c3.text = "Pakistan"
                    price3.text = dtModal3.price.toString()
                }
                3 -> {
                    moduleIcon3.setImageResource(R.drawable.hotel)
                    heading1c3.text = "Hotel In ${dtModal3.to}"
                    heading2c3.text = dtModal3.to
                    price3.text = dtModal3.price.toString()
                }
                12 -> {
                    currency3.invisible()
                    price3.invisible()
                    moduleIcon3.setImageResource(R.drawable.ic_discover_icon)
                    heading1c3.text = "Discover ${dtModal3.to}"
                    heading2c3.text = "Pakistan"
                }
                14 -> {
                    currency3.invisible()
                    price3.invisible()
                    moduleIcon3.setImageResource(R.drawable.ic_blog_icon)
                    heading1c3.text = "${dtModal3.to} Blogs"
                    heading2c3.text = "Pakistan"
                }
            }
            when (dtModal4?.id) {
                1 -> {
                    moduleIcon4.setImageResource(R.drawable.flight)
                    heading1c4.text = "Flight To ${dtModal4.to}"
                    heading2c4.text = "From ${dtModal4.from}"
                    price4.text = dtModal4.price.toString()
                }
                2 -> {
                    moduleIcon4.setImageResource(R.drawable.trip)
                    heading1c4.text = "Tour To ${dtModal4.to}"
                    heading2c4.text = "Pakistan"
                    price4.text = dtModal4.price.toString()
                }
                3 -> {
                    moduleIcon4.setImageResource(R.drawable.hotel)
                    heading1c4.text = "Hotel In ${dtModal4.to}"
                    heading2c4.text = dtModal4.to
                    price4.text = dtModal4.price.toString()
                }
                12 -> {
                    currency4.invisible()
                    price4.invisible()
                    moduleIcon4.setImageResource(R.drawable.ic_discover_icon)
                    heading1c4.text = "Discover ${dtModal4.to}"
                    heading2c4.text = "Pakistan"
                }
                14 -> {
                    currency4.invisible()
                    price4.invisible()
                    moduleIcon4.setImageResource(R.drawable.ic_blog_icon)
                    heading1c4.text = "${dtModal4.to} Blogs"
                    heading2c4.text = "Pakistan"
                }
            }
            when (dtModal4?.id) {
                1 -> {
                    moduleIcon4.setImageResource(R.drawable.flight)
                    heading1c4.text = "Flight To ${dtModal4.to}"
                    heading2c4.text = "From ${dtModal4.from}"
                    price4.text = dtModal4.price.toString()
                }
                2 -> {
                    moduleIcon4.setImageResource(R.drawable.trip)
                    heading1c4.text = "Tour To ${dtModal4.to}"
                    heading2c4.text = "Pakistan"
                    price4.text = dtModal4.price.toString()
                }
                3 -> {
                    moduleIcon4.setImageResource(R.drawable.hotel)
                    heading1c4.text = "Hotel In ${dtModal4.to}"
                    heading2c4.text = dtModal4.to
                    price4.text = dtModal4.price.toString()
                }
                12 -> {
                    moduleIcon4.setImageResource(R.drawable.ic_discover_icon)
                    heading1c4.text = "Discover ${dtModal4.to}"
                    heading2c4.text = "Pakistan"
                    currency4.invisible()
                    price4.invisible()
                }
                14 -> {
                    currency4.invisible()
                    price4.invisible()
                    moduleIcon4.setImageResource(R.drawable.ic_blog_icon)
                    heading1c4.text = "${dtModal4.to} Blogs"
                    heading2c4.text = "Pakistan"
                }
            }
            cardModule.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardDetail.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardModule2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardDetail2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardModule3.setOnClickListener {
                context.toast("card3 clicked")
            }
            cardDetail3.setOnClickListener {
                context.toast("card3 clicked")
            }
            cardModule4.setOnClickListener {
                context.toast("card4 clicked")
            }
            cardDetail4.setOnClickListener {
                context.toast("card4 clicked")
            }
        }
        5 -> {
            //val banner = findViewById<ImageView>(R.id.banner)
            val city = findViewById<TextView>(R.id.city)

            val moduleIcon = findViewById<ImageView>(R.id.image1)
            val heading1c1 = findViewById<TextView>(R.id.heading1)
            val heading2c1 = findViewById<TextView>(R.id.heading2)
            val price = findViewById<TextView>(R.id.price)
            val cardModule = findViewById<CardView>(R.id.icon1)
            val cardDetail = findViewById<CardView>(R.id.card1)

            val moduleIcon2 = findViewById<ImageView>(R.id.image2)
            val heading1c2 = findViewById<TextView>(R.id.heading12)
            val heading2c2 = findViewById<TextView>(R.id.heading22)
            val price2 = findViewById<TextView>(R.id.price2)
            val cardModule2 = findViewById<CardView>(R.id.icon2)
            val cardDetail2 = findViewById<CardView>(R.id.card2)

            val moduleIcon3 = findViewById<ImageView>(R.id.image3)
            val heading1c3 = findViewById<TextView>(R.id.heading13)
            val heading2c3 = findViewById<TextView>(R.id.heading23)
            val price3 = findViewById<TextView>(R.id.price3)
            val cardModule3 = findViewById<CardView>(R.id.icon3)
            val cardDetail3 = findViewById<CardView>(R.id.card3)

            val moduleIcon4 = findViewById<ImageView>(R.id.image4)
            val heading1c4 = findViewById<TextView>(R.id.heading14)
            val heading2c4 = findViewById<TextView>(R.id.heading24)
            val price4 = findViewById<TextView>(R.id.price4)
            val cardModule4 = findViewById<CardView>(R.id.icon4)
            val cardDetail4 = findViewById<CardView>(R.id.card4)

            val moduleIcon5 = findViewById<ImageView>(R.id.image5)
            val heading1c5 = findViewById<TextView>(R.id.heading15)
            val heading2c5 = findViewById<TextView>(R.id.heading25)
            val price5 = findViewById<TextView>(R.id.price5)
            val cardModule5 = findViewById<CardView>(R.id.icon5)
            val cardDetail5 = findViewById<CardView>(R.id.card5)

            val currency = findViewById<TextView>(R.id.currency)
            val currency2 = findViewById<TextView>(R.id.currency2)
            val currency3 = findViewById<TextView>(R.id.currency3)
            val currency4 = findViewById<TextView>(R.id.currency4)
            val currency5 = findViewById<TextView>(R.id.currency5)

            city.text = dataModal.city
            val dtModal1 = dataModal.dataList?.get(0)
            val dtModal2 = dataModal.dataList?.get(1)
            val dtModal3 = dataModal.dataList?.get(2)
            val dtModal4 = dataModal.dataList?.get(3)
            val dtModal5 = dataModal.dataList?.get(4)
            when (dtModal1?.id) {
                1 -> {
                    moduleIcon.setImageResource(R.drawable.flight)
                    heading1c1.text = "Flight To ${dtModal1.to}"
                    heading2c1.text = "From ${dtModal1.from}"
                    price.text = dtModal1.price.toString()
                }
                2 -> {
                    moduleIcon.setImageResource(R.drawable.trip)
                    heading1c1.text = "Tour To ${dtModal1.to}"
                    heading1c2.text = "Pakistan"
                    price.text = dtModal1.price.toString()
                }
                3 -> {
                    moduleIcon.setImageResource(R.drawable.hotel)
                    heading1c1.text = "Hotel In ${dtModal1.to}"
                    heading1c2.text = dtModal1.to
                    price.text = dtModal1.price.toString()
                }
                12 -> {
                    moduleIcon.setImageResource(R.drawable.ic_discover_icon)
                    heading1c1.text = "Discover ${dtModal1.to}"
                    heading1c2.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
                14 -> {
                    moduleIcon.setImageResource(R.drawable.ic_blog_icon)
                    heading1c1.text = "${dtModal1.to} Blogs"
                    heading2c1.text = "Pakistan"
                    currency.invisible()
                    price.invisible()
                }
            }
            when (dtModal2?.id) {
                1 -> {
                    moduleIcon2.setImageResource(R.drawable.flight)
                    heading1c2.text = "Flight To ${dtModal2.to}"
                    heading2c2.text = "From ${dtModal2.from}"
                    price2.text = dtModal2.price.toString()
                }
                2 -> {
                    moduleIcon2.setImageResource(R.drawable.trip)
                    heading1c2.text = "Tour To ${dtModal2.to}"
                    heading2c2.text = "Pakistan"
                    price2.text = dtModal2.price.toString()
                }
                3 -> {
                    moduleIcon2.setImageResource(R.drawable.hotel)
                    heading1c2.text = "Hotel In ${dtModal2.to}"
                    heading2c2.text = dtModal2.to
                    price2.text = dtModal2.price.toString()
                }
                12 -> {
                    moduleIcon2.setImageResource(R.drawable.ic_discover_icon)
                    heading1c2.text = "Discover ${dtModal2.to}"
                    heading2c2.text = "Pakistan"
                    currency2.invisible()
                    price2.invisible()
                }
                14 -> {
                    currency2.invisible()
                    price2.invisible()
                    moduleIcon2.setImageResource(R.drawable.ic_blog_icon)
                    heading1c2.text = "${dtModal2.to} Blogs"
                    heading2c2.text = "Pakistan"
                }
            }
            when (dtModal3?.id) {
                1 -> {
                    moduleIcon3.setImageResource(R.drawable.flight)
                    heading1c3.text = "Flight To ${dtModal3.to}"
                    heading2c3.text = "From ${dtModal3.from}"
                    price3.text = dtModal3.price.toString()
                }
                2 -> {
                    moduleIcon3.setImageResource(R.drawable.trip)
                    heading1c3.text = "Tour To ${dtModal3.to}"
                    heading2c3.text = "Pakistan"
                    price3.text = dtModal3.price.toString()
                }
                3 -> {
                    moduleIcon3.setImageResource(R.drawable.hotel)
                    heading1c3.text = "Hotel In ${dtModal3.to}"
                    heading2c3.text = dtModal3.to
                    price3.text = dtModal3.price.toString()
                }
                12 -> {
                    currency3.invisible()
                    price3.invisible()
                    moduleIcon3.setImageResource(R.drawable.ic_discover_icon)
                    heading1c3.text = "Discover ${dtModal3.to}"
                    heading2c3.text = "Pakistan"
                }
                14 -> {
                    currency3.invisible()
                    price3.invisible()
                    moduleIcon3.setImageResource(R.drawable.ic_blog_icon)
                    heading1c3.text = "${dtModal3.to} Blogs"
                    heading2c3.text = "Pakistan"
                }
            }
            when (dtModal4?.id) {
                1 -> {
                    moduleIcon4.setImageResource(R.drawable.flight)
                    heading1c4.text = "Flight To ${dtModal4.to}"
                    heading2c4.text = "From ${dtModal4.from}"
                    price4.text = dtModal4.price.toString()
                }
                2 -> {
                    moduleIcon4.setImageResource(R.drawable.trip)
                    heading1c4.text = "Tour To ${dtModal4.to}"
                    heading2c4.text = "Pakistan"
                    price4.text = dtModal4.price.toString()
                }
                3 -> {
                    moduleIcon4.setImageResource(R.drawable.hotel)
                    heading1c4.text = "Hotel In ${dtModal4.to}"
                    heading2c4.text = dtModal4.to
                    price4.text = dtModal4.price.toString()
                }
                12 -> {
                    moduleIcon4.setImageResource(R.drawable.ic_discover_icon)
                    heading1c4.text = "Discover ${dtModal4.to}"
                    heading2c4.text = "Pakistan"
                    currency4.invisible()
                    price4.invisible()
                }
                14 -> {
                    currency4.invisible()
                    price4.invisible()
                    moduleIcon4.setImageResource(R.drawable.ic_blog_icon)
                    heading1c4.text = "${dtModal4.to} Blogs"
                    heading2c4.text = "Pakistan"
                }
            }
            when (dtModal5?.id) {
                1 -> {
                    moduleIcon5.setImageResource(R.drawable.flight)
                    heading1c5.text = "Flight To ${dtModal5.to}"
                    heading2c5.text = "From ${dtModal5.from}"
                    price5.text = dtModal5.price.toString()
                }
                2 -> {
                    moduleIcon5.setImageResource(R.drawable.trip)
                    heading1c5.text = "Tour To ${dtModal5.to}"
                    heading2c5.text = "Pakistan"
                    price5.text = dtModal5.price.toString()
                }
                3 -> {
                    moduleIcon5.setImageResource(R.drawable.hotel)
                    heading1c5.text = "Hotel In ${dtModal5.to}"
                    heading2c5.text = dtModal5.to
                    price5.text = dtModal5.price.toString()
                }
                12 -> {
                    currency5.invisible()
                    price5.invisible()
                    moduleIcon5.setImageResource(R.drawable.ic_discover_icon)
                    heading1c5.text = "Discover ${dtModal5.to}"
                    heading2c5.text = "Pakistan"
                }
                14 -> {
                    moduleIcon5.setImageResource(R.drawable.ic_blog_icon)
                    heading1c5.text = "${dtModal5.to} Blogs"
                    heading2c5.text = "Pakistan"
                    currency5.invisible()
                    price5.invisible()
                }
            }
            cardModule.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardDetail.setOnClickListener {
                context.toast("card1 clicked")
            }
            cardModule2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardDetail2.setOnClickListener {
                context.toast("card2 clicked")
            }
            cardModule3.setOnClickListener {
                context.toast("card3 clicked")
            }
            cardDetail3.setOnClickListener {
                context.toast("card3 clicked")
            }
            cardModule4.setOnClickListener {
                context.toast("card4 clicked")
            }
            cardDetail4.setOnClickListener {
                context.toast("card4 clicked")
            }
            cardModule5.setOnClickListener {
                context.toast("card5 clicked")
            }
            cardDetail5.setOnClickListener {
                context.toast("card5 clicked")
            }
        }
    }
}
@SuppressLint("UseCompatLoadingForColorStateLists", "NewApi")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun TextView.normalize(view: TextView) {
    view.backgroundTintList = resources.getColorStateList(R.color.colorGray, null)
    view.setTextColor(resources.getColor(R.color.colorBlack, null))
    view.isClickable = true
}

@SuppressLint("UseCompatLoadingForColorStateLists", "NewApi")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun TextView.special(view: TextView) {
    view.backgroundTintList = resources.getColorStateList(R.color.colorRed, null)
    view.setTextColor(resources.getColor(R.color.colorRed, null))
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}