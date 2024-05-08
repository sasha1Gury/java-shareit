package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(long requestAuthorId, ItemRequestDto itemRequestDto) {
        return post("", requestAuthorId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllItemRequests(long requestAuthorId) {
        return get("", requestAuthorId);
    }

    public ResponseEntity<Object> getAllRequests(long requestAuthorId, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("size", size);
        return get("/all?from={from}&size={size}", requestAuthorId, parameters);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}