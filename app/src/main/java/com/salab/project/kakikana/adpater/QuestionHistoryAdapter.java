package com.salab.project.kakikana.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.QuestionResult;

import java.util.List;

public class QuestionHistoryAdapter extends RecyclerView.Adapter<QuestionHistoryAdapter.QuestionHistoryViewHolder> {

    private List<QuestionResult> mQuestionResultList;
    private Context mContext;

    public QuestionHistoryAdapter(List<QuestionResult> mQuestionResultList, Context mContext) {
        this.mQuestionResultList = mQuestionResultList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public QuestionHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_quiz_history, parent, false);
        return new QuestionHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHistoryViewHolder holder, int position) {
        QuestionResult selectedResult = mQuestionResultList.get(position);

        if (selectedResult.isIssued()) {
            // issued recognition result
            holder.resultImageView.setImageResource(R.drawable.ic_flag);
        } else {
            // O or X image depends on correct or not
            holder.resultImageView.setImageResource(selectedResult.isCorrect() ?
                    R.drawable.ic_correct : R.drawable.ic_incorrect);
        }

        // get the same question as seen in quiz
        holder.questionTextView.setText(mContext.getString(
                R.string.format_quiz_question,
                selectedResult.getQuestionKanaRomaji(),
                selectedResult.getQuestionKanaType()));

        holder.correctTextView.setText(selectedResult.getQuestionKana());
        // load user drawn image
        holder.userImageView.setImageBitmap(selectedResult.getDrawnAnswer());
    }

    @Override
    public int getItemCount() {
        return mQuestionResultList == null ? 0 : mQuestionResultList.size();
    }

    class QuestionHistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView resultImageView;
        TextView questionTextView;
        TextView correctTextView;
        ImageView userImageView;

        public QuestionHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            resultImageView = itemView.findViewById(R.id.iv_item_result);
            questionTextView = itemView.findViewById(R.id.tv_item_question);
            correctTextView = itemView.findViewById(R.id.iv_item_correct);
            userImageView = itemView.findViewById(R.id.iv_item_user);

        }
    }
}
