package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizRecyclerViewAdapter extends RecyclerView.Adapter<QuizRecyclerViewAdapter.ViewHolder> {

    private List<Question> questionsList;
    private LayoutInflater mInflater;
    private List<TextView> answers = new ArrayList<>();
    private Map<String, Boolean> answersMap = new HashMap<>();

    public QuizRecyclerViewAdapter(Context context, List<Question> data) {
        this.mInflater = LayoutInflater.from(context);
        this.questionsList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        answers = new ArrayList<>();
        View view = mInflater.inflate(R.layout.quiz_rec_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        answersMap = questionsList.get(position).getAnswers();
        holder.question.setText(questionsList.get(position).getQuestion());
        int answersPosition = 0;
        for (Map.Entry<String, Boolean> entry : answersMap.entrySet()) {
            answers.get(answersPosition).setText(entry.getKey());
            answers.get(answersPosition).setTextColor(Color.parseColor(String.valueOf(R.color.diffRed)));
            if (entry.getValue()) {
                answers.get(answersPosition).setTextColor(Color.parseColor(String.valueOf(R.color.diffGreen)));
            }
            answersPosition++;
        }

    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView question, answer1, answer2, answer3, answer4;

        ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer1 = itemView.findViewById(R.id.answer1);
            answer2 = itemView.findViewById(R.id.answer2);
            answer3 = itemView.findViewById(R.id.answer3);
            answer4 = itemView.findViewById(R.id.answer4);
            answers.add(answer1);
            answers.add(answer2);
            answers.add(answer3);
            answers.add(answer4);
        }
    }

    Question getItem(int id) {
        return questionsList.get(id);
    }

}

