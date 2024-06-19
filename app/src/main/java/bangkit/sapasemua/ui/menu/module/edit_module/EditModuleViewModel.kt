package amirlabs.sapasemua.ui.menu.module.edit_module

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Module
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

class EditModuleViewModel(private val repo: MainRepository) : DevViewModel() {
    private val disposable = CompositeDisposable()

    private val _module = MutableLiveData<DevState<Module>>(DevState.default())
    val module: LiveData<DevState<Module>> get() = _module

    private val _editResult = MutableLiveData<DevState<Module>>(DevState.Default())
    val editResult get() = _editResult

    fun getOneModule(moduleId: String){
        _module.value = DevState.loading()
        repo.getOneModule(moduleId)
            .delay(1000L, java.util.concurrent.TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) _module.value = DevState.success(it.data)
                else _module.value = DevState.fail(null, it.message)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _module.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _module.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun editModule(moduleId:String, name: String?, description: String?, level: Int?, image: File?) {
        _editResult.value = DevState.Loading()
        var body:Map<String, Any> = emptyMap()
        if (name != null) body = body.plus(Pair("name", name))
        if (description != null) body = body.plus(Pair("description", description))
        if (level != null) body = body.plus(Pair("level", level))

        repo.editModule(moduleId, body, image)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null){
                    _editResult.value = DevState.fail(it.message)
                    logError(it.message)
                }else{
                    _editResult.value = DevState.success(it.data)
                }
            }, {
                _editResult.value = DevState.fail( it.message.toString())
                logError(it.message)
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}