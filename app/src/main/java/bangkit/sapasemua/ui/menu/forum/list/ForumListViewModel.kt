package amirlabs.sapasemua.ui.menu.forum.list

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class ForumListViewModel(private val repo: MainRepository)  : DevViewModel(){
    private val disposable = CompositeDisposable()

    private val _pagedForum = MutableLiveData<DevState<List<Forum>>>()
    val pagedForum: LiveData<DevState<List<Forum>>> get() = _pagedForum

    private val _forum = MutableLiveData<DevState<List<Forum>>>()
    val forum: LiveData<DevState<List<Forum>>> get() = _forum


    fun getPagedForum(page:Int = 1, pageSize:Int = 5){
        _pagedForum.value = DevState.loading()
        repo.getForum(page, pageSize)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                _pagedForum.value = DevState.success(it.data ?: emptyList())
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _pagedForum.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _pagedForum.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun getAllForum(page:Int = 1, pageSize:Int = 500){
        _forum.value = DevState.loading()
        repo.getForum(page, pageSize)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                _forum.value = DevState.success(it.data ?: emptyList())
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _forum.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _forum.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}