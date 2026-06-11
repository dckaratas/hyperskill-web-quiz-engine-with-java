package engine.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AnswerRequest(@NotNull List<Integer> answer) {
}
