package com.example.kotu9.gpsgame.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizRecyclerViewAdapter extends RecyclerView.Adapter<QuizRecyclerViewAdapter.ViewHolder> {

    private List<Question> questionsList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<TextView> answers = new ArrayList<>();

    public QuizRecyclerViewAdapter(Context context, List<Question> data) {
        this.mInflater = LayoutInflater.from(context);
        this.questionsList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.quiz_rec_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.question.setText(questionsList.get(position).getQuestion());
        for (Map.Entry<String, Boolean> entry : questionsList.get(position).getAnswers().entrySet()) {
            answers.get(position).setText(entry.getKey());
            answers.get(position).setTextColor(Color.RED);
            if (entry.getValue()) {
                answers.get(position).setTextColor(Color.GREEN);
            }
        }


    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    Question getItem(int id) {
        return questionsList.get(id);
    }


    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

