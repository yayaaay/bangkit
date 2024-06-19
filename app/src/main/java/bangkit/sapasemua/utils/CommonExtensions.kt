package amirlabs.sapasemua.utils

import amirlabs.sapasemua.base.ViewModelFactory
import amirlabs.sapasemua.data.local.DevPreferenceManager
import amirlabs.sapasemua.data.model.User
import android.content.Context
import android.text.SpannableString
import android.text.style.BulletSpan
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.gson.Gson

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(): Lazy<T> {
    return lazy { ViewModelProvider(this, ViewModelFactory.viewModelFactory)[T::class.java] }
}

fun prefs(context:Context?=null): DevPreferenceManager {
    return DevPreferenceManager(context?:ApplicationContext.get(), "amirlabs.sapasemua.prefs", Gson())
}

fun isAdmin(): Boolean {
    return prefs().getObject("user", User::class.java)?.role == "PENGAJAR"
}

fun logDebug(vararg message: String?) {
    Log.d("TAG_DEBUG", message.toList().toString())
}

fun logError(vararg message: String?) {
    Log.e("TAG_ERROR", message.toList().toString())
}

fun List<String>.toBulletedList(): CharSequence {
    return SpannableString(this.joinToString("\n")).apply {
        this@toBulletedList.foldIndexed(0) { index, acc, span ->
            val end = acc + span.length + if (index != this@toBulletedList.size - 1) 1 else 0
            this.setSpan(BulletSpan(16), acc, end, 0)
            end
        }
    }
}