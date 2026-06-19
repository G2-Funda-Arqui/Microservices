package pe.edu.upc.medibridge.communication.domain.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;

    @Indexed
    private String chatId;

    @Indexed
    private Long senderUserId;

    @Indexed
    private Long recipientUserId;

    private String content;

    @Indexed
    private Instant sentAt;
}
