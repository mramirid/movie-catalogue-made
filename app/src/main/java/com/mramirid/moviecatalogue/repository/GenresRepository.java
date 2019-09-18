package com.mramirid.moviecatalogue.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mramirid.moviecatalogue.BuildConfig;
import com.mramirid.moviecatalogue.database.GenresDbContract;
import com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsRepositoryCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.GENRES_NOT_RECEIVED;
import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.GENRES_RECEIVED;

public class GenresRepository {

	public GenresRepository(final Context context, ItemsRepositoryCallback callback, final Uri uriGenre, String itemType) {
		final WeakReference<ItemsRepositoryCallback> weakItemsViewModel = new WeakReference<>(callback);
		String url = "https://api.themoviedb.org/3/genre/" + itemType + "/list?api_key=" + BuildConfig.API_KEY + "&language=en-US";

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					String result = new String(responseBody);
					JSONObject responseObject = new JSONObject(result);
					JSONArray list = responseObject.getJSONArray("genres");

					for (int i = 0; i < list.length(); ++i) {
						JSONObject genreObject = list.getJSONObject(i);
						int id = genreObject.getInt("id");
						String name = genreObject.getString("name");
						ContentValues contentValues = new ContentValues();
						contentValues.put(GenresDbContract.GenresColumns._ID, id);
						contentValues.put(GenresDbContract.GenresColumns.NAME, name);
						if (!isGenreInDatabase(context, id))
							context.getContentResolver().insert(uriGenre, contentValues);
					}

					weakItemsViewModel.get().onGenresReceived(GENRES_RECEIVED);
				} catch (JSONException e) {
					Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
				weakItemsViewModel.get().onGenresReceived(GENRES_NOT_RECEIVED);
			}
		});
	}

	private boolean isGenreInDatabase(Context context, int id) {
		boolean hasInserted = false;
		Cursor genreCursor = context.getContentResolver().query(
				GenresDbContract.CONTENT_URI, null, null, new String[] {String.valueOf(id)}, null
		);

		if (genreCursor != null) {
			if (genreCursor.moveToFirst()) {
				int idFromDb = genreCursor.getInt(genreCursor.getColumnIndexOrThrow(GenresDbContract.GenresColumns._ID));
				if (idFromDb == id)
					hasInserted = true;
			}
			genreCursor.close();
		}

		return hasInserted;
	}
}
