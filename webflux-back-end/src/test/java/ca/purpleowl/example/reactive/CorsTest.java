package ca.purpleowl.example.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

/**
 * This provides a simple test of our CORS configuration.  Because of the nature of this test, we end up needing to
 * stand up the entire application.  To facilitate parallel builds and to generally be a good neighbour when running
 * these tests on CI, we use a random port.
 * <br/><br/>
 * This comes with a bit of a tradeoff.  Ideally, we should make one client and reuse that client for all tests.  Doing
 * so, however, would require static initialization and would happen before we know the port number of the server.  To
 * step around this problem, we'll simply initialize a new client for every test, which happens well after we have
 * access to the port number the server is running on.
 * <br/><br/>
 * In order to simulate a consistent set of allowed domains, we use the <i>"test"</i> Profile, which provides a single
 * allowed origin: http://allowed-origin.ca
 */
@ActiveProfiles("test")
@SpringBootTest(classes = WebfluxBackEndApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//, properties = {"classpath:application-test.yml"})
public class CorsTest {
    private static final String BASE_URL = "http://localhost:%d";
    private static final String BASE_GUESTBOOK_URL = "/guestbook";
    private static final String CORS_ORIGIN = "http://any-origin.com";

    private WebTestClient client;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void init() {
        client = WebTestClient.bindToServer()
                .baseUrl(String.format(BASE_URL, port))
                .defaultHeader("Origin", CORS_ORIGIN)
                .build();
    }

    @Test
    void corsTestFromAllowedOrigin() {
        // Here, we supply our CORS headers.
        ResponseSpec response = client.get()
                                      .uri(BASE_GUESTBOOK_URL)
                                      .header("Origin", "http://allowed-origin.ca")
                                      .exchange();

        // we should get our CORS access control header specifying all origins are allowed.  If we'd specified a
        // specific set of allowed origins, we'd see that there.
        response.expectHeader()
                .valueEquals("Access-Control-Allow-Origin", "http://allowed-origin.ca");
    }

    @Test
    void corsTestFromDisallowedOrigin() {
        ResponseSpec response = client.get()
                                      .uri(BASE_GUESTBOOK_URL)
                                      .header("Origin", "http://disallowed-origin.ca")
                                      .exchange();

        response.expectStatus().isForbidden()
                .expectHeader().valueEquals("Vary", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers");
    }

    @Test
    void preFlightTestFromAllowedOrigin() {
        // We should also do a simple pre-flight test to make sure that calling options on the relevant endpoint
        // will actually return which methods are allowed against it.
        ResponseSpec response = client.options()
                                      .uri(BASE_GUESTBOOK_URL)
                                      .header("Access-Control-Request-Method", "GET")
                                      .header("Origin", "http://allowed-origin.ca")
                                      .exchange();

        response.expectHeader().valueEquals("Access-Control-Allow-Methods", "GET,POST")
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://allowed-origin.ca");
    }

    @Test
    void preFlightTestFromDisallowedOrigin() {
        ResponseSpec response = client.options()
                                      .uri(BASE_GUESTBOOK_URL)
                                      .header("Access-Control-Request-Method", "GET")
                                      .header("Origin", "http://disallowed-origin.ca")
                                      .exchange();

        response.expectStatus().isForbidden()
                .expectHeader().valueEquals("Vary", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers");
    }
}
