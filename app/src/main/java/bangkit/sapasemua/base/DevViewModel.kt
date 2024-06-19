package amirlabs.sapasemua.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Suppress("MemberVisibilityCanBePrivate")
open class DevViewModel() : ViewModel(), LifecycleObserver {

    protected val messageAlert = MutableLiveData("")
    val messageAlertLiveData: LiveData<String>
        get() = messageAlert

    protected val messageError = MutableLiveData("")
    val messageErrorLiveData: LiveData<String>
        get() = messageError

    protected val isSessionEnd = MutableLiveData<Boolean>(false)
    val isSessionEndLiveData: LiveData<Boolean>
        get() = isSessionEnd

    override fun onCleared() {
        super.onCleared()
    }
}