package pk.mosafir.travsol.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pk.mosafir.travsol.dao.UserDetailDao
import pk.mosafir.travsol.response.UserDetails

class UserInfoViewModel(
    private val userDetailDao: UserDetailDao
) : BaseViewModel() {

    private val _userInfoData = MutableLiveData<UserDetails>()
    val userInfoData: MutableLiveData<UserDetails>
                 get() = _userInfoData

    fun getUserInfoData(){
        viewModelScope.launch {
            userInfoData.value = userDetailDao.getUserDetail()
        }
    }
}