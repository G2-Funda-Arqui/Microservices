package pe.edu.upc.medibridge.communication.domain.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_rooms")
@CompoundIndex(name = "chat_room_participants_idx", def = "{'senderUserId': 1, 'recipientUserId': 1}", unique = true)
public class ChatRoom {
    @Id
    private String id;

    @Indexed
    private Long senderUserId;

    @Indexed
    private Long recipientUserId;

    @Indexed
    private String chatId;
}
