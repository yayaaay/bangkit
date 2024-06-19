package amirlabs.sapasemua.ui.menu.module.lesson

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.model.SubModule
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.DevState
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import java.io.FileOutputStream

class LessonViewModel(private val repo: MainRepository) : DevViewModel(){
    private val disposable = CompositeDisposable()
    private val _lesson = MutableLiveData<DevState<List<SubModule>>>(DevState.default())
    val lesson: MutableLiveData<DevState<List<SubModule>>>
        get() = _lesson

    fun getLessons(moduleId: String){
        repo.getLessons(moduleId)
            .subscribe({
                if (it.data != null) {
                    _lesson.postValue(DevState.success(it.data))
                }
                else _lesson.value = DevState.fail(null, it.message)
            },{
                val errorMessage = it.localizedMessage
                _lesson.value = DevState.fail(null, errorMessage)
            }).let(disposable::add)
    }

    private fun base64ToVideo(base64: String, lessonId: String): File {
        val decodedBytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
//        val dir = File(Environment.getExternalStorageDirectory().toString() + "/video/$lessonId.mp4")
//        dir.createNewFile()
        val dir = File.createTempFile(lessonId,".mp4")
        try {
            val out = FileOutputStream(dir)
            out.write(decodedBytes)
            out.close()
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
        return dir
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}