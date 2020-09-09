package com.salab.project.kakikana.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.User;

import java.util.List;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder> {

    private static final String TAG = ScoreboardAdapter.class.getSimpleName();

    private List<User> mScoreboardUserList;
    private int mSize;
    private Context mContext;

    public ScoreboardAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ScoreboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_scoreboard, parent, false);

        return new ScoreboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreboardViewHolder holder, int position) {
        User selectedUser = mScoreboardUserList.get(reversedPosition(position));
        holder.rankTextView.setText(String.valueOf(reversedPosition(position) + 1));
        holder.nameTextView.setText(selectedUser.getName());
        holder.numCorrectTextView.setText(String.valueOf(selectedUser.getTotalCorrect()));

        float corrRate = 0f;
        if (selectedUser.getTotalTested() != 0){
            corrRate = Math.round((float) selectedUser.getTotalCorrect() / selectedUser.getTotalTested() * 100);
        }

        holder.corrRateTextView.setText(mContext.getResources().getString(R.string.format_corr_rate_in_percent, corrRate));
    }

    @Override
    public int getItemCount() {
        mSize = mScoreboardUserList == null ? 0 : mScoreboardUserList.size();
        return mSize;
    }

    public void setmScoreboardUserList(List<User> mScoreboardUserList) {
        this.mScoreboardUserList = mScoreboardUserList;
        notifyDataSetChanged();
    }

    private int reversedPosition(int position){
        // Firebase can only have list in ascending order
        // so it is needed to reverse it on client side.
        return mSize - position - 1;
    }

    class ScoreboardViewHolder extends RecyclerView.ViewHolder {

        TextView rankTextView;
        TextView nameTextView;
        TextView numCorrectTextView;
        TextView corrRateTextView;

        public ScoreboardViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.tv_item_rank);
            nameTextView = itemView.findViewById(R.id.tv_item_name);
            numCorrectTextView = itemView.findViewById(R.id.tv_item_num_correct);
            corrRateTextView = itemView.findViewById(R.id.tv_item_correctness_rate);
        }
    }
}
