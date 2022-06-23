package pk.mosafir.travsol.ui

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.facebook.*
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
import pk.mosafir.travsol.ui.account.UserInfoFragment
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
    lateinit var callbackManager:CallbackManager

    companion object {
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
        lateinit var socialLoginInterface:SocialLoginInterface

        fun socialLogin(which:String, result: SocialLoginInterface ){
            when(which){

                "1"->{
                    login.performClick()
                }
                "2"->{
                    googlelogin.performClick()
                }
            }
            socialLoginInterface = result
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


        //fb signin
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
                    var email = ""
                    var name = ""
                    var imageURL = ""
                    var authID = ""
                    var authTYPE = ""
                    val request = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { `object`, _ ->

                        email = `object`!!.getString("email")

                        name = `object`!!.getString("name")

                        imageURL = "https://graph.facebook.com/${`object`!!
                            ?.getString("id")}/picture?width=500&height=500"

                        authID = result.accessToken.userId

                        authTYPE = "FB"

                        //toast("email: "+email)
                        Log.d("FB_DATA: ","name: "+name+ "email: "+email+ "imageURL: "+imageURL+
                               "authID: "+authID+ "authTYPE: "+authTYPE)

                        //pass model to interface which will be used in account fragment
                        var socialLoginModel = SocialLoginModel(email, name, imageURL, authID, authTYPE)
                        socialLoginInterface.updated(socialLoginModel)

//                        postSocialData(email, name, imageURL, authID, authTYPE)

                        //displaying profile img in custom dialog
                        //showDialog(imageURL)
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                    toast("FB_ERROR: "+error.message)
                }
            })

    //google signin
    val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client))
        .requestEmail()
        .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity ,gso)

        googlelogin = findViewById(R.id.googleLoginBtn)
        googlelogin.setOnClickListener {
            SignInGoogle()

        }

        val bNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bNav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            var selectedFragment: Fragment = HomeFragment()
            when (item.itemId) {

                R.id.navigation_account -> {
                    if (loggedIn) {
                        selectedFragment = LoggedInFragment()
//                        val intent = Intent(this, WebViewActivity::class.java)
//                        intent.putExtra("url", "https://mosafir.pk/mobile/Admin/dashboard")
//                        startActivity(intent)
//                        return@OnItemSelectedListener true
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
            transaction.replace(R.id.nav_host_fragment, selectedFragment).addToBackStack(null)
            transaction.commit()
            return@OnItemSelectedListener true
        })
    }

    //method calling apiii


    private fun SignInGoogle() {
        val signinIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent, RC_SIGN_IN)
    }

    private fun showDialog(imageURL: String) {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_profile_img_dialog)
        val img = dialog.findViewById(R.id.img_profile) as ImageView
        val cancel = dialog.findViewById(R.id.img_cancl) as TextView

        Glide.with(this@MainActivity).load(imageURL).into(img)

        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()

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
        var selectedFragment: Fragment = HomeFragment()
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
                if (loggedIn) {
                    selectedFragment = LoggedInFragment()
                } else {
                    selectedFragment = AccountFragment()
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

    fun openFragment(selectedFragment: Fragment){
        transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, selectedFragment).addToBackStack(null)
        transaction.commit()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            Log.e("google_social_login: ","success")

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else{
            Log.e("gogoel_social_login: ","failed")
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            //signin successfully
            val googleID = account?.id ?:""
            val googleFirstName = account?.givenName ?: ""
            val googleLastName = account?.familyName ?: ""
            val googleEmail = account?.email ?: ""
            val googleProfilePicURL = account?.photoUrl.toString()
            val googleIdToken = account?.idToken ?: ""
            //toast("$googleEmail $googleID")
            var authTYPE = ""

            authTYPE = "GOOGLE"

            //pass model to interface which will be used in account fragment
            var socialLoginModel = SocialLoginModel(googleEmail,
                "$googleFirstName $googleLastName", googleProfilePicURL, googleID, authTYPE)
            socialLoginInterface.updated(socialLoginModel)
        }catch (e:ApiException) {
            Timber.e(e.statusCode.toString())
        }

    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate()
            Log.e("displayscreen: ","1")
        }
        else{
            Log.e("displayscreen: ","2")
            if (doubleBackToExitPressedOnce){
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

        /*val myFragment2: LoggedInFragment? =
            supportFragmentManager.findFragmentByTag("MY_FRAGMENT2") as LoggedInFragment?
        if (myFragment2 == null || !myFragment2.isVisible) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, LoggedInFragment(), "MY_FRAGMENT2")
            transaction.commit()
        }


        val myFragment: HomeFragment? =
            supportFragmentManager.findFragmentByTag("MY_FRAGMENT") as HomeFragment?
        if (myFragment == null || !myFragment.isVisible) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
        else {
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
        }*/
    }
}