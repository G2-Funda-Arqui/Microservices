package pe.edu.upc.medibridge.communication.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;
import pe.edu.upc.medibridge.communication.domain.model.exceptions.ChatRoomNotAvailableException;
import pe.edu.upc.medibridge.communication.domain.services.ChatMessageService;
import pe.edu.upc.medibridge.communication.domain.services.ChatRoomService;
import pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories.ChatMessageRepository;
import pe.edu.upc.medibridge.communication.infrastructure.websocket.RealtimeMessagingService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RealtimeMessagingService realtimeMessagingService;

    public ChatMessageServiceImpl(
            ChatMessageRepository chatMessageRepository,
            ChatRoomService chatRoomService,
            RealtimeMessagingService realtimeMessagingService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
        this.realtimeMessagingService = realtimeMessagingService;
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderUserId(), chatMessage.getRecipientUserId(), true)
                .orElseThrow(() -> new ChatRoomNotAvailableException(
                        chatMessage.getSenderUserId(),
                        chatMessage.getRecipientUserId()));
        chatMessage.setChatId(chatId);
        if (chatMessage.getSentAt() == null) {
            chatMessage.setSentAt(Instant.now());
        }
        var saved = chatMessageRepository.save(chatMessage);
        realtimeMessagingService.sendChatMessage(saved);
        return saved;
    }

    @Override
    public List<ChatMessage> findChatMessages(Long senderUserId, Long recipientUserId) {
        var chatId = chatRoomService.getChatRoomId(senderUserId, recipientUserId, false);
        return chatId.map(chatMessageRepository::findByChatIdOrderBySentAtAsc).orElse(new ArrayList<>());
    }
}
