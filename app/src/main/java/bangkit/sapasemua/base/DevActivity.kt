package bangkit.sapasemua.base

import bangkit.sapasemua.R
import bangkit.sapasemua.utils.logDebug
import android.app.Activity
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

abstract class DevActivity<T : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity(), DevView, DevFragment.AttachListener {

    protected lateinit var binding: T
    open val vm: DevViewModel = DevViewModel()
    open val devView: DevView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: intent?.getBundleExtra("bundle"))
        binding = DataBindingUtil.setContentView(this, layoutResId)
    }

    override fun onStart() {
        super.onStart()
        onViewReady()
    }

    /**
     * Series of actions when the fragment is ready
     */
    private fun onViewReady() {
        initData()
        initUI()
        initAction()
        initObserver()
        if (devView != null && vm::class.simpleName != DevViewModel::class.simpleName) observeDevView()
    }

    private fun observeDevView() {
        vm.isSessionEndLiveData.observe(this) { isEnd ->
            if (isEnd) {
                devView?.onSessionEnd()
            }
        }
        vm.messageAlertLiveData.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) devView?.onMessageAlert(msg)
        }
        vm.messageErrorLiveData.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) devView?.onMessageError(msg)
        }
    }

    /**
     * Method to init global variable or data from intent
     */
    abstract fun initData()

    /**
     * Method for UI configuration and initialization
     */
    abstract fun initUI()

    /**
     * Method to action to do in activity
     */
    abstract fun initAction()

    /**
     * Method to initialize observer
     */
    abstract fun initObserver()

    override fun onFragmentAttached(tag: String) {
        logDebug("$tag attached")
    }

    override fun onFragmentDetached(tag: String) {
        logDebug("$tag detached")
    }

    override fun onMessageAlert(message: String) {}

    override fun onMessageError(message: String) {}

    override fun onSessionEnd() {}

    protected fun refreshActivity(){
        val mCurrentActivity: Activity = this
        val intent = intent
        val tempBundle = Bundle()
        intent?.putExtra("bundle", tempBundle)

        mCurrentActivity.finish()
        mCurrentActivity.overridePendingTransition(R.anim.anim_flip_in, R.anim.anim_flip_out)
        mCurrentActivity.startActivity(intent)
    }
}