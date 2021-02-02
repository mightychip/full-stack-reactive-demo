package ca.purpleowl.example.reactive.event.publisher;

import ca.purpleowl.example.reactive.event.GuestBookEntryCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class GuestBookEntryCreatedEventPublisher implements ApplicationListener<GuestBookEntryCreatedEvent>,
                                                            Consumer<FluxSink<GuestBookEntryCreatedEvent>> {

    private final Executor executor;
    private final BlockingQueue<GuestBookEntryCreatedEvent> queue;

    public GuestBookEntryCreatedEventPublisher(Executor executor) {
        this.executor = executor;
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void accept(FluxSink<GuestBookEntryCreatedEvent> sink) {
        executor.execute(() -> {
            // TODO do something more elegant than an infinite loop
            //noinspection InfiniteLoopStatement
            while(true) {
                try {
                    GuestBookEntryCreatedEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onApplicationEvent(GuestBookEntryCreatedEvent guestBookEntryCreatedEvent) {
        queue.offer(guestBookEntryCreatedEvent);
    }
}
