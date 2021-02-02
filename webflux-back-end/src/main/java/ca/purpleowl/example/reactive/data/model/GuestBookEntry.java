package ca.purpleowl.example.reactive.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Document(collection = "guestbook")
@NoArgsConstructor
public class GuestBookEntry {
    @Id
    private String id;
    private String message;
}
