package amirlabs.sapasemua.data.api.service

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.rxjava3.core.Flowable

interface WebSocketService {
    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocket.Event>
    @Send
    fun sendCoordinates(subscribe: Subscribe)
    @Receive
    fun observeCoordinates(): Flowable<String>
}