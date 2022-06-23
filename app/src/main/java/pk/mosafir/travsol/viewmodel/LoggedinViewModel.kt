package pk.mosafir.travsol.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pk.mosafir.travsol.dao.UserDetailDao
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.response.UserDetails

class LoggedInViewModel(
    private val userDetailDao: UserDetailDao
) : BaseViewModel() {

    private val _userData = MutableLiveData<UserDetails>()
    val userData: MutableLiveData<UserDetails>
        get() = _userData

  /*  private val _deleteData = MutableLiveData<Boolean>()
    val deleteData: MutableLiveData<Boolean>
        get() = _deleteData*/

    fun getUserData() {
        viewModelScope.launch {
            userData.value = userDetailDao.getUserDetail()
        }
    }

    fun deleteUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            userDetailDao.clear()
        }
    }
}