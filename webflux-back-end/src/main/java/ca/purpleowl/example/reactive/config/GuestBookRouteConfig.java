package ca.purpleowl.example.reactive.config;

import ca.purpleowl.example.reactive.data.handler.GuestBookHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class GuestBookRouteConfig {
    @Bean
    public RouterFunction<ServerResponse> guestbookRoutes(GuestBookHandler handler) {
        return route(GET("/guestbook"), handler::all)
                .andRoute(GET("/guestbook/{id}"), handler::getById)
                .andRoute(POST("/guestbook"), handler::create);
    }
}
