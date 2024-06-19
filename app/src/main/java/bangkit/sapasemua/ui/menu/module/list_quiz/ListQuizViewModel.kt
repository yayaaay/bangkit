package amirlabs.sapasemua.ui.menu.module.list_quiz

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class ListQuizViewModel(private val repo: MainRepository)  : DevViewModel(){
    private val disposable = CompositeDisposable()
    private val _quiz = MutableLiveData<DevState<List<Quiz>>>()
    val quiz: LiveData<DevState<List<Quiz>>> get() = _quiz

    private val _deleteQuiz = MutableLiveData<DevState<Quiz>>()
    val deleteQuiz: LiveData<DevState<Quiz>> get() = _deleteQuiz

    fun getQuizByModule(moduleId:String){
        _quiz.value = DevState.loading()
        repo.getQuizQuestion(moduleId)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _quiz.value = DevState.fail(null, it.message)
                }else if (it.data.isEmpty()) {
                    _quiz.value = DevState.empty()
                }
                _quiz.value = DevState.success(it.data ?: emptyList())
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _quiz.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _quiz.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun deleteQuiz(quizId: String){
        _deleteQuiz.value = DevState.loading()
        repo.deleteQuiz(quizId)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _deleteQuiz.value = DevState.fail(null, it.message)
                }else {
                    _deleteQuiz.value = DevState.success(it.data)
                }
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _deleteQuiz.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _deleteQuiz.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}