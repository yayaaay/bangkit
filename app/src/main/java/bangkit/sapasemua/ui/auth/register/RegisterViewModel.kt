package amirlabs.sapasemua.ui.auth.register

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RegisterViewModel(private val repo: MainRepository) : DevViewModel(){
    private val disposable = CompositeDisposable()
    private val _registerStatus = MutableLiveData<DevState<User>>(DevState.default())
    val registerStatus: MutableLiveData<DevState<User>>
        get() = _registerStatus

    fun performRegister(name: String, email: String, password: String) {
        _registerStatus.value = DevState.loading()
        val body = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )
        repo.register(body)
            .compose(singleScheduler())
            .subscribe({ user ->
                if (user.data == null) {
                    _registerStatus.value = DevState.fail(user.message?:"Error")
                } else {
                    _registerStatus.value = DevState.success(user.data)
                }
            }, {
                val errorMessage = it.localizedMessage
                _registerStatus.value = DevState.fail(null, errorMessage)
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}