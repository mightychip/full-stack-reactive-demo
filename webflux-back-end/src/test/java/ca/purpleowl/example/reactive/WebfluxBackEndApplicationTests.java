package ca.purpleowl.example.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Very simple smoke test.  Does the application explode when we try to start it??
 * <br/><br/>
 * This stands up the entire ApplicationContext to see if anything breaks.  You would be amazed at how many times
 * a simple test like this can expose a critical problem before wasting the often limited time of QA resources.
 */
@SpringBootTest
class WebfluxBackEndApplicationTests {

    @Test
    void contextLoads() {
        // Is the application able to successfully do nothing without catching fire?
        // Sounds worthy of release to me. Ship it!
    }
}
