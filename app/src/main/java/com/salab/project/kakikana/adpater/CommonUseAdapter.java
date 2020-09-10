package com.salab.project.kakikana.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.CommonUse;

import java.util.List;

/**
 * Commonly used word list RecyclerView is backed by this Adapter
 */
public class CommonUseAdapter extends RecyclerView.Adapter<CommonUseAdapter.CommonUseViewHolder> {

    private static final String TAG = CommonUseAdapter.class.getSimpleName();

    private List<CommonUse> commonUseList;

    @NonNull
    @Override
    public CommonUseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_common_use, parent, false);
        return new CommonUseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonUseViewHolder holder, int position) {
        CommonUse selectedWord = commonUseList.get(position);
        holder.kanjiTextView.setText(selectedWord.getKanji());
        holder.kanaTextView.setText(selectedWord.getKana());
        holder.meaningTextView.setText(selectedWord.getMeaning());
    }

    @Override
    public int getItemCount() {
        return commonUseList == null ? 0 : commonUseList.size();
    }

    public void setCommonUseList(List<CommonUse> commonUseList) {
        this.commonUseList = commonUseList;
        notifyDataSetChanged();
    }

    public class CommonUseViewHolder extends RecyclerView.ViewHolder {

        TextView kanjiTextView;
        TextView kanaTextView;
        TextView meaningTextView;

        public CommonUseViewHolder(@NonNull View itemView) {
            super(itemView);
            kanjiTextView = itemView.findViewById(R.id.tv_item_kanji);
            kanaTextView = itemView.findViewById(R.id.tv_item_kana);
            meaningTextView = itemView.findViewById(R.id.tv_item_meaning);
        }
    }
}
