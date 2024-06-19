package amirlabs.sapasemua.ui.menu.module.edit_quiz

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Module
import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.logError
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File

class EditQuizViewModel(private val repo: MainRepository) : DevViewModel() {
    private val disposable = CompositeDisposable()

    private val _quiz = MutableLiveData<DevState<Quiz>>(DevState.default())
    val quiz: LiveData<DevState<Quiz>> get() = _quiz

    private val _editResult = MutableLiveData<DevState<Quiz>>(DevState.Default())
    val editResult: LiveData<DevState<Quiz>>
        get() = _editResult

    fun getOneQuiz(quizId: String) {
        _quiz.value = DevState.loading()
        repo.getOneQuiz(quizId)
            .delay(1000L, java.util.concurrent.TimeUnit.MILLISECONDS)
            .compose(singleScheduler())
            .subscribe({
                if (it.data != null) _quiz.value = DevState.success(it.data)
                else _quiz.value = DevState.fail(null, it.message)
            }, {
                _quiz.value = DevState.fail(null, it.message.toString())
                logError(it.message)
            }).let(disposable::add)
    }

    fun editQuiz(
        quizId: String,
        question: String?,
        answer: String?,
        option1: String?,
        option2: String?,
        option3: String?,
        option4: String?,
        attachment: File?
    ) {
        _editResult.value = DevState.Loading()
        var body:Map<String, Any> = emptyMap()
        if (question != null) body = body.plus(Pair("question", question))
        if (answer != null) body = body.plus(Pair("answer", answer))
        if (option1 != null) body = body.plus(Pair("option1", option1))
        if (option2 != null) body = body.plus(Pair("option2", option2))
        if (option3 != null) body = body.plus(Pair("option3", option3))
        if (option4 != null) body = body.plus(Pair("option4", option4))
        repo.editQuiz(quizId, body, attachment)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _editResult.value = DevState.fail(it.message)
                    logError(it.message)
                } else {
                    _editResult.value = DevState.success(it.data)
                }
            }, {
                _editResult.value = DevState.fail(it.message.toString())
                logError(it.message)
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}