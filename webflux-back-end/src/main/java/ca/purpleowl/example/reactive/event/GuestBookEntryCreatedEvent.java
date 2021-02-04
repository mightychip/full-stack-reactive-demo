package ca.purpleowl.example.reactive.event;

import ca.purpleowl.example.reactive.data.model.GuestBookEntry;
import org.springframework.context.ApplicationEvent;

/**
 * Simple event which provides a copy of the GuestBookEntry which was just created.
 */
public class GuestBookEntryCreatedEvent extends ApplicationEvent {
    public GuestBookEntryCreatedEvent(GuestBookEntry source) {
        super(source);
    }
}
