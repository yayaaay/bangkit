package amirlabs.sapasemua.ui.menu.module.add_submodule

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Module
import amirlabs.sapasemua.data.model.SubModule
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.logError
import amirlabs.sapasemua.utils.prefs
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.HttpException
import java.io.File

class AddSubmoduleViewModel (private val repo: MainRepository) : DevViewModel(){
    private val disposable = CompositeDisposable()

    private val _addSubmoduleResult = MutableLiveData<DevState<SubModule>>(DevState.Default())
    val addSubmoduleResult get() = _addSubmoduleResult

    fun addSubmodule(moduleId:String, video: File, title: String, duration: String){
        _addSubmoduleResult.value = DevState.Loading()
        val body = mapOf(
            "name" to title,
            "duration" to duration
        )
        repo.createSubmodule(moduleId, body, video)
            .delay(1000L, java.util.concurrent.TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) _addSubmoduleResult.value = DevState.success(it.data)
                else _addSubmoduleResult.value = DevState.fail(null, it.message)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _addSubmoduleResult.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _addSubmoduleResult.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}