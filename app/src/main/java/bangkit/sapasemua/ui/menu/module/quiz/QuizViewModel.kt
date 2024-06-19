package amirlabs.sapasemua.ui.menu.module.quiz

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Quiz
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

class QuizViewModel(private val repo: MainRepository) : DevViewModel(){
    private val disposable = CompositeDisposable()
    private val _quiz = MutableLiveData<DevState<List<Quiz>>>()
    val quiz: LiveData<DevState<List<Quiz>>> get() = _quiz

    private val _submitResult: MutableLiveData<DevState<QuizResult>> = MutableLiveData(DevState.default())
    val submitResult: LiveData<DevState<QuizResult>> get() = _submitResult

    private val _quizResult: MutableLiveData<DevState<QuizResult>> = MutableLiveData(DevState.default())
    val quizResult: LiveData<DevState<QuizResult>> get() = _quizResult

    val user = prefs().getObject("user", User::class.java)

    fun getQuizByModule(moduleId:String){
        _quiz.value = DevState.loading()
        repo.getQuizByModule(moduleId)
            .delay(1000L, TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _quiz.value = DevState.fail(null, "Data Kosong")
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

    fun submitQuiz(moduleId: String, answers:List<Quiz>){
        _submitResult.value = DevState.loading()
        val body = mapOf(
            "module_id" to moduleId,
            "creator" to (user?.id ?:""),
            "answer" to answers
        )
        repo.submitQuiz(body).compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _submitResult.value = DevState.fail(null, "Data Kosong")
                }else {
                    _submitResult.value = DevState.success(it.data)
                }
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _submitResult.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _submitResult.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }

    fun getQuizResultById(resultId:String){
        _quizResult.value = DevState.loading()
        repo.getQuizResult(resultId).compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _quizResult.value = DevState.fail(null, "Data Kosong")
                }else {
                    _quizResult.value = DevState.success(it.data)
                }
            },{
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        _quizResult.value = DevState.Failure(null, errorBody)
                    }
                } else {
                    val errorMessage = it.localizedMessage
                    _quizResult.value = DevState.fail(null, errorMessage)
                }
            }).let(disposable::add)
    }
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}