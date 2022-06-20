package pk.mosafir.travsol.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.model.SocialLoginModel
import pk.mosafir.travsol.response.Offer
import pk.mosafir.travsol.response.Response

class AccountViewModel(private val repository: Repository) : BaseViewModel() {
    private val _checkUser = MutableLiveData<String>()
    val checkUser: MutableLiveData<String>
        get() = _checkUser

    private val _checkSocialLogin = MutableLiveData<String>()
    val checkSocialLogin: MutableLiveData<String>
        get() = _checkSocialLogin

    private val _validateOTP = MutableLiveData<String>()
    val validateOTP: MutableLiveData<String>
        get() = _validateOTP

    private val _checkUserRegister = MutableLiveData<String>()
    val checkUserRegister: MutableLiveData<String>
        get() = _checkUserRegister

    private val _validateOTPRegister = MutableLiveData<String>()
    val validateOTPRegister: MutableLiveData<String>
        get() = _validateOTPRegister

    private val _offers = MutableLiveData<List<Offer>?>()
    val offers: MutableLiveData<List<Offer>?>
        get() = _offers

    init {
        isLoading.value = true
    }
    ///login
    @SuppressLint("NullSafeMutableLiveData")
    fun checkUser(mobile: String, country_code: String) {
        viewModelScope.launch {
            when (val response = repository.checkUser(mobile, country_code)) {
                is Response.Success -> _checkUser.value = response.data
                is Response.Error -> error.value = response.message
            }
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun validateOTP(mobile: String, temp_key: String) {
        viewModelScope.launch {
            when (val response = repository.validateOTP(mobile, temp_key)) {
                is Response.Success -> _validateOTP.value = response.data
                is Response.Error -> error.value = response.message
            }
        }
    }
    //Register
    @SuppressLint("NullSafeMutableLiveData")
    fun checkUserRegister(name: String, mobile: String, country_code: String) {
        viewModelScope.launch {
            when (val response = repository.checkUserRegister(name, mobile, country_code)) {
                is Response.Success -> _checkUserRegister.value = response.data
                is Response.Error -> error.value = response.message
            }
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun validateOTPRegister(mobile: String, temp_key: String) {
        viewModelScope.launch {
            when (val response = repository.validateOTPRegister(mobile, temp_key)) {
                is Response.Success -> _validateOTPRegister.value = response.data
                is Response.Error -> error.value = response.message
            }
        }
    }

    //social login
    ///login
    @SuppressLint("NullSafeMutableLiveData")
    fun checkSocialLogin(socialLoginModel: SocialLoginModel) {
        viewModelScope.launch {
            when (val response = repository.checkSocialUser(socialLoginModel)) {
                is Response.Success -> _checkSocialLogin.value = response.data
                is Response.Error -> error.value = response.message
            }
        }
    }
}