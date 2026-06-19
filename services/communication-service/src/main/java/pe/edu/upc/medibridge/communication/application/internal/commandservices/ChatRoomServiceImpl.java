package pe.edu.upc.medibridge.communication.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatRoom;
import pe.edu.upc.medibridge.communication.domain.services.ChatRoomService;
import pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories.ChatRoomRepository;

import java.util.Optional;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public Optional<String> getChatRoomId(Long senderUserId, Long recipientUserId, boolean createIfMissing) {
        return chatRoomRepository.findBySenderUserIdAndRecipientUserId(senderUserId, recipientUserId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfMissing) {
                        return Optional.empty();
                    }
                    var chatId = buildChatId(senderUserId, recipientUserId);
                    chatRoomRepository.save(ChatRoom.builder()
                            .senderUserId(senderUserId)
                            .recipientUserId(recipientUserId)
                            .chatId(chatId)
                            .build());
                    chatRoomRepository.save(ChatRoom.builder()
                            .senderUserId(recipientUserId)
                            .recipientUserId(senderUserId)
                            .chatId(chatId)
                            .build());
                    return Optional.of(chatId);
                });
    }

    private String buildChatId(Long userId1, Long userId2) {
        var min = Math.min(userId1, userId2);
        var max = Math.max(userId1, userId2);
        return min + "_" + max;
    }
}
