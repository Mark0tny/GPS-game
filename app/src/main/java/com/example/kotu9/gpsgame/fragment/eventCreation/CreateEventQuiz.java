package com.example.kotu9.gpsgame.fragment.eventCreation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

    private static final String TAG = AppCompatActivity.class.getSimpleName();
    private static final int QUESTIONS_LIMIT = 5;
    private static final int ANSWER_LIMIT = 4;

    private Button btnSubmitQuiz, btnSubmitQuestion, btnAddAnswer;
    private EditText editTextQuestion, editTextAnswer;
    private RadioButton radioAnswer;
    private Question question;
    private List<Question> questions;
    private QuizType event;
    private NavController navController;
    private int correctAnswers = 0;

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
        setRetainInstance(true);
        question = new Question();
        question.answers = new HashMap<>();
        questions = new ArrayList<>();
        event = new QuizType((Event) getArguments().get(String.valueOf(R.string.eventBundle)));
        event.questionsList = new ArrayList<>();
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
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
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
        radioAnswer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitQuiz:
                submitQuiz();
                break;
            case R.id.buttonAddAnswer:
                checkAnswer();
                addAnswerToMap();
                break;
            case R.id.submitQuestion:
                submitQuestion();
                break;
            case R.id.radioBtnAnswer:
                radioAnswerClick();
                break;
        }
    }

    private void radioAnswerClick() {
        if (!radioAnswer.isSelected()) {
            radioAnswer.setChecked(true);
            radioAnswer.setSelected(true);
        } else {
            radioAnswer.setChecked(false);
            radioAnswer.setSelected(false);
        }
    }

    private void addAnswerToMap() {
        if (question.answers.size() < ANSWER_LIMIT) {
            if (question.answers.containsKey(getAnswerFormEditText()))
                Toast.makeText(getContext(), "Duplicate answer", Toast.LENGTH_SHORT).show();
            else {
                if (question.answers.containsValue(true) && checkAnswerRadio()) {
                    Toast.makeText(getContext(), "Question already have correct answer", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkNextAnswer()) correctAnswers--;
                    if (correctAnswers != ANSWER_LIMIT - 1) {
                        if (!TextUtils.isEmpty(editTextAnswer.getText().toString()))
                            question.answers.put(getAnswerFormEditText(), checkAnswerRadio());
                        calculateAnswers();
                    } else {
                        Toast.makeText(getContext(), "Please add any correct answer", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(getContext(), "To much answers", Toast.LENGTH_SHORT).show();
        }
        addAnswerBtnHide();
        clearAnswerField();
        Toast.makeText(getContext(), question.answers.toString(), Toast.LENGTH_SHORT).show();

    }

    private boolean checkNextAnswer() {
        checkAnswer();
        return checkAnswerRadio();
    }

    private void addAnswerBtnHide() {
        if (question.answers.size() == ANSWER_LIMIT)
            btnAddAnswer.setEnabled(false);
    }

    private void addAnswerBtnShow() {
        if (question.answers.size() != ANSWER_LIMIT)
            btnAddAnswer.setEnabled(true);
    }


    private void calculateAnswers() {
        if (question.answers.values().contains(false))
            correctAnswers++;
    }

    private String getAnswerFormEditText() {
        checkAnswer();
        checkAnswerRadio();
        return editTextAnswer.getText().toString().trim();
    }

    private void clearAnswerField() {
        editTextAnswer.setText("");
        radioAnswer.setChecked(false);
        radioAnswer.setSelected(false);
    }

    private void submitQuestion() {
        if (question.answers.size() == ANSWER_LIMIT) {
            question.question = editTextQuestion.getText().toString().trim();
            clearQuestionField();
            questions.add(question);
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Not enough answers. Add " + (ANSWER_LIMIT - question.answers.size()) + " more", Toast.LENGTH_SHORT).show();
        }

        question = new Question();
        correctAnswers = 0;
        addAnswerBtnShow();
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

    private Boolean checkAnswerRadio() {
        return radioAnswer.isChecked();
    }


    private void submitQuiz() {
        setEventQuiz();
        if (checkSizeOfQuestions()) {
            Bundle eventBundle = new Bundle();
            eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
            navController.navigate(R.id.createEventHints, eventBundle);
        } else
            Toast.makeText(getContext(), "Please add " + countQuestionToAdd() + " more questions to quiz", Toast.LENGTH_SHORT).show();
    }

    private int countQuestionToAdd() {
        if (event.questionsList != null) {
            if (event.questionsList.size() == 0)
                return QUESTIONS_LIMIT;
            else return (QUESTIONS_LIMIT - event.questionsList.size());
        }
        return QUESTIONS_LIMIT;

    }

    private void setEventQuiz() {
        event.setQuestionsList(questions);
    }

    private boolean checkSizeOfQuestions() {
        if (event.questionsList != null) {
            return event.questionsList.size() == QUESTIONS_LIMIT;
        }
        return false;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            questions.remove(viewHolder.getAdapterPosition());
            mAdapter.notifyDataSetChanged();
        }
    };
}
