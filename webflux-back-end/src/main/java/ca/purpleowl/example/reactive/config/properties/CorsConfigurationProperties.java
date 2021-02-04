package ca.purpleowl.example.reactive.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application")
@Data
public class CorsConfigurationProperties {
    private String[] allowedOrigins;
}
