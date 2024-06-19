package amirlabs.sapasemua.base

interface DevView {
    fun onMessageAlert(message:String)
    fun onMessageError(message: String)
    fun onSessionEnd()
}