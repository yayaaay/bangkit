package amirlabs.sapasemua.ui.menu.module.submodule

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

class SubModuleViewModel (private val repo: MainRepository) : DevViewModel(){
    private val disposable = CompositeDisposable()

    private val _modules = MutableLiveData<DevState<Module>>(DevState.default())
    val modules: LiveData<DevState<Module>>
        get() = _modules
    private val _editSubmoduleResult = MutableLiveData<DevState<SubModule>>(DevState.Default())
    val editSubmoduleResult get() = _editSubmoduleResult

    private val _deleteSubmoduleResult = MutableLiveData<DevState<SubModule>>(DevState.Default())
    val deleteSubmoduleResult get() = _deleteSubmoduleResult

    private val _lesson = MutableLiveData<DevState<SubModule>>(DevState.default())
    val lesson: MutableLiveData<DevState<SubModule>>
        get() = _lesson

    fun getOneModule(moduleId: String){
        _modules.value = DevState.loading()
        repo.getOneModule(moduleId)
            .delay(1000L, java.util.concurrent.TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) _modules.value = DevState.success(it.data)
                else _modules.value = DevState.fail(null, it.message)
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

    fun getLessonById(lessonId: String){
        repo.getOneSubModule(lessonId)
            .subscribe({
                if (it.data != null) {
//                    _video.postValue(base64ToVideo(it.data.video?:"", lessonId))
//                    it.data.video = null
                    _lesson.postValue(DevState.success(it.data))
                }
                else _lesson.value = DevState.fail(null, it.message)
            },{
                val errorMessage = it.localizedMessage
                _lesson.value = DevState.fail(null, errorMessage)
            }).let(disposable::add)
    }

    fun editSubmodule(submoduleId:String, video: File?, title: String, duration: String){
        _editSubmoduleResult.value = DevState.Loading()
        val body = mapOf(
            "title" to title,
            "duration" to duration
        )
        repo.editSubmodule(submoduleId, body, video)
            .delay(1000L, java.util.concurrent.TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) _editSubmoduleResult.value = DevState.success(it.data)
                else _editSubmoduleResult.value = DevState.fail(null, it.message)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _editSubmoduleResult.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _editSubmoduleResult.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun deleteSubmodule(submoduleId: String){
        _deleteSubmoduleResult.value = DevState.Loading()
        repo.deleteSubmodule(submoduleId)
            .delay(1000L, java.util.concurrent.TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) _deleteSubmoduleResult.value = DevState.success(it.data)
                else _deleteSubmoduleResult.value = DevState.fail(null, it.message)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _deleteSubmoduleResult.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _deleteSubmoduleResult.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }
//    fun editModule(image: File?, video:List<File>?, submodule:List<Map<String, Any>>?, name: String, description: String?, level: Int?) {
//        _editResult.value = DevState.Loading()
//        val body = mapOf(
//            "name" to name,
//            "description" to description,
//            "level" to level
//        )
//        val user = prefs().getObject("user", User::class.java)
//        repo.createModule(user?.id, body, image, submodule, video)
//            .compose(singleScheduler())
//            .subscribe({
//                if (it.data == null){
//                    _editResult.value = DevState.fail(it.message)
//                    logError(it.message)
//                }else{
//                    _editResult.value = DevState.success(it.data)
//                }
//            }, {
//                _editResult.value = DevState.fail( it.message.toString())
//                logError(it.message)
//            }).let(disposable::add)
//    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}