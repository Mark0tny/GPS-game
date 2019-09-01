package com.example.kotu9.gpsgame.fragment.eventGame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.Question;
import com.example.kotu9.gpsgame.model.QuizType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventStartQuiz extends Fragment implements View.OnClickListener {

    final static long INTERVAL = 1000;
    final static long TIMEOUT = 1000;
    int progressValue = 0;

    private CountDownTimer countDownTimer;

    int index, thisQuestion, totalQuestion, correctAnswer, score;


    //View
    private ProgressBar progressBar;
    private Button btnA, btnB, btnC, btnD;
    private TextView mQuestionValue, mQuestionNum, mScore;
    private NavController navController;


    //Data
    private ClusterMarker clusterMarker;
    private Event event;
    private QuizType quizType;
    private List<Question> questionList;
    private Map<String, Boolean> answersMap = new HashMap<>();
    private List<TextView> answers = new ArrayList<>();

    public EventStartQuiz() {

    }

    public static EventStartQuiz newInstance() {
        EventStartQuiz fragment = new EventStartQuiz();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.fr_start_quiz);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clusterMarker = new ClusterMarker();
            clusterMarker = (ClusterMarker) getArguments().get(String.valueOf(R.string.markerBundleGame));

//            event = new QuizType();
//            event = clusterMarker.getEvent();
//
//            quizType = new QuizType(event);
//
//            questionList = new ArrayList<>();
//            questionList = quizType.questionsList;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_start_quiz, container, false);


        return view;
    }

    private void setupViews(View view) {
        progressBar = view.findViewById(R.id.progressBarQuiz);

        btnA = view.findViewById(R.id.btnAnswer1);
        btnB = view.findViewById(R.id.btnAnswer2);
        btnC = view.findViewById(R.id.btnAnswer3);
        btnD = view.findViewById(R.id.btnAnswer4);

        mQuestionValue = view.findViewById(R.id.question_text);
        mQuestionNum = view.findViewById(R.id.question_size);
        mScore = view.findViewById(R.id.question_points);


        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        countDownTimer.cancel();
        if (index < questionList.size()) {
            Button clickedBytton = (Button) view;
            if (true) { //warunek gdzy pytanie ok pobranie z mapy on bierze string
                correctAnswer++;
                score += 10;
                showQuestion(++index);
            } else {
                // coś gdzy zle odpowie
                // kolorować buttonki przed zmiana na i go next
            }

            mScore.setText(String.format("%d", score));
        }
    }

    private void showQuestion(int index) {

        if (index < questionList.size()) {

            thisQuestion++;
            mQuestionNum.setText(String.format("%d/%d", thisQuestion, questionList.size()));
            progressBar.setProgress(0);
            progressValue = 0;

            answersMap = questionList.get(index).getAnswers();
            mQuestionValue.setText(questionList.get(index).getQuestion());
            int answersPosition = 0;
            for (Map.Entry<String, Boolean> entry : answersMap.entrySet()) {
                answers.get(answersPosition).setText(entry.getKey());
                answersPosition++;
            }

        }


    }
}
