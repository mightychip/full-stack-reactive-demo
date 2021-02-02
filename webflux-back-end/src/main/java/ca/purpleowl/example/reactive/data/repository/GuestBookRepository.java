package ca.purpleowl.example.reactive.data.repository;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GuestBookRepository extends ReactiveMongoRepository<GuestBookEntry, String> {
}
