package com.salab.project.kakikana.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.repository.Repository;

import java.util.List;

public class FlashCardRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FlashCardRemoteViewsFactory(getApplicationContext());
    }
}

/**
 * A remote adapter to support flash card StackVIew
 */

class FlashCardRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    // constants
    private static final String TAG = FlashCardRemoteViewsFactory.class.getSimpleName();
    public static final int NUM_CARDS = 20;

    // global variables
    private Context context;
    private List<Kana> KanaList;
    private Repository repository;


    public FlashCardRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
        repository = new Repository(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // either instance creation or AppWidgetManager notify data changes will trigger this callback
        KanaList = repository.getRandomFlashCards(NUM_CARDS);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return KanaList == null ? 0 : KanaList.size() + 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews itemView = new RemoteViews(context.getPackageName(), R.layout.item_flash_card);

        if (position == KanaList.size()){
            // additional last card
            itemView.setTextViewText(R.id.tv_flash_last_card_word,
                    context.getString(R.string.text_widget_last_flash_card));

            // display only this TextView
            itemView.setViewVisibility(R.id.tv_flash_last_card_word, View.VISIBLE);
            itemView.setViewVisibility(R.id.tv_flash_card_kana, View.GONE);
            itemView.setViewVisibility(R.id.tv_flash_card_kana_desc, View.GONE);

        } else {
            // populate normal cards
            Kana selectedKana = KanaList.get(position);

            itemView.setTextViewText(R.id.tv_flash_card_kana, selectedKana.getKana());
            String kanaDesc = context.getString(
                    R.string.format_widget_flash_card_desc, selectedKana.getRomaji(), selectedKana.getType());
            itemView.setTextViewText(R.id.tv_flash_card_kana_desc, kanaDesc);
        }
        return itemView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
