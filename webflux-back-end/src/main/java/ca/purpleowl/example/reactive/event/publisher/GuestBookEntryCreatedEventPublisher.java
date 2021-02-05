package ca.purpleowl.example.reactive.event.publisher;

import ca.purpleowl.example.reactive.event.GuestBookEntryCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * This is our <i>very</i> simple event publisher.  We just take the message from the queue and dump it to the {@link FluxSink}.
 * <br/><br/>
 * This is also logged at debug level to help with debugging or exploring how this works.
 */
@Component
@Log4j2
public class GuestBookEntryCreatedEventPublisher implements Consumer<FluxSink<GuestBookEntryCreatedEvent>>,
                                                            DisposableBean {

    private volatile boolean shuttingDown;
    private final BlockingQueue<GuestBookEntryCreatedEvent> queue;
    private final Executor executor;

    public GuestBookEntryCreatedEventPublisher(Executor executor) {
        this.executor = executor;
        this.queue = new LinkedBlockingQueue<>();
        this.shuttingDown = false;
    }

    @Override
    public void accept(FluxSink<GuestBookEntryCreatedEvent> sink) {
        executor.execute(() -> {
            // This thread will continue until the boolean is set to false
            while(!shuttingDown) {
                try {
                    GuestBookEntryCreatedEvent event = queue.take();
                    log.debug(String.format("Publishing GuestBookEntryCreatedEvent: [%s]", event.toString()));
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        });
    }

    /**
     * We use the {@link EventListener} annotation here to automagically create an Application Event Listener for
     * {@link GuestBookEntryCreatedEvent} instances.  We could have opted for implementing the
     * {@link ApplicationListener} interface, but this design allows for multiple handlers on the same class.
     *
     * @param guestBookEntryCreatedEvent - A {@link GuestBookEntryCreatedEvent} to be handled.
     */
    @EventListener
    public void handleGuestBookEntryCreatedEvent(GuestBookEntryCreatedEvent guestBookEntryCreatedEvent) {
        queue.offer(guestBookEntryCreatedEvent);
    }

    /**
     * When this bean goes out of scope, this method will be called to terminate the publisher thread.
     */
    @Override
    public void destroy() {
        this.shuttingDown = true;
    }
}
