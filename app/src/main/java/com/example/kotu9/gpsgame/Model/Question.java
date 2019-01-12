package com.example.kotu9.gpsgame.Model;


import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@NoArgsConstructor
class Question {
    public String question;
    public Map<String, Boolean> answers = new HashMap<>();
}
