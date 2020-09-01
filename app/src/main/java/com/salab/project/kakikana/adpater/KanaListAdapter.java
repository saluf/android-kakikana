package com.salab.project.kakikana.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.Kana;

import java.util.List;

import static com.salab.project.kakikana.util.KanaUtil.NUM_OF_HIRAGANA;
import static com.salab.project.kakikana.util.KanaUtil.NUM_OF_KATAKANA;

/**
 * Kana list RecyclerView is backed by this Adapter
 */
public class KanaListAdapter extends RecyclerView.Adapter<KanaListAdapter.KanaViewHolder> {

    private static final String TAG = KanaListAdapter.class.getSimpleName();

    private List<Kana> mKanaList;
    private onKanaItemClickListener mListener;
    // mKanaList contains all kana including both hiragana and katakana, so only partial list will
    // be shown. Since the list is sorted, so instead of slice, indexing (offset) will be more efficient.
    private boolean isHiragana = true;
    private int indexOffset = 0;

    public interface onKanaItemClickListener {
        void onKanaItemClick(int kanaId);
    }

    public KanaListAdapter(List<Kana> mKanaList, onKanaItemClickListener mListener) {
        this.mKanaList = mKanaList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public KanaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_kana, parent, false);
        return new KanaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KanaViewHolder holder, int position) {
        Kana selectedKana = mKanaList.get(position + indexOffset);
        holder.mKanaTextView.setText(selectedKana.getKana());
        holder.mRomajiTextView.setText(selectedKana.getRomaji());
    }

    @Override
    public int getItemCount() {
        // max range depends on how many characters we have
        return isHiragana ? NUM_OF_HIRAGANA : NUM_OF_KATAKANA;
    }

    public void setmKanaList(List<Kana> mKanaList) {
        this.mKanaList = mKanaList;
        notifyDataSetChanged();
    }

    public void setIsHiragana(boolean isHiragana) {
        this.isHiragana = isHiragana;

        // hiragana : 0 ~ NUM_OF_HIRAGANA - 1,
        // katakana : NUM_OF_HIRAGANA ~ NUM_OF_HIRAGANA + NUM_OF_KATAKANA - 1 == NUM_OF_HIRAGANA + (0 ~ NUM_OF_KATAKANA)
        if (isHiragana) {
            indexOffset = 0;
        } else {
            indexOffset = NUM_OF_HIRAGANA;
        }
        notifyDataSetChanged();
    }

    class KanaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mKanaTextView;
        TextView mRomajiTextView;

        public KanaViewHolder(@NonNull View itemView) {
            super(itemView);
            mKanaTextView = itemView.findViewById(R.id.tv_kana);
            mRomajiTextView = itemView.findViewById(R.id.tv_romaji);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Kana selectedKana = mKanaList.get(getAdapterPosition() + indexOffset);
            if (!selectedKana.getRomaji().isEmpty()) {
                // skip empty kana item (empty place holder for standard arrangement)
                mListener.onKanaItemClick(selectedKana.getId());
            }
        }
    }
}
