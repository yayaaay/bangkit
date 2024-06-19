package amirlabs.sapasemua.ui.menu.translate

import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.data.api.NetworkConfig
import amirlabs.sapasemua.data.repo.MainRepository
import amirlabs.sapasemua.utils.mediapipe.HandLandmarkerHelper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tinder.scarlet.WebSocket
import io.reactivex.rxjava3.disposables.CompositeDisposable

class TranslateViewModel(private val repo: MainRepository) : DevViewModel(){
    private val disposable = CompositeDisposable()
    private var _delegate: Int = HandLandmarkerHelper.DELEGATE_GPU
    private var _minHandDetectionConfidence: Float =
        HandLandmarkerHelper.DEFAULT_HAND_DETECTION_CONFIDENCE
    private var _minHandTrackingConfidence: Float = HandLandmarkerHelper
        .DEFAULT_HAND_TRACKING_CONFIDENCE
    private var _minHandPresenceConfidence: Float = HandLandmarkerHelper
        .DEFAULT_HAND_PRESENCE_CONFIDENCE
    private var _maxHands: Int = HandLandmarkerHelper.DEFAULT_NUM_HANDS

    val currentDelegate: Int get() = _delegate
    val currentMinHandDetectionConfidence: Float
        get() =
            _minHandDetectionConfidence
    val currentMinHandTrackingConfidence: Float
        get() =
            _minHandTrackingConfidence
    val currentMinHandPresenceConfidence: Float
        get() =
            _minHandPresenceConfidence
    val currentMaxHands: Int get() = _maxHands

    private val _eventListener = MutableLiveData<WebSocket.Event>()
    val eventListener: LiveData<WebSocket.Event>
        get() = _eventListener

    private var socketService = NetworkConfig.socketService

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinHandDetectionConfidence(confidence: Float) {
        _minHandDetectionConfidence = confidence
    }
    fun setMinHandTrackingConfidence(confidence: Float) {
        _minHandTrackingConfidence = confidence
    }
    fun setMinHandPresenceConfidence(confidence: Float) {
        _minHandPresenceConfidence = confidence
    }

    fun setMaxHands(maxResults: Int) {
        _maxHands = maxResults
    }

    fun listenEvent() {
        socketService.observeWebSocketEvent()
            .subscribe {
                _eventListener.postValue(it)
            }.let(disposable::add)
    }

    fun sendCoordinates(body:Subscribe){
        socketService.sendCoordinates(body)
    }

    fun refreshConnection(){
        socketService = NetworkConfig.socketService
        socketService.observeWebSocketEvent()
            .subscribe {
                _eventListener.postValue(it)
            }.let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}