package com.mramirid.moviecatalogue.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.helper.MappingHelper;
import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.model.WidgetItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	private final ArrayList<WidgetItem> widgetItems = new ArrayList<>();
	private final Context context;

	StackRemoteViewsFactory(Context context) {
		this.context = context;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDataSetChanged() {
		widgetItems.clear();

		final long identityToken = Binder.clearCallingIdentity();
		Cursor favoritesCursor = context.getContentResolver().query(FavoritesDbContract.CONTENT_URI, null, null, null, null);
		Binder.restoreCallingIdentity(identityToken);

		ArrayList<Item> itemsList = MappingHelper.mapItemsCursorToArrayList(favoritesCursor);

		for (Item item : itemsList) {
			String name = item.getName();
			Bitmap image = null;
			try {
				image = Glide.with(context)
						.asBitmap()
						.load(item.getPoster())
						.submit()
						.get();
			} catch (ExecutionException | InterruptedException e) {
				Log.d(this.getClass().getSimpleName(), "onDataSetChanged: " + e.getMessage());
			}

			if (image == null)
				image = BitmapFactory.decodeResource(context.getResources(), R.drawable.movie);

			widgetItems.add(new WidgetItem(name, image));
		}
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		return widgetItems.size();
	}

	@Override
	public RemoteViews getViewAt(int i) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
		remoteViews.setImageViewBitmap(R.id.img_widget_poster, widgetItems.get(i).getImgPoster());
		remoteViews.setTextViewText(R.id.tv_name, widgetItems.get(i).getName());

		Bundle extras = new Bundle();
		extras.putString(FavoritesWidget.EXTRA_NAME, widgetItems.get(i).getName());
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);

		remoteViews.setOnClickFillInIntent(R.id.img_widget_poster, fillInIntent);
		return remoteViews;
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
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
}
