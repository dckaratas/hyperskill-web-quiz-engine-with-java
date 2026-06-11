package engine.repository;

import engine.model.Question;
import engine.model.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends PagingAndSortingRepository<Question, Integer>, CrudRepository<Question, Integer> {
    Optional<Question> findById(int id);

    Page<QuestionResponse> findAllProjectedBy(Pageable pageable);
}
