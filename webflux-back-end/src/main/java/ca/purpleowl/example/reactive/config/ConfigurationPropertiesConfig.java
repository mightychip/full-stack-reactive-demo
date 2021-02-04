package ca.purpleowl.example.reactive.config;

import ca.purpleowl.example.reactive.config.properties.CorsConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CorsConfigurationProperties.class})
public class ConfigurationPropertiesConfig {
}
