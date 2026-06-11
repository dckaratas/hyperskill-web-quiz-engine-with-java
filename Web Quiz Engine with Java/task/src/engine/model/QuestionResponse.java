package engine.model;

import java.util.List;

public record QuestionResponse(Integer id, String title, String text, List<String> options) {
}
