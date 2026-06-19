package pe.edu.upc.medibridge.communication.domain.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.UserConnectionStatus;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "connected_users")
public class ConnectedUser {
    @Id
    private String id;

    @Indexed(unique = true)
    private Long userId;

    private String username;
    private String fullName;
    private UserConnectionStatus status;
    private Instant connectedAt;
    private Instant disconnectedAt;
}
