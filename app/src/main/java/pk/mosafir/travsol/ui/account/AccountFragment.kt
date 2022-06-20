package pk.mosafir.travsol.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentAccountBinding
import pk.mosafir.travsol.ui.MainActivity
import pk.mosafir.travsol.ui.base.BaseFragment
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.viewmodel.AccountViewModel

class AccountFragment : BaseFragment(), View.OnClickListener {
    private var mAuth: FirebaseAuth? = null
    private lateinit var _binding: FragmentAccountBinding
    private val binding get() = _binding
    var m = 59
    val stringFirst = "If you didn't receive OTP "
    val stringSecond = "CLICK HERE"
    var stringExtra = " for Voice OTP"
    val stringThird = " for voice OTP, or "
    val stringFourth = "CLICK HERE"
    val stringFive = " for SMS OTP"
    var tryier = 0
    private val viewModel: AccountViewModel by viewModel()

    //]private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.register.setOnClickListener(this)
        binding.login.setOnClickListener(this)
        binding.registerNow.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()

        /*val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client))
            .requestEmail()
            .build()*/
        //val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        //mGoogleSignInClient = GoogleSignIn.getClient(requireContext(),gso)

        binding.loginSpinnerCountry.apply {
            ccpDialogShowTitle = false
            setDefaultCountryUsingNameCode("PK")
        }
        binding.spinnerCountry.apply {
            ccpDialogShowTitle = false
            setDefaultCountryUsingNameCode("PK")
        }

        //click fb button and call fb login method which is written in MainActivity class
        binding.facebook.setOnClickListener {
            MainActivity.login.performClick()
        }
        //google login button
        binding.google.setOnClickListener {
            /*val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1)*/
            MainActivity.googlelogin.performClick()
        }

        return binding.root
    }

    /*private fun SignInGoogle() {
        val signinIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent,1)
    }*/

    @SuppressLint("SetTextI18n")
    override fun setTitle(title: String) {
        tvTitle?.text = "Account Fragment"
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.register -> {
                //binding.title.text = "Register"
                when {
                    binding.register.text.toString().lowercase() == "validate" -> {
                        viewModel.validateOTPRegister(binding.otp.text.toString(), temp_key)
                        viewModel.validateOTPRegister.observe(viewLifecycleOwner, {
                            when {
                                it.equals("1") -> {
                                    //viewModel.validateOTPRegister.value = "-1"
                                    requireContext().toast("Login success")
                                    val fragmentManager = requireActivity().supportFragmentManager
                                    val transaction = fragmentManager.beginTransaction()
                                    transaction.replace(
                                        R.id.nav_host_fragment,
                                        HomeFragment(),
                                        "MY_FRAGMENT"
                                    )
                                    transaction.commit()
                                }
                                it.equals("0") -> {
                                    //viewModel.validateOTPRegister.value = "-1"
                                    binding.register.text = "Register"
                                    binding.phoneNumber.isEnabled = false
                                    binding.register.setTextColor(resources.getColor(R.color.colorWhite))
                                    binding.register.background =
                                        resources.getDrawable(R.drawable.background_login, null)
                                    binding.register.isEnabled = true
                                    binding.otpLayoutRegister.gone()
                                    binding.timerRegister.gone()
                                    binding.timerTextRegister.gone()
                                    requireActivity().toast("incorrect OTP")
                                }
                                else -> {
                                    //viewModel.validateOTPRegister.value = "-1"
//                                    requireContext().toast("No")
                                }
                            }
                        })
                        return
                    }
                    else -> {
                        val mobile = binding.phoneNumber.text!!.trim().toString()
                        val code = binding.spinnerCountry.selectedCountryCode.toString()
                        val name = binding.name.text.toString()
                        when {
                            name.isEmpty() -> {
                                binding.name.error = "Please fill in this field"
//                                requireContext().toast("Name is required")
                                return
                            }
                            mobile.length < 10 -> {
                                binding.phoneNumber.error =
                                    "Please use correct format e.g 3xxxxxxxxxx"
//                                requireContext().toast("Enter valid phone number")
                                return
                            }

                            else -> {
                                binding.register.text = "Wait..."
                                binding.register.isEnabled = false
                                binding.register.setTextColor(resources.getColor(R.color.colorGrayDark))
                                binding.register.background =
                                    resources.getDrawable(R.drawable.background_login_disable, null)
                                viewModel.checkUserRegister(name, mobile, code)
                                viewModel.checkUserRegister.observe(viewLifecycleOwner, { s ->
                                    when {
                                        s.equals("1") -> {
                                            //viewModel.checkUserRegister.value = "-1"
                                            bindRegister()
                                        }
                                        s.equals("0") -> {
                                            //viewModel.checkUserRegister.value = "-1"
                                            binding.register.text = "Register"
                                            binding.register.isEnabled = true
                                            binding.register.background =
                                                resources.getDrawable(
                                                    R.drawable.background_login,
                                                    null
                                                )
                                            binding.register.setTextColor(resources.getColor(R.color.colorWhite))
                                            binding.loginCard.visible()
                                            binding.registerCard.gone()
                                            requireContext().toast("Already Registered")
                                        }
                                        else -> {
                                            //viewModel.checkUserRegister.value = "-1"
                                            binding.loginCard.visible()
                                            binding.registerCard.gone()
                                            requireContext().toast("An error about registering")
                                        }
                                    }
                                })
                                viewModel.error.observe(viewLifecycleOwner, {
                                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                                })
                            }
                        }
                    }
                }
            }
            R.id.login -> {
                binding.title.text = "User Login"
                when {
                    binding.login.text.toString().lowercase() == "validate" -> {
                        viewModel.validateOTP(binding.otp.text.toString(), temp_key)
                        viewModel.validateOTP.observe(viewLifecycleOwner, {
                            when {
                                it.equals("1") -> {
                                    //viewModel.validateOTP.value = "-1"
                                    requireContext().toast("Login success")
                                    val fragmentManager = requireActivity().supportFragmentManager
                                    val transaction = fragmentManager.beginTransaction()
                                    transaction.replace(
                                        R.id.nav_host_fragment,
                                        HomeFragment(),
                                        "MY_FRAGMENT"
                                    )
                                    transaction.commit()
                                }
                                it.equals("0") -> {
                                    // viewModel.validateOTP.value = "-1"
                                    binding.login.text = "LOGIN"
                                    binding.phoneNumberLogin.isEnabled = false
                                    binding.login.setTextColor(resources.getColor(R.color.colorWhite))
                                    binding.login.background =
                                        resources.getDrawable(R.drawable.background_login, null)
                                    binding.login.isEnabled = true
                                    binding.otpLayout.gone()
                                    binding.timer.gone()
                                    binding.timerText.gone()
                                    requireActivity().toast("incorrect OTP")
                                }
                                else -> {
                                    //viewModel.validateOTP.value = "-1"
//                                    requireContext().toast("No")
                                }
                            }
                            viewModel.validateOTP.removeObservers(viewLifecycleOwner)
                        })
                        return
                    }
                    else -> {
                        val mobile = binding.phoneNumberLogin.text!!.trim().toString()
                        val code = binding.loginSpinnerCountry.selectedCountryCode.toString()
                        when {
                            mobile.length < 10 -> {
                                binding.phoneNumberLogin.error =
                                    "Please use correct format e.g 3xxxxxxxxxx"
                                return
                            }
                            else -> {
                                binding.login.text = "Wait..."
                                binding.login.isEnabled = false
                                binding.login.setTextColor(resources.getColor(R.color.colorGrayDark))
                                binding.login.background =
                                    resources.getDrawable(R.drawable.background_login_disable, null)
                                viewModel.checkUser(mobile, code)
                                viewModel.checkUser.observe(viewLifecycleOwner, { s ->
                                    if (s.equals("1")) {
                                        //viewModel.checkUser.value = "-1"
                                        bindLogin()
                                    } else {
                                        //viewModel.checkUser.value = "-1"
                                        binding.login.text = "Login"
                                        binding.login.isEnabled = true
                                        binding.login.background =
                                            resources.getDrawable(R.drawable.background_login, null)
                                        binding.login.setTextColor(resources.getColor(R.color.colorWhite))
                                        binding.loginCard.gone()
                                        binding.registerCard.visible()
                                        tryier = 0
                                    }
                                })
                                viewModel.error.observe(viewLifecycleOwner, {
                                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                                })
                            }
                        }
                    }
                }
            }
            R.id.registerNow -> {
                binding.title.text = "Register"
                binding.loginCard.invisible()
                binding.registerCard.visible()
                tryier = 0
            }
            R.id.back -> {
                if (binding.registerCard.isVisible) {
                    binding.registerCard.invisible()
                    binding.title.text = "User Login"
                    binding.loginCard.visible()
                } else {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
                    transaction.commit()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken.idToken, null)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                idToken.id?.let { it1 -> loggedInUser(it1) }
                Toast.makeText(requireContext(), "logged in", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "${it.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode == 1) {
            requireActivity().toast("login successful")

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            
            *//*val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                requireActivity().toast(account.email.toString())
                account.displayName
                account.photoUrl
                Log.e("Google: ","name: "+account.displayName.toString())
                Log.e("img: ",account.photoUrl.toString())
                Log.e("email: ",account.email.toString())
            } catch (e: ApiException) {
                requireActivity().toast(e.toString())
            }*//*
        }
        else{
            requireActivity().toast("login failed")
        }*/
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {

            val account = task.getResult(ApiException::class.java)
            //signin successfully
            val googleID = account?.id ?:""
            Log.i("Google ID", googleID.toString())
            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)
            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)
            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)
            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)
            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

        }catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credentials = FacebookAuthProvider.getCredential(token.token)
//        mAuth!!.signInWithCredential(credentials).addOnCompleteListener {
//            if (it.isSuccessful) {
//                loggedInUser(token.userId)
//                requireContext().toast("logged success")
//            } else {
//                requireContext().toast("failed")
//            }
//        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun bindRegister() {
        binding.otpLayoutRegister.visible()
        binding.register.text = "VALIDATE"
        binding.name.isEnabled = false
        binding.phoneNumber.isEnabled = false
        binding.timerRegister.visible()
        binding.register.background =
            resources.getDrawable(
                R.drawable.background_login,
                null
            )
        binding.register.isEnabled = true
        binding.timerTextRegister.visible()
        object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerRegister.text =
                    (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.softWarningRegister.visible()
                var spannable =
                    SpannableString(stringFirst + stringSecond + stringExtra)
                val voiceClickableSpan: ClickableSpan =
                    object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            requireContext().toast("voice")
                            tryier++
                            binding.softWarning.invisible()
                            bindRegister()
                        }
                    }
                spannable.setSpan(
                    voiceClickableSpan,
                    stringFirst.length - 1,
                    stringFirst.length + stringSecond.length,
                    1
                )
                if (tryier != 0) {
                    spannable =
                        SpannableString(stringFirst + stringSecond + stringThird + stringFourth + stringFive)
                    val smsClickableSpan: ClickableSpan =
                        object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                requireContext().toast("sms")
                                binding.softWarning.invisible()
                                bindRegister()
                            }
                        }
                    spannable.setSpan(
                        voiceClickableSpan,
                        stringFirst.length - 1,
                        stringFirst.length + stringSecond.length,
                        1
                    )
                    spannable.setSpan(
                        smsClickableSpan,
                        stringFirst.length+stringSecond.length+stringThird.length - 1,
                        stringFirst.length+stringSecond.length+stringThird.length + stringFourth.length,
                        2
                    )
                }
                binding.errorRegister.text = spannable
                binding.errorRegister.movementMethod =
                    LinkMovementMethod.getInstance()
                binding.errorRegister.highlightColor =
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
            }
        }.start()
        binding.otpRegister.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                binding.register.background =
                    resources.getDrawable(
                        R.drawable.background_login,
                        null
                    )
                binding.register.isEnabled = true
            } else {
                binding.register.isEnabled = false
                binding.register.background =
                    resources.getDrawable(
                        R.drawable.background_login_disable,
                        null
                    )
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun bindLogin() {
        binding.otpLayout.visible()
        binding.login.text = "VALIDATE"
        binding.login.isEnabled = false
        binding.phoneNumberLogin.isEnabled = false
        binding.timer.visible()
        binding.timerText.visible()
        object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text =
                    (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.softWarning.visible()
                var spannable =
                    SpannableString(stringFirst + stringSecond + stringExtra)
                val voiceClickableSpan: ClickableSpan =
                    object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            requireContext().toast("voice")
                            tryier++
                            binding.softWarning.invisible()
                            bindLogin()
                        }
                    }
                spannable.setSpan(
                    voiceClickableSpan,
                    stringFirst.length - 1,
                    stringFirst.length + stringSecond.length,
                    1
                )
                if (tryier != 0) {
                    spannable =
                        SpannableString(stringFirst + stringSecond + stringThird + stringFourth + stringFive)
                    val smsClickableSpan: ClickableSpan =
                        object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                binding.softWarning.invisible()
                                requireContext().toast("sms")
                                bindLogin()
                            }
                        }

                    spannable.setSpan(
                        voiceClickableSpan,
                        stringFirst.length - 1,
                        stringFirst.length + stringSecond.length,
                        1
                    )
                    spannable.setSpan(
                        smsClickableSpan,
                        stringFirst.length+stringSecond.length+stringThird.length - 1,
                        stringFirst.length+stringSecond.length+stringThird.length + stringFourth.length,
                        2
                    )
                }
                binding.error.text = spannable
                binding.error.movementMethod =
                    LinkMovementMethod.getInstance()
                binding.error.highlightColor =
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
            }
        }.start()
        binding.otp.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                binding.login.background =
                    resources.getDrawable(
                        R.drawable.background_login,
                        null
                    )
                binding.login.setTextColor(resources.getColor(R.color.colorWhite))
                binding.login.isEnabled = true
            } else {
                binding.login.isEnabled = false
                binding.login.setTextColor(resources.getColor(R.color.colorGrayDark))
                binding.login.background = resources.getDrawable(
                    R.drawable.background_login_disable,
                    null
                )

            }
        }
    }
}