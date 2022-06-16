package pk.mosafir.travsol.ui

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.BuildConfig
import pk.mosafir.travsol.R
import pk.mosafir.travsol.ui.account.AccountFragment
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.viewmodel.OffersViewModel
import pk.mosafir.travsol.webview.WebViewActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var fragmentManager: FragmentManager
    private lateinit var transaction: FragmentTransaction
    private val viewModel: OffersViewModel by viewModel()
    lateinit var callbackManager:CallbackManager
    companion object {
        var fragment = ""
        lateinit var sharedPreferences: SharedPreferences
        lateinit var token: String
        lateinit var firebaseToken: String
        var userId = 0L
        lateinit var login: LoginButton
    }
    //
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("main", MODE_PRIVATE)
        fragmentManager = supportFragmentManager
        transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
        transaction.commit()
        requestForSpecificPermission()
        firebaseAnalytics = Firebase.analytics
        token = getToken()
        userId = getUserId()
        firebaseToken = getFirebaseToken()
        loggedIn = getIfLoggedIn()
        BuildConfig.slide_banner
        FacebookSdk.sdkInitialize(applicationContext)
        login = findViewById(R.id.login_button)
        login.setPermissions(
            listOf(
                "public_profile", "email", "user_birthday", "user_friends"
            )
        )
        if (getTempKey() == "12") {
            setTempKey("temp-${System.currentTimeMillis()}")
        }
        callbackManager = CallbackManager.Factory.create()
        login.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d("userLogin", "here")
                    var email = ""
                    val request = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { `object`, _ ->
                        email = `object`!!.getString("email")
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday")
                    request.parameters = parameters
                    request.executeAsync()
//                    handleFacebookAccessToken(result.accessToken)
                    //Toast.makeText(requireContext(), "login done $email", Toast.LENGTH_SHORT).show()
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {

                }
            })
        val bNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bNav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            var selectedFragment: Fragment = HomeFragment()
            when (item.itemId) {
                R.id.navigation_account -> {
                    if (loggedIn) {
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra("url", "https://mosafir.pk/mobile/Admin/dashboard")
                        startActivity(intent)
                        return@OnItemSelectedListener true
                    } else {
                        selectedFragment = AccountFragment()
                    }
                }
                R.id.navigation_booking -> {
                    if (loggedIn) {
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra("url", "https://mosafir.pk/mobile/Admin/all_bookings")
                        startActivity(intent)
                        return@OnItemSelectedListener true
                        //BookingFragment()
                    } else {
                        selectedFragment = AccountFragment()
                    }
                }
                R.id.navigation_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.navigation_payment -> {
                    if (loggedIn) {
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra("url", "https://mosafir.pk/mobile/Admin/paid_bookings")
                        startActivity(intent)
                        return@OnItemSelectedListener true
                        //BookingFragment()
                    } else {
                        selectedFragment = AccountFragment()
                    }
                }
                R.id.navigation_talk_us -> {
                    showBottomSheetDialog()
                    return@OnItemSelectedListener true
                }
            }
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            fragmentManager = supportFragmentManager
            transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, selectedFragment)
            transaction.commit()
            return@OnItemSelectedListener true
        })
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestForSpecificPermission() {
        val requestCode = 1
        val permissions = arrayOf(
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.CALL_PHONE,
            permission.READ_PHONE_STATE
        )
        if (!hasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val call: ImageView? = bottomSheetDialog.findViewById(R.id.call)
        val whatsapp: ImageView? = bottomSheetDialog.findViewById(R.id.whatsapp)
        val cancel = bottomSheetDialog.findViewById<ImageView>(R.id.cancel)

        call!!.clipToOutline = true
        whatsapp!!.clipToOutline = true
        call.setOnClickListener {
            callUs()
            bottomSheetDialog.dismiss()
        }
        cancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        whatsapp.setOnClickListener {
            whatsapp()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun whatsapp() {
        //abc
        val trimToNumber = "923010050010"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$trimToNumber/?text=")
        startActivity(intent)
    }

    private fun callUs() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:+923010050010")
        startActivity(intent)
    }

    @SuppressLint("LogNotTimber")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        var selectedFragment: Fragment
        fragmentManager = supportFragmentManager
        when (fragment) {
            "home" -> {
                selectedFragment = HomeFragment()
                transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, selectedFragment)
                transaction.commit()
            }
            "booking" -> {
                if (loggedIn) {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("url", "https://mosafir.pk/mobile/Admin/all_bookings")
                    startActivity(intent)
                    return
                } else {
                    selectedFragment = AccountFragment()
                }
                transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, selectedFragment)
                transaction.commit()
            }
            "login" -> {
                if (loggedIn) {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("url", "https://mosafir.pk/mobile/Admin/dashboard")
                    startActivity(intent)
                    return
                } else {
                    selectedFragment = AccountFragment()
                }
                transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, selectedFragment)
                transaction.commit()
            }
            "payment" -> {
                if (loggedIn) {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("url", "https://mosafir.pk/mobile/Admin/paid_bookings")
                    startActivity(intent)
                    return
                } else {
                    selectedFragment = AccountFragment()
                }
                transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, selectedFragment)
                transaction.commit()
            }
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            firebaseToken = it
            setFirebaseToken(firebaseToken)
            viewModel.putData()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1&& resultCode == RESULT_OK) {
     //       requireActivity().toast("login successful")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
       //         requireActivity().toast(account.email.toString())
                account.displayName
                account.photoUrl
            } catch (e: ApiException) {
         //       requireActivity().toast(e.toString())
            }
        }
        else{
           // requireActivity().toast("login failed")
        }
    }
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        val myFragment: HomeFragment? =
            supportFragmentManager.findFragmentByTag("MY_FRAGMENT") as HomeFragment?
        if (myFragment == null || !myFragment.isVisible) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this,
                "Please click BACK again to exit",
                Toast.LENGTH_SHORT
            ).show()
            val executor: ScheduledExecutorService? = Executors.newSingleThreadScheduledExecutor()
            executor!!.schedule({
                doubleBackToExitPressedOnce = false
            }, 1, TimeUnit.SECONDS)
        }
    }
}