package engine.service;

import engine.exception.UserUnauthorizedException;
import engine.mapper.QuestionMapper;
import engine.model.*;
import engine.repository.QuestionRepository;
import engine.repository.SolvedQuestionRepository;
import engine.repository.UserRepository;
import jakarta.persistence.OrderBy;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QuizHandler {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final SolvedQuestionRepository solvedQuestionRepository;

    public QuizHandler(QuestionRepository questionRepository, UserRepository userRepository, SolvedQuestionRepository solvedQuestionRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.solvedQuestionRepository = solvedQuestionRepository;
    }

    public Page<SolvedQuestionDTO> getSolvedQuestions(Long userId, Pageable pageable) {
        return solvedQuestionRepository.findByUserId(userId, pageable).map(q -> new SolvedQuestionDTO(q.getQuestion().getId(), q.getSolvedDateTime()));
    }

    public Page<QuestionResponse> getQuestions(Pageable pageable) {
        return this.questionRepository.findAllProjectedBy(pageable);
    }

    public QuestionResponse saveQuestion(Question question, String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        question.setUser(user);
        questionRepository.save(question);
        return QuestionMapper.toResponse(question);
    }
    @Transactional
    public void deleteQuestion(int id, String email) throws UsernameNotFoundException, UserUnauthorizedException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));
        if (question.getUser().getId() == user.getId()) {
            solvedQuestionRepository.deleteByQuestion(question);
            questionRepository.delete(question);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    public QuestionResponse getQuestionById(Integer id) throws NoSuchElementException {
        Optional<Question> question = questionRepository.findById(id);
        return new QuestionResponse(question.get().getId(), question.get().getTitle(), question.get().getText(), question.get().getOptions());
    }

    public Answer checkAnswerForQuestion(Integer id, AnswerRequest answer, String email) throws NoSuchElementException {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) throw new NoSuchElementException();

        AtomicInteger trues = new AtomicInteger(0);
        question.get().getAnswer().forEach(i -> {
            if (answer.answer().contains(i)) {
                trues.incrementAndGet();
            }
        });
        if (answer.answer().size() == question.get().getAnswer().size()
                && trues.get() == question.get().getAnswer().size()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//            boolean alreadySolved = solvedQuestionRepository
//                    .existsByUserAndQuestion(user, question.get());
//            if (!alreadySolved) {
//                solvedQuestionRepository.save(new SolvedQuestion(null, question.get(), user, LocalDateTime.now()));
//            }
            solvedQuestionRepository.save(new SolvedQuestion(null, question.get(), user, LocalDateTime.now()));
            return new Answer(true, "Congratulations, you're right!");
        } else {
            return new Answer(false, "Wrong answer! Please, try again.");
        }

    }

}
