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
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.BuildConfig
import pk.mosafir.travsol.R
import pk.mosafir.travsol.interfaces.SocialLoginInterface
import pk.mosafir.travsol.model.SocialLoginModel
import pk.mosafir.travsol.ui.account.AccountFragment
import pk.mosafir.travsol.ui.account.LoggedInFragment
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.viewmodel.OffersViewModel
import pk.mosafir.travsol.webview.WebViewActivity
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var fragmentManager: FragmentManager
    private lateinit var transaction: FragmentTransaction
    private val viewModel: OffersViewModel by viewModel()
    private lateinit var callbackManager: CallbackManager

    companion object {
        var runningFragment = ""
        var fragment = ""
        lateinit var sharedPreferences: SharedPreferences

        //        lateinit var token: String
        lateinit var firebaseToken: String
        lateinit var login: LoginButton
        lateinit var googlelogin: Button

        lateinit var mGoogleSignInClient: GoogleSignInClient
        const val RC_SIGN_IN = 9001

        //we want to call social login api in account fragment instead of main activity
        //for that we used interface to send social data to account fragment and then call api there
        lateinit var socialLoginInterface: SocialLoginInterface

        fun socialLogin(which: String, result: SocialLoginInterface) {
            when (which) {
                "1" -> {
                    login.performClick()
                }
                "2" -> {
                    googlelogin.performClick()
                }
            }
            socialLoginInterface = result
        }

        fun logoutSocial() {
            try {
                LoginManager.getInstance().logOut()
                mGoogleSignInClient.signOut()
            } catch (e: Exception) {

            }
        }
    }

    /////
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
        firebaseToken = getFirebaseToken()
        loggedIn = getIfLoggedIn()
        BuildConfig.slide_banner

        if (getTempKey() == "12") {
            setTempKey("temp-${System.currentTimeMillis()}")
        }
        FacebookSdk.sdkInitialize(applicationContext)
        login = findViewById(R.id.login_button)
        login.setPermissions(
            listOf(
                "public_profile", "email", "user_birthday", "user_friends"
            )
        )
        callbackManager = CallbackManager.Factory.create()
        login.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    var email: String
                    var name: String
                    var imageURL: String
                    var authID: String
                    var authTYPE: String
                    val request = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { jsonObject, _ ->

                        email = jsonObject!!.getString("email")

                        name = jsonObject.getString("name")

                        imageURL = "https://graph.facebook.com/${
                            jsonObject.getString("id")
                        }/picture?width=500&height=500"

                        authID = result.accessToken.userId

                        authTYPE = "FB"
                        val socialLoginModel =
                            SocialLoginModel(email, name, imageURL, authID, authTYPE)
                        socialLoginInterface.updated(socialLoginModel)

//                        postSocialData(email, name, imageURL, authID, authTYPE)

                        //displaying profile img in custom dialog
                        //showDialog(imageURL)
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
                    toast("FB_ERROR: " + error.message)
                }
            })

        //google signin
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)

        googlelogin = findViewById(R.id.googleLoginBtn)
        googlelogin.setOnClickListener {
            SignInGoogle()

        }

        val bNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bNav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            var selectedFragment: Fragment = HomeFragment()
            when (item.itemId) {

                R.id.navigation_account -> {
                    selectedFragment = if (loggedIn) {
                        LoggedInFragment()
                        return@OnItemSelectedListener true
                    } else {
                        AccountFragment()
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

    private fun SignInGoogle() {
        val signinIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent, RC_SIGN_IN)
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
        val selectedFragment: Fragment
//        fragmentManager = supportFragmentManager
        when (fragment) {
            "home" -> {
                selectedFragment = HomeFragment()
                openFragment(selectedFragment)
            }
            "booking" -> {
                if (loggedIn) {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("url", "https://mosafir.pk/mobile/Admin/all_bookings")
                    startActivity(intent)
                    return
                } else {
                    selectedFragment = AccountFragment()
                    openFragment(selectedFragment)
                }
            }
            "login" -> {
                selectedFragment = if (loggedIn) {
                    LoggedInFragment()
                } else {
                    AccountFragment()
                }
                openFragment(selectedFragment)
            }
            "payment" -> {
                if (loggedIn) {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("url", "https://mosafir.pk/mobile/Admin/paid_bookings")
                    startActivity(intent)
                    return
                } else {
                    selectedFragment = AccountFragment()
                    openFragment(selectedFragment)
                }
            }

        }
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            firebaseToken = it
            setFirebaseToken(firebaseToken)
            viewModel.putData()
        }
    }

    private fun openFragment(selectedFragment: Fragment) {
        transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, selectedFragment)
        transaction.commit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val googleID = account?.id ?: ""
            val googleFirstName = account?.givenName ?: ""
            val googleLastName = account?.familyName ?: ""
            val googleEmail = account?.email ?: ""
            val googleProfilePicURL = account?.photoUrl.toString()
            val socialLoginModel = SocialLoginModel(
                googleEmail,
                "$googleFirstName $googleLastName", googleProfilePicURL, googleID, "GOOGLE"
            )
            socialLoginInterface.updated(socialLoginModel)
        } catch (e: ApiException) {
            Timber.e(e.statusCode.toString())
        }
    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        val myFragment =
            supportFragmentManager.findFragmentByTag("MY_FRAGMENT")
        if (myFragment == null || !myFragment.isVisible) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            var nextFragment: Fragment = HomeFragment()
            if (runningFragment == "loggedin") {
                nextFragment = LoggedInFragment()
                runningFragment = ""
                transaction.replace(R.id.nav_host_fragment, nextFragment, "MY_FRAG")
            }
            else{
                transaction.replace(R.id.nav_host_fragment, nextFragment, "MY_FRAGMENT")
            }
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