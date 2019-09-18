package com.mramirid.moviecatalogue.viewmodel.favoritesviewmodel;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.mramirid.moviecatalogue.database.FavoritesDbContract;

import java.lang.ref.WeakReference;

public class LoadFavoritesAsync extends AsyncTask<Void, Void, Cursor> {

	private final String itemType;
	private final WeakReference<Context> weakContext;
	private final WeakReference<LoadFavoritesCallback> weakCallback;

	LoadFavoritesAsync(Context context, LoadFavoritesCallback callback, String itemType) {
		this.weakContext = new WeakReference<>(context);
		this.weakCallback = new WeakReference<>(callback);
		this.itemType = itemType;
	}

	@Override
	protected Cursor doInBackground(Void... voids) {
		Context context = weakContext.get();
		return context.getContentResolver().query(
				FavoritesDbContract.CONTENT_URI,
				null,
				null,
				new String[] {itemType},
				null
		);
	}

	@Override
	protected void onPostExecute(Cursor cursor) {
		super.onPostExecute(cursor);
		weakCallback.get().postExecute(cursor);
	}
}

