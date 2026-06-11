package engine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank()
    private String title;
    @NotBlank
    private String text;
    @NotNull
    @Size(min = 2)
    private List<String> options;

    private List<Integer> answer = new ArrayList<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<SolvedQuestion> solvedByUsers = new ArrayList<>();

}
