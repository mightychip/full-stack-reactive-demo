package ca.purpleowl.example.reactive.data;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import ca.purpleowl.example.reactive.data.repository.GuestBookRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
@Log4j2
public class GuestBookDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final GuestBookRepository repo;

    public GuestBookDataInitializer(GuestBookRepository repo) {
        this.repo = repo;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Random generator = new Random();

        repo.deleteAll()
            .thenMany(
                    Flux.just("Have a wonderful day!", "It was great to visit.", "I stole all of your forks.", "Great party.  Thanks for inviting me!", "Great times, great company.")
                        .map(message -> new GuestBookEntry(UUID.randomUUID().toString(), message))
                        .flatMap(repo::save)
            )
            .thenMany(repo.findAll())
            .subscribe(entry -> log.info(String.format("Saved [%s]", entry.toString())));
    }
}
