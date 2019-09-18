package com.mramirid.moviecatalogue.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mramirid.moviecatalogue.R;

public class FavoritesWidget extends AppWidgetProvider {

	private static final String TOAST_ACTION = "TOAST_ACTION";
	public static final String EXTRA_NAME = "EXTRA_NAME";

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		Intent intent = new Intent(context, StackWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorites_widget);
		views.setRemoteAdapter(R.id.stack_view, intent);
		views.setEmptyView(R.id.stack_view, R.id.empty_favorites);

		Intent toastIntent = new Intent(context, FavoritesWidget.class);
		toastIntent.setAction(FavoritesWidget.TOAST_ACTION);
		toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		Log.d(this.getClass().getSimpleName(), "onUpdate: EXEC");
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction() != null) {
			if (intent.getAction().equals(TOAST_ACTION)) {
				String itemName = intent.getStringExtra(EXTRA_NAME);
				Toast.makeText(context, itemName, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static void notifyWidgetDataChanged(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), FavoritesWidget.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view);
	}
}

