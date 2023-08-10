package org.forum.historyservice.service;

import org.forum.historyservice.dto.request.ViewRequest;
import org.forum.historyservice.dto.response.GeneralResponse;
import org.forum.historyservice.dto.response.StatusResponse;
import org.forum.historyservice.entity.History;
import org.forum.historyservice.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HistoryService {
    private HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public GeneralResponse viewPost(ViewRequest dto, String token) {
        historyRepository.createHistory(dto, token);
        return GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .message("History saved!")
                        .statusCode(200)
                        .success(true)
                        .build())
                .build();
    }

    public List<History> getHistoryByUserID(String token) {
        List<History> list = historyRepository.getHistoryByUserID(token);
        return list;
    }
}
