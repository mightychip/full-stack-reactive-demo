package ca.purpleowl.example.reactive.websocket;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a relatively simple test for our WebSocket implementation.  For now, this is going to be pretty basic.
 */
@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIntegrationTests {

    private static final String GUESTBOOK_ENDPOINT_URL = "http://localhost:%d/guestbook";
    private static final String GUESTBOOK_WEBSOCKET_URL = "ws://localhost:%d/ws/guestbook";

    private final int port;

    public WebSocketIntegrationTests(@LocalServerPort int port) {
        this.port = port;
    }

    private final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

    private final WebClient webClient = WebClient.builder().build();

    @Test
    public void notificationsOnUpdatesTest() throws InterruptedException {
        int itemsToWrite = 10;
        // Since our WebSocketClient will be doing things across multiple threads, we'll need to use an AtomicLong to
        // track the number of messages we've read.
        AtomicLong messagesRead = new AtomicLong();

        URI uri = URI.create(String.format(GUESTBOOK_WEBSOCKET_URL, port));

        webSocketClient.execute(uri, (WebSocketSession session) -> {
            // With our current implementation, a WebSocket session is initiated with a throwaway message.  The content
            // of the message is completely meaningless.
            Mono<WebSocketMessage> out = Mono.just(session.textMessage("Talk to me."));

            // This is the operation when we receive a message.  In this case, we're just dumping it to a String and
            // emitting it as an element.
            Flux<String> in = session.receive().map(WebSocketMessage::getPayloadAsText);

            // Now we chain together the operations for our WebSocket session.
            return session.send(out)
                          .thenMany(in)
                          // For every message we receive, we'll log the message and increment our counter.
                          .doOnNext(message -> {
                              log.debug(String.format("Just received message: [%s]", message));
                              messagesRead.incrementAndGet();
                          })
                          .then();
        }).subscribe();  // Without calling .subscribe(), none of these actions will get executed.

        // The WebSocketClient is now subscribed and listening, so let's create our guestbook entries.
        Flux.<GuestBookEntry>generate(sink -> sink.next(generateRandomEntry()))
                // We'll do this exactly 10 times (or whatever the value of itemsToWrite is).
                .take(itemsToWrite)
                // Here, we flatMap the results through our guestbook endpoint.
                .flatMap(this::postToGuestBookEndpoint)
                .blockLast();

        // Since this chain of operations is asynchronous, let's give everything a chance to catch up.
        Thread.sleep(100);

        Assertions.assertEquals(itemsToWrite, messagesRead.get(),
                        "There should be one WebSocket message for every GuestBookEntry created.");
    }

    private Publisher<GuestBookEntry> postToGuestBookEndpoint(GuestBookEntry entry) {
        // This utilizes the webclient to make a POST call against the GuestBook endpoint
        return webClient.post()
                        .uri(String.format(GUESTBOOK_ENDPOINT_URL, port))
                        .body(BodyInserters.fromValue(entry))
                        .retrieve()
                        .bodyToMono(String.class)
                        .log()
                        .thenReturn(entry);
    }

    private GuestBookEntry generateRandomEntry() {
        String randomUUID = UUID.randomUUID().toString();
        return new GuestBookEntry(randomUUID, String.format("This is a message for user %s", randomUUID));
    }
}
