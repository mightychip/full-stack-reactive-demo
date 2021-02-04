package ca.purpleowl.example.reactive.config;

import ca.purpleowl.example.reactive.event.GuestBookEntryCreatedEvent;
import ca.purpleowl.example.reactive.event.publisher.GuestBookEntryCreatedEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@Log4j2
public class WebSocketConfig {

    @Bean
    public Executor executor() {
        // This would probably be insufficient for production situations
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public WebSocketHandler webSocketHandler(ObjectMapper objectMapper,
                                             GuestBookEntryCreatedEventPublisher publisher) {

        // .share() is the most important part here.  If this is not used, then consumers exclusively consume their
        // messages, meaning each user browsing the GuestBook would see different updates... and nobody would see the
        // same updates.  There are some cases where this kind of behaviour can be desirable, but not here.
        Flux<GuestBookEntryCreatedEvent> broadcaster = Flux.create(publisher).share();

        // TODO add a description of what's happening here
        return session -> {
            Flux<WebSocketMessage> messageFlux = broadcaster
                    .map(event -> {
                        try {
                            return objectMapper.writeValueAsString(event.getSource());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(json -> {
                        log.info(String.format("Sending [%s]", json));
                        return session.textMessage(json);
                    });

            return session.send(messageFlux);
        };
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler handler) {
        return new SimpleUrlHandlerMapping() {
            // Static initialization is not something you see very commonly, but we have to use it here for some simple
            // configuration... there's likely a more elegant way to do this.
            {
                setUrlMap(Collections.singletonMap("/ws/guestbook", handler));
                setOrder(10); // TODO so what does this do??
            }
        };
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
