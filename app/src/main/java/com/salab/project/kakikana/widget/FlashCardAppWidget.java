package com.salab.project.kakikana.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.salab.project.kakikana.R;

/**
 * Widget provider for flash card widget
 */
public class FlashCardAppWidget extends AppWidgetProvider {

    // constants
    private static final String TAG = FlashCardAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.flash_card_app_widget);

        // setup flash card stack view
        Intent flashCardServiceIntent = new Intent(context, FlashCardRemoteViewsService.class);
        views.setRemoteAdapter(R.id.stack_flash_card, flashCardServiceIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        Log.d(TAG, "Widget " + appWidgetId + " is updated");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

