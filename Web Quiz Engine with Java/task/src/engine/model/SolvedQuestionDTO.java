package engine.model;

import java.time.LocalDateTime;

public record SolvedQuestionDTO(Integer id, LocalDateTime completedAt) {
}
