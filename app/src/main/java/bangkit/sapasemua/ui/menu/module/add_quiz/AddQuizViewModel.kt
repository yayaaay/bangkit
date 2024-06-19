package amirlabs.sapasemua.ui.menu.module.add_quiz

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.logError
import amirlabs.sapasemua.utils.singleScheduler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File

class AddQuizViewModel(private val repo: MainRepository) : DevViewModel() {
    private val disposable = CompositeDisposable()
    private val _createResult = MutableLiveData<DevState<Quiz>>(DevState.Default())
    val createResult: LiveData<DevState<Quiz>>
        get() = _createResult

    fun createQuiz(
        module: String,
        question: String,
        answer: String,
        option1: String,
        option2: String,
        option3: String,
        option4: String,
        attachment: File,
    ) {
        _createResult.value = DevState.Loading()
        val body = mapOf(
            "module" to module,
            "question" to question,
            "answer" to answer,
            "option1" to option1,
            "option2" to option2,
            "option3" to option3,
            "option4" to option4
        )
        repo.createQuiz(body, attachment)
            .compose(singleScheduler())
            .subscribe({
                if (it.data == null) {
                    _createResult.value = DevState.fail(it.message)
                    logError(it.message)
                } else {
                    _createResult.value = DevState.success(it.data)
                }
            }, {
                _createResult.value = DevState.fail(it.message.toString())
                logError(it.message)
            }).let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}