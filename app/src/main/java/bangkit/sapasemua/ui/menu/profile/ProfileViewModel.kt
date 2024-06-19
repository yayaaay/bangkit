package amirlabs.sapasemua.ui.menu.profile

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.prefs
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File

class ProfileViewModel(private val repo: MainRepository)  : DevViewModel(){
    private val disposable = CompositeDisposable()

    private val _profile = MutableLiveData<DevState<User>>(DevState.default())
    val profile: LiveData<DevState<User>> get() = _profile

    private val _submitProfile = MutableLiveData<DevState<User>>(DevState.default())
    val submitProfile: LiveData<DevState<User>> get() = _submitProfile
    private val user = prefs().getObject("user", User::class.java)

    fun getProfileDetail(userId: String){
        _profile.value = DevState.loading()
        repo.getProfile(userId)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null)
                    _profile.value = DevState.fail(null, it.message ?: "Something went wrong")
                else {
                    prefs().setObject("user", it.data)
                    _profile.value = DevState.success(it.data)
                }
            },{
                _profile.value = DevState.fail(null, it.localizedMessage)
            }).let(disposable::add)
    }

    fun updateProfile(userId:String, updatedField: Map<String?, Any>, avatar: File?){
        _submitProfile.value = DevState.loading()
        val body = updatedField.plus("id" to userId)
        repo.updateProfile(body, avatar)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null)
                    _submitProfile.value = DevState.fail(null, it.message ?: "Something went wrong")
                else{
                    _submitProfile.value = DevState.success(it.data)
//                    prefs().setObject("user", it.data)
                }
            },{
                _submitProfile.value = DevState.fail(null, it.localizedMessage)
            }).let(disposable::add)
    }
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}