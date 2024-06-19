package amirlabs.sapasemua.ui.menu.home

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.utils.prefs
import io.reactivex.rxjava3.disposables.CompositeDisposable

class HomeViewModel : DevViewModel(){
    private val disposable = CompositeDisposable()
    val user = prefs().getObject("user", User::class.java)

    fun getModuleProgress(){

    }

    fun getUserProfile(){
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}