package com.bank.bank.telegram.service;




import com.bank.bank.telegram.handler.model.TransferContext;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStateService {

    // Thread-safe хранилище состояний (MVP: память; Production: Redis)
    private final Map<Long, TransferContext> userStates = new ConcurrentHashMap<>();

    /**
     * Возвращает контекст пользователя.
     * Если его нет — создает новый со состоянием NONE.
     */
    public TransferContext getContext(Long telegramId) {
        return userStates.computeIfAbsent(telegramId, id -> new TransferContext());
    }

    /**
     * Сбрасывает FSM-состояние пользователя (удаляет контекст).
     */
    public void reset(Long telegramId) {
        userStates.remove(telegramId);
    }
}
