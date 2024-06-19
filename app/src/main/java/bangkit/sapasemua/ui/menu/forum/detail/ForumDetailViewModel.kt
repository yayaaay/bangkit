package amirlabs.sapasemua.ui.menu.forum.detail

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

class ForumDetailViewModel(private val repo: MainRepository)  : DevViewModel(){
    private val disposable = CompositeDisposable()
    private val _forumDetail = MutableLiveData<DevState<Forum>>()
    val forumDetail: LiveData<DevState<Forum>> get() = _forumDetail

    private val _addComment = MutableLiveData<DevState<Forum>>()
    val addComment: LiveData<DevState<Forum>> get() = _addComment

    private val user = prefs().getObject("user", User::class.java)

    fun getForumDetail(forumId:String){
        _forumDetail.value = DevState.loading()
        repo.getForumDetail(forumId)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) _forumDetail.value = DevState.fail(null, it.message)
                else _forumDetail.value = DevState.success(it.data)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _forumDetail.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _forumDetail.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun addComment(forumId:String, comment:String){
        _addComment.value = DevState.loading()
        val body = mapOf("title" to comment, "creator" to (user?.id?:""))
        repo.addComment(forumId, body)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) _addComment.value = DevState.fail(null, it.message)
                else _addComment.value = DevState.success(it.data)
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _addComment.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _addComment.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}