package ca.purpleowl.example.reactive.config;

import ca.purpleowl.example.reactive.config.properties.CorsConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * This is our general configuration for Webflux.  This is where we'll specify things like CORS configuration.  In fact,
 * at this point in the example configuration of CORS is <i>all</i> we do here.
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    private final CorsConfigurationProperties corsConfig;

    @Autowired
    public WebFluxConfig(CorsConfigurationProperties corsConfig) {
        this.corsConfig = corsConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsConfig.getAllowedOrigins())
                .allowedMethods("GET", "POST")
                .maxAge(3600);
    }
}
