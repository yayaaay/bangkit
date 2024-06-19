package amirlabs.sapasemua.utils.rx3

import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import io.reactivex.rxjava3.core.Flowable

class FlowableStreamAdapter<T : Any> : StreamAdapter<T, Flowable<T>> {

    override fun adapt(stream: Stream<T>): Flowable<T> = Flowable.fromPublisher(stream)
}
