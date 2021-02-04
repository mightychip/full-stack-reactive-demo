package ca.purpleowl.example.reactive.data.service;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import ca.purpleowl.example.reactive.data.repository.GuestBookRepository;
import ca.purpleowl.example.reactive.event.GuestBookEntryCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GuestBookService {
    private final GuestBookRepository repo;
    private final ApplicationEventPublisher publisher;

    public GuestBookService(GuestBookRepository repo, ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public Flux<GuestBookEntry> all() {
        return repo.findAll();
    }

    public Mono<GuestBookEntry> get(String id) {
        return repo.findById(id);
    }

    public Mono<GuestBookEntry> create(GuestBookEntry entry) {
        return repo
                .save(new GuestBookEntry(UUID.randomUUID().toString(), entry.getMessage()))
                .doOnSuccess(e -> publisher.publishEvent(new GuestBookEntryCreatedEvent(e)));
    }
}
