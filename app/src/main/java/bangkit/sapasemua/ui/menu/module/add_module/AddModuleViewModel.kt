package amirlabs.sapasemua.ui.menu.module.add_module

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Module
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.logError
import amirlabs.sapasemua.utils.prefs
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File

class AddModuleViewModel(private val repo: MainRepository) : DevViewModel() {
    private val disposable = CompositeDisposable()
    private val _createResult = MutableLiveData<DevState<Module>>(DevState.Default())
    val createResult get() = _createResult
    fun createModule(image: File, video:List<File>, submodule:List<Map<String, Any>>, name: String, description: String, level: Int) {
        _createResult.value = DevState.Loading()
        val body = mapOf(
            "name" to name,
            "description" to description,
            "level" to level
        )
        val user = prefs().getObject("user", User::class.java)
        repo.createModule(user?.id, body, image, submodule, video)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null){
                    _createResult.value = DevState.fail(it.message)
                    logError(it.message)
                }else{
                    _createResult.value = DevState.success(it.data)
                }
            }, {
                _createResult.value = DevState.fail( it.message.toString())
                logError(it.message)
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}