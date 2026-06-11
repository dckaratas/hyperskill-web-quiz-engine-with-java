package engine.repository;

import engine.model.Question;
import engine.model.SolvedQuestion;
import engine.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SolvedQuestionRepository extends PagingAndSortingRepository<SolvedQuestion, Long>, CrudRepository<SolvedQuestion, Long> {
    Page<SolvedQuestion> findByUserId(Long userId, Pageable pageable);
    boolean existsByUserAndQuestion(User user, Question question);
    void deleteByQuestion(Question question);
}
