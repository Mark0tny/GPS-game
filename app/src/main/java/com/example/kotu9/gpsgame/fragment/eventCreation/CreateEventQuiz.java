package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.adapters.QuizRecyclerViewAdapter;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.Question;
import com.example.kotu9.gpsgame.model.QuizType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateEventQuiz extends Fragment implements View.OnClickListener {

    private static final String TAG = Activity.class.getSimpleName();
    private static final int QUESTIONS_LIMIT = 5;
    private static final int ANSWER_LIMIT = 4;

    private Button btnSubmitQuiz, btnSubmitQuestion, btnAddAnswer;
    private EditText editTextQuestion, editTextAnswer;
    private RadioButton radioAnswer;
    private Question question;
    private List<Question> questions;
    private QuizType event;
    private NavController navController;
    private boolean correctAnswerSet = false;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public CreateEventQuiz() {
    }

    public static CreateEventQuiz newInstance() {
        CreateEventQuiz fragment = new CreateEventQuiz();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = new Question();
        question.answers = new HashMap<>();
        questions = new ArrayList<>();
        event = new QuizType((Event) getArguments().get(String.valueOf(R.string.eventBundle)));
        Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_quiz, container, false);

        setupViews(view);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerAnswers);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new QuizRecyclerViewAdapter(getContext(), questions);
        recyclerView.setAdapter(mAdapter);
    }

    private void setupViews(View view) {
        btnSubmitQuiz = view.findViewById(R.id.submitQuiz);
        btnSubmitQuestion = view.findViewById(R.id.submitQuestion);
        btnAddAnswer = view.findViewById(R.id.buttonAddAnswer);
        editTextQuestion = view.findViewById(R.id.editTextQuestion);
        editTextAnswer = view.findViewById(R.id.editTextAnswer);
        radioAnswer = view.findViewById(R.id.radioBtnAnswer);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        btnSubmitQuiz.setOnClickListener(this);
        btnSubmitQuestion.setOnClickListener(this);
        btnAddAnswer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitQuiz:
                submitQuiz();
                break;
            case R.id.buttonAddAnswer:
                addAnswerToMap();
                break;
            case R.id.submitQuestion:
                submitQuestion();
                break;
        }
    }

    //TODO uwzględnić ze może być tylko 1 poprawna odpowiedz
    //TODO zabezpieczyć przed dodawaniem takich samych odpowiedzi
    private void addAnswerToMap() {
        if (question.answers.size() < ANSWER_LIMIT)
            question.answers.put(getAnswerFormEditText(), correctAnswerSet);
        else
            Toast.makeText(getContext(), "To much answers", Toast.LENGTH_SHORT).show();

        clearAnswerField();
        Toast.makeText(getContext(), question.answers.toString(), Toast.LENGTH_SHORT).show();
    }

    private String getAnswerFormEditText() {
        checkAnswer();
        checkAnswerRadio();
        return editTextAnswer.getText().toString().trim();
    }

    private void clearAnswerField() {
        editTextAnswer.setText("");
        radioAnswer.setChecked(false);
    }

    private void submitQuestion() {
        if (question.answers.size() == ANSWER_LIMIT) {
            question.question = editTextQuestion.getText().toString().trim();
            clearQuestionField();
        } else {
            Toast.makeText(getContext(), "Not enough answers. Add " + (ANSWER_LIMIT - question.answers.size()) + "more", Toast.LENGTH_SHORT).show();
        }
        questions.add(question);
        mAdapter.notifyDataSetChanged();
    }

    private void clearQuestionField() {
        editTextQuestion.setText("");
    }

    private void checkAnswer() {
        if (TextUtils.isEmpty(editTextQuestion.getText().toString())) {
            editTextQuestion.setError("Please enter question");
            editTextQuestion.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(editTextAnswer.getText().toString())) {
            editTextAnswer.setError("Please enter answer");
            editTextAnswer.requestFocus();
            return;
        }
        return;
    }

    private void checkAnswerRadio() {
        correctAnswerSet = radioAnswer.isChecked();
    }

    private void submitQuiz() {
        if (checkSizeOfQuestions()) {
            setEventQuiz();
            Bundle eventBundle = new Bundle();
            eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
            navController.navigate(R.id.createEventMarker, eventBundle);
        } else
            Toast.makeText(getContext(), "Please add questions to quiz", Toast.LENGTH_SHORT).show();

    }

    private void setEventQuiz() {
        event.setQuestionsList(questions);
    }

    private boolean checkSizeOfQuestions() {
        if (event.questionsList.size() > 0 && event.questionsList.size() <= QUESTIONS_LIMIT)
            return true;
        else return false;
    }
}
