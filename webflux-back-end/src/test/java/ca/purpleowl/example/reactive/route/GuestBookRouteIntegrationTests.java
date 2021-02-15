package ca.purpleowl.example.reactive.route;

import ca.purpleowl.example.reactive.config.GuestBookRouteConfig;
import ca.purpleowl.example.reactive.config.properties.CorsConfigurationProperties;
import ca.purpleowl.example.reactive.data.handler.GuestBookHandler;
import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import ca.purpleowl.example.reactive.data.repository.GuestBookRepository;
import ca.purpleowl.example.reactive.data.service.GuestBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This tests our Guest Book endpoints by binding our {@link WebTestClient} to the method which builds the related
 * {@link org.springframework.web.reactive.function.server.RouterFunction}.
 * <br/><br/>
 * This serves as an integration test of all but the data layer of the application.  We could also potentially use a
 * real data layer, but using a mocked Repo allows us to sidestep the generation of random UUIDs in
 * {@link GuestBookService}.
 */
@Import({
            GuestBookRouteConfig.class,
            GuestBookHandler.class,
            GuestBookService.class,
            // Strangely, we end up needing to import this despite never actually using it.
            CorsConfigurationProperties.class
        })
@WebFluxTest
public class GuestBookRouteIntegrationTests {

    private static final String GUESTBOOK_URI = "/guestbook";

    @MockBean
    private GuestBookRepository mockRepo;

    private final WebTestClient client;

    @Autowired
    public GuestBookRouteIntegrationTests(GuestBookRouteConfig routeConfig, GuestBookHandler guestBookHandler) {
        this.client = WebTestClient.bindToRouterFunction(routeConfig.guestbookRoutes(guestBookHandler)).build();
    }

    @Test
    public void getAllTest() {
        Mockito.when(mockRepo.findAll()).thenReturn(Flux.just(sampleData()));

        ResponseSpec response = client.get()
                                      .uri(GUESTBOOK_URI)
                                      .accept(MediaType.APPLICATION_JSON)
                                      .exchange();

        response.expectStatus().isOk()
                // Verify the content header is properly specified.
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                // Ideally, we should specify certain values at certain fields for validation. This allows us to
                // avoid fields which will have inconsistent values (such as created timestamps) and sidesteps issues
                // with inconsistent field ordering.
                    .jsonPath("$.[0].id").isEqualTo("1")
                    .jsonPath("$.[0].message").isEqualTo("Message 1")
                    .jsonPath("$.[1].id").isEqualTo("2")
                    .jsonPath("$.[1].message").isEqualTo("Message 2");
    }

    @Test
    public void saveTest() {
        GuestBookEntry testData = new GuestBookEntry(null, "Test message");
        GuestBookEntry savedData = new GuestBookEntry("test-uri", "Test message");

        Mockito.when(mockRepo.save(Mockito.any(GuestBookEntry.class)))
              .thenReturn(Mono.just(savedData));

        ResponseSpec response = client.post()
                                      .uri(GUESTBOOK_URI)
                                      .accept(MediaType.APPLICATION_JSON)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .body(Mono.just(testData), GuestBookEntry.class)
                                      .exchange();

        response.expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/guestbook/test-uri")
                .expectBody().isEmpty();
    }

    /**
     * This simply builds out some sample data.  While it's only used in one place, generating this data distracts
     * from the function of the test.
     *
     * @return An array of {@link GuestBookEntry} objects representing some fake data.
     */
    private GuestBookEntry[] sampleData() {
        GuestBookEntry[] returnMe = new GuestBookEntry[5];
        for (int i = 0; i < 5; i++) {
            GuestBookEntry entry = new GuestBookEntry(Integer.toString(i+1), String.format("Message %d", i+1));
            returnMe[i] = entry;
        }
        return returnMe;
    }
}
