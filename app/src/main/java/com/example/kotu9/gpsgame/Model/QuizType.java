package com.example.kotu9.gpsgame.Model;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode(callSuper=false)
@NonNull
@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
public class QuizType extends EventType{

    public List<Question> questionsList;
}
