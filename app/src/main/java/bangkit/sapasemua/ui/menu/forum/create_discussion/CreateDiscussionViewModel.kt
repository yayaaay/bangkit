package amirlabs.sapasemua.ui.menu.forum.create_discussion

import amirlabs.sapasemua.base.DevViewModel
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

class CreateDiscussionViewModel(private val repo: MainRepository)  : DevViewModel(){
    private val disposable = CompositeDisposable()
    val user = prefs().getObject("user", User::class.java)

    private val _forumResult = MutableLiveData<DevState<Forum>>()
    val forumResult: LiveData<DevState<Forum>> get() = _forumResult

    fun submitForum(title:String, description:String){
        _forumResult.value = DevState.loading()
        val body = mapOf("title" to title, "creator" to (user?.id?:""), "description" to description)
        repo.createForum(body)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) _forumResult.value = DevState.fail(null, it.message)
                else _forumResult.value = DevState.success(it.data)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _forumResult.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _forumResult.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}