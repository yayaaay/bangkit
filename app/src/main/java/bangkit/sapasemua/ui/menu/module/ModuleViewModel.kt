package amirlabs.sapasemua.ui.menu.module

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Module
import amirlabs.sapasemua.data.model.QuizResult
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.prefs
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class ModuleViewModel(private val repo: MainRepository)  : DevViewModel(){
    private val disposable = CompositeDisposable()
    private val _modules = MutableLiveData<DevState<List<Module>>>()
    val modules: LiveData<DevState<List<Module>>> get() = _modules

    private val _quizResults = MutableLiveData<DevState<List<QuizResult>>>()
    val quizResults: LiveData<DevState<List<QuizResult>>> get() = _quizResults

    private val _deleteModule = MutableLiveData<DevState<Module>>()
    val deleteModule: LiveData<DevState<Module>> get() = _deleteModule

    fun getAllModule(){
        _modules.value = DevState.loading()
        repo.getAllModule()
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                _modules.value = DevState.success(it.data ?: emptyList())
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _modules.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _modules.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun getAllQuizResult(){
        _quizResults.value = DevState.loading()
        val user = prefs().getObject("user", User::class.java)
        repo.getAllQuizResult(user?.id?:"")
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                _quizResults.value = DevState.success(it.data ?: emptyList())
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _quizResults.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _quizResults.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun deleteModule(moduleId:String){
        _deleteModule.value = DevState.loading()
        repo.deleteModule(moduleId)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) {
                    _deleteModule.postValue(DevState.success(it.data))
                }
                else _deleteModule.value = DevState.fail(null, it.message)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _modules.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _modules.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}