package amirlabs.sapasemua.base

import amirlabs.sapasemua.R
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable


abstract class DevFragment<T : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    Fragment(), DevView {

    open val vm: DevViewModel = DevViewModel()
    open val devView: DevView? = null
    protected var currentActivity: DevActivity<*>? = null
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DevActivity<*>) {
            currentActivity = context
            currentActivity?.onFragmentAttached(this.javaClass.simpleName)
        }
    }

    override fun onDetach() {
        currentActivity?.onFragmentDetached(this.javaClass.simpleName)
        currentActivity = null
        super.onDetach()
    }

    private fun onViewReady() {
        initData()
        initUI()
        initAction()
        initObserver()
        if (devView != null && vm::class.simpleName != DevViewModel::class.simpleName) observeDevView()
    }

    private fun observeDevView() {
        vm.isSessionEndLiveData.observe(viewLifecycleOwner) { isEnd ->
            if (isEnd) {
                devView?.onSessionEnd()
            }
        }
        vm.messageAlertLiveData.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) devView?.onMessageAlert(msg)
        }
        vm.messageErrorLiveData.observe(viewLifecycleOwner) { msg ->
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
     * Method to action to do in fragment
     */
    abstract fun initAction()

    /**
     * Method to initialize observer
     */
    abstract fun initObserver()

    interface AttachListener {

        /**
         * Action when fragment attached
         */
        fun onFragmentAttached(tag: String)

        /**
         * Action when fragment detached
         */
        fun onFragmentDetached(tag: String)
    }

    override fun onMessageAlert(message: String) {}

    override fun onMessageError(message: String) {}

    override fun onSessionEnd() {}

    protected fun refreshActivity(){
        val mCurrentActivity: Activity? = activity
        val intent = activity?.intent
        val tempBundle = Bundle()
        intent?.putExtra("bundle", tempBundle)

        mCurrentActivity?.finish()
        mCurrentActivity?.overridePendingTransition(R.anim.anim_flip_in, R.anim.anim_flip_out)
        mCurrentActivity?.startActivity(intent)
    }
}