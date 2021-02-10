package ca.purpleowl.example.reactive.service;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import ca.purpleowl.example.reactive.data.repository.GuestBookRepository;
import ca.purpleowl.example.reactive.data.service.GuestBookService;
import ca.purpleowl.example.reactive.event.GuestBookEntryCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

/**
 * We're testing the {@link GuestBookService} here, but we'll manually create it rather than leaning on Autowiring.
 * This allows us to more easily and reliably insert a mocked ApplicationEventPublisher to verify that it is called upon
 * save.
 */
@DataMongoTest
public class GuestBookServiceTests {

    @Autowired
    private GuestBookRepository repository;

    @Mock
    private ApplicationEventPublisher mockPublisher;

    private GuestBookService fixture;

    @BeforeEach
    public void init() {
        fixture = new GuestBookService(repository, mockPublisher);
    }

    @Test
    public void saveTest() {
        // First we set up our mocked method call on our mocked ApplicationEventPublisher.
        Mockito.doNothing().when(mockPublisher).publishEvent(Mockito.any(GuestBookEntryCreatedEvent.class));

        // Now we'll create the entry we're going to try to save.
        GuestBookEntry entry = new GuestBookEntry(null, "This is a message.");

        // Next, we use the StepVerifier on the GuestBookEntry mono emitted by GuestBookService.create(...)
        Mono<GuestBookEntry> createEntry = fixture.create(entry);
        StepVerifier.create(createEntry)
                    // Here, we verify that the next element emitted has an assigned ID, meaning it was saved.
                    .expectNextMatches(saved -> StringUtils.isNotBlank(saved.getId()))
                    // Now, we verify that there are no more elements and execute the above verification.  If this
                    // method isn't added, then our verifications (ie. the call to .expectNextMatches(...)) will not be
                    // executed.
                    .verifyComplete();

        // Now that we've verified the chain of operations is complete, we can check to see if our Publisher was called
        // during the course of operations. It should have been called exactly once.
        Mockito.verify(mockPublisher, Mockito.times(1)).publishEvent(Mockito.any(GuestBookEntryCreatedEvent.class));
    }

    @Test
    public void failedSaveTest() {
        // We'll need a different repository for this.  It needs to fail when called.
        GuestBookRepository mockRepo = Mockito.mock(GuestBookRepository.class);
        Mockito.when(mockRepo.save(Mockito.any(GuestBookEntry.class)))
               .thenReturn(Mono.error(new Exception("Fake error")));

        // We should also set this method up in case it gets called... but it shouldn't.
        Mockito.doNothing().when(mockPublisher).publishEvent(Mockito.any(GuestBookEntryCreatedEvent.class));

        // Create a new GuestBookService which includes our mockRepo.
        fixture = new GuestBookService(mockRepo, mockPublisher);

        GuestBookEntry entry = new GuestBookEntry(null, "Burn baby, burn!");

        // This call will definitely fail.
        Mono<GuestBookEntry> result = fixture.create(entry);

        StepVerifier.create(result)
                    // Verify the failure occurred
                    .expectError()
                    .verify();

        // Now make sure the mockPublisher never gets called.
        Mockito.verify(mockPublisher, Mockito.never()).publishEvent(Mockito.any(GuestBookEntryCreatedEvent.class));
    }

    @Test
    public void getAllTest() {
        // Let's quickly save a bunch of profiles.
        Flux<GuestBookEntry> savedElements = repository.saveAll(Flux.just(
                new GuestBookEntry(null, "Test message 1"),
                new GuestBookEntry(null, "Test message 2"),
                new GuestBookEntry(null, "Test message 3"),
                new GuestBookEntry(null, "Test message 4"),
                new GuestBookEntry(null, "Test message 5")
        ));

        // Now we'll append our test data to this call.
        Flux<GuestBookEntry> getAll = fixture.all().thenMany(savedElements);

        @SuppressWarnings("ConstantConditions") // The NPE IntelliJ complains about will not occur here.
        Predicate<GuestBookEntry> match = entry -> savedElements.any(savedItem -> savedItem.equals(entry)).block();

        StepVerifier.create(getAll)
                    // This looks a little weird, but what we're doing is verifying that the next 5 emitted elements
                    // match those which we created.
                    .expectNextMatches(match)
                    .expectNextMatches(match)
                    .expectNextMatches(match)
                    .expectNextMatches(match)
                    .expectNextMatches(match)
                    .verifyComplete();
    }

    @Test
    public void getByIdTest() {
        GuestBookEntry createMe = new GuestBookEntry(null, "Test message");

        Mono<GuestBookEntry> savedEntry = fixture.create(createMe)
                                                 .flatMap(entry -> fixture.get(entry.getId()));

        StepVerifier.create(savedEntry)
                    // This time, we check to make sure the ID was assigned AND that the message matches what we expect.
                    .expectNextMatches(entry -> StringUtils.isNotBlank(entry.getId()) &&
                                                createMe.getMessage().equals(entry.getMessage()))
                    .verifyComplete();
    }
}
