package ca.purpleowl.example.reactive.data.handler;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import ca.purpleowl.example.reactive.data.service.GuestBookService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class GuestBookHandler {
    private static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private final GuestBookService service;

    public GuestBookHandler(GuestBookService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        return defaultReadResponse(service.get(request.pathVariable("id")));
    }

    public Mono<ServerResponse> all(ServerRequest ignore) {
        return defaultReadResponse(service.all());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<GuestBookEntry> entry = request.bodyToFlux(GuestBookEntry.class)
                                            .flatMap(service::create);

        return defaultWriteResponse(entry);
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<GuestBookEntry> entries) {
        return ServerResponse.ok()
                             .contentType(MEDIA_TYPE)
                             .body(entries, GuestBookEntry.class);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<GuestBookEntry> entries) {
        return Mono.from(entries)
                   .flatMap(p -> ServerResponse.created(URI.create(String.format("/guestbook/%s", p.getId())))
                                               .build());
    }
}
