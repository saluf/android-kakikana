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

/**
 * Kana list RecyclerView is backed by this Adapter
 */
public class KanaListAdapter extends RecyclerView.Adapter<KanaListAdapter.KanaViewHolder> {

    private static final String TAG = KanaListAdapter.class.getSimpleName();

    private List<Kana> mKanaList;
    private onKanaItemClickListener mListener;

    public interface onKanaItemClickListener{
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
        Kana selectedKana = mKanaList.get(position);
        holder.mKanaTextView.setText(selectedKana.getKana());
        holder.mRomajiTextView.setText(selectedKana.getRomaji());
    }

    @Override
    public int getItemCount() {
        return mKanaList == null ? 0 : mKanaList.size();
    }

    public void setmKanaList(List<Kana> mKanaList) {
        this.mKanaList = mKanaList;
        notifyDataSetChanged();
    }

    class KanaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            Kana selectedKana = mKanaList.get(getAdapterPosition());
            if (!selectedKana.getRomaji().isEmpty()){
                // skip empty kana item (empty place holder for standard arrangement)
                mListener.onKanaItemClick(selectedKana.getId());
            }
        }
    }
}
