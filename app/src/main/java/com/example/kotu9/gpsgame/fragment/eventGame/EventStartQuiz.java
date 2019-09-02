package com.example.kotu9.gpsgame.fragment.eventGame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.Question;
import com.example.kotu9.gpsgame.model.QuizType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventStartQuiz extends Fragment implements View.OnClickListener {


    private static final int DIALOG_DISMISS_TIME = 2000;
    int index = 0, thisQuestion, totalQuestion, correctAnswer;


    //View
    private Button btnA, btnB, btnC, btnD;
    private TextView mQuestionValue, mQuestionNum, mScore;
    private NavController navController;
    private Chronometer eventTimer;


    //Data
    private ClusterMarker clusterMarker;
    public QuizType quizType;
    private List<Question> questionList;
    private List<Button> answers = new ArrayList<>();
    private Map<String, Boolean> answersMap = new HashMap<>();
    private FirebaseFirestore mDb;
    private long timerValue;

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
            quizType = new QuizType((Event) clusterMarker.getEvent());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_start_quiz, container, false);

        setupViews(view);
        mDb = FirebaseFirestore.getInstance();
        setQuestionsList();

        eventTimer.start();

        return view;
    }

    private void setQuestionsList() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setSslEnabled(true)
                .build();

        mDb.setFirestoreSettings(settings);
        DocumentReference newUserRef = mDb
                .collection(getString(R.string.collection_events)).document(quizType.eventType.eventType.toString()).collection(quizType.id).document(quizType.id);
        newUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    quizType = task.getResult().toObject(QuizType.class);
                    questionList = quizType.questionsList;
                    Collections.shuffle(questionList);
                    totalQuestion = questionList.size();
                    showQuestion(index);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Get question list failure:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupViews(View view) {

        btnA = view.findViewById(R.id.btnAnswer1);
        btnB = view.findViewById(R.id.btnAnswer2);
        btnC = view.findViewById(R.id.btnAnswer3);
        btnD = view.findViewById(R.id.btnAnswer4);

        mQuestionValue = view.findViewById(R.id.question_text);
        mQuestionNum = view.findViewById(R.id.question_size);
        mScore = view.findViewById(R.id.question_points);

        eventTimer = view.findViewById(R.id.gameTimerValueQR);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_start_game);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

        answers.add(btnA);
        answers.add(btnB);
        answers.add(btnC);
        answers.add(btnD);
    }

    @Override
    public void onClick(View view) {
        if (index <= questionList.size()) {
            Button clickedBytton = (Button) view;
            if (findCorrectAnswer(clickedBytton.getText().toString().trim())) {
                correctAnswer++;
                showDialog("Correct Answer\nNext Question");
                showQuestion(++index);
            } else {
                showDialog("Incorrect Answer\nNext Question");
                showQuestion(++index);
            }
            mScore.setText(String.format("%d", correctAnswer));
        }else {
            showDialog("Congratulations quiz finished\nYour score\n" + String.format("%d/%d", correctAnswer , totalQuestion));
            endQuiz(endGame());
        }
    }

    private boolean findCorrectAnswer(String answer) {
        if (answersMap != null) {
            if (answersMap.containsKey(answer)) {
                return answersMap.get(answer);
            } else {
                return answersMap.get(answer);
            }
        }
        return false;
    }

    private void showQuestion(int index) {
        if (index < totalQuestion) {
            answersMap = questionList.get(index).getAnswers();
            thisQuestion++;
            mQuestionNum.setText(String.format("%d/%d", thisQuestion, totalQuestion));

            answersMap = questionList.get(index).getAnswers();
            mQuestionValue.setText(questionList.get(index).getQuestion());
            int answersPosition = 0;
            for (Map.Entry<String, Boolean> entry : answersMap.entrySet()) {
                answers.get(answersPosition).setText(entry.getKey());
                answersPosition++;
            }
        } else {
            endQuiz(endGame());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showDialog(String Message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle("Quiz")
                .setCancelable(true)
                .setMessage(Message);


        final AlertDialog alert = dialog.create();
        alert.show();

        new CountDownTimer(DIALOG_DISMISS_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                alert.dismiss();
            }
        }.start();
    }


    private Bundle endGame() {
        timerValue = eventTimer.getBase();
        Bundle bundle = new Bundle();
        bundle.putParcelable(String.valueOf(R.string.markerBundleGame), clusterMarker);
        bundle.putLong(String.valueOf(R.string.timerBundleGame), timerValue);
        bundle.putInt(String.valueOf(R.string.answersBundleGame), correctAnswer);
        return bundle;
    }

    private void endQuiz(Bundle bundle) {
        showDialog("Congratulations quiz finished\nYour score\n" + String.format("%d/%d", correctAnswer , totalQuestion));
        navController.navigate(R.id.eventStartSummary, bundle);
    }


}
