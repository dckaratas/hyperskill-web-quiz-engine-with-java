package engine.controller;

import engine.exception.UserUnauthorizedException;
import engine.model.*;
import engine.service.QuizHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizHandler quizHandler;

    @Autowired
    public QuizController(QuizHandler quizHandler) {
        this.quizHandler = quizHandler;
    }

    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(@RequestParam int page) {
        return new ResponseEntity<>(quizHandler.getQuestions(PageRequest.of(page, 10)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(quizHandler.getQuestionById(id), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> saveQuestion(@Valid @RequestBody Question question, Authentication authentication) {
        try {
            return new ResponseEntity<>(quizHandler.saveQuestion(question, authentication.getName()), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/solve")
    public ResponseEntity<Answer> solveQuestion(@PathVariable Integer id, @Valid @RequestBody AnswerRequest answer, Authentication authentication) {
        try {
            return new ResponseEntity<>(quizHandler.checkAnswerForQuestion(id, answer, authentication.getName()), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer id, Authentication authentication) {
        try {
            quizHandler.deleteQuestion(id, authentication.getName());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserUnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<SolvedQuestionDTO>> getAllCompletedQuestions(@AuthenticationPrincipal User user, @RequestParam int page) {
        return new ResponseEntity<>(quizHandler.getSolvedQuestions((long) user.getId(), PageRequest.of(page, 10, Sort.by("solvedDateTime").descending())), HttpStatus.OK);
    }
}
