package pk.mosafir.travsol.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pk.mosafir.travsol.dao.UserDetailDao
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.model.UpdateProfileModel
import pk.mosafir.travsol.response.Response
import pk.mosafir.travsol.response.UserDetails

class UserInfoViewModel(
    private val userDetailDao: UserDetailDao,
    private val repository: Repository
) : BaseViewModel() {

    private val _userInfoData = MutableLiveData<UserDetails>()
    val userInfoData: MutableLiveData<UserDetails>
                 get() = _userInfoData


    private val _updateProfile = MutableLiveData<String?>()
    val updateProfile: MutableLiveData<String?>
        get() = _updateProfile

    fun getUserInfoData(){
        viewModelScope.launch {
            userInfoData.value = userDetailDao.getUserDetail()
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun updateProfileData(updateProfileModel: UpdateProfileModel){
        viewModelScope.launch {
            when(val response = repository.profileUpdate(updateProfileModel)) {
                is Response.Success -> _updateProfile.value = response.data
                is Response.Error -> error.value = response.message
            }
        }
    }
}