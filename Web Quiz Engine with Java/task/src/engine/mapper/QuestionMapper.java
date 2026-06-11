package engine.mapper;

import engine.model.Question;
import engine.model.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public class QuestionMapper {
    public static QuestionResponse toResponse(Question question) {
        return new QuestionResponse(question.getId(), question.getTitle(), question.getText(), question.getOptions());
    }

}
