package com.mramirid.moviecatalogue.repository;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mramirid.moviecatalogue.BuildConfig;
import com.mramirid.moviecatalogue.alarm.AlarmNotification;
import com.mramirid.moviecatalogue.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class ReleaseTodayRepository {

	public ReleaseTodayRepository(final Context appContext) {
		Log.d(this.getClass().getSimpleName(), "ReleaseTodayRepository: EXEC");

		final ArrayList<Item> itemsList = new ArrayList<>();
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + BuildConfig.API_KEY + "&primary_release_date.gte=" + getCurrentDate() + "&primary_release_date.lte=" + getCurrentDate();

		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					String result = new String(responseBody);
					JSONObject responseObject = new JSONObject(result);
					JSONArray list = responseObject.getJSONArray("results");

					for (int i = 0; i < list.length(); ++i) {
						JSONObject itemData = list.getJSONObject(i);
						Item item = new Item(itemData, "movie");
						itemsList.add(item);
					}

					AlarmNotification.onReceiveReleaseToday(appContext, itemsList);
				} catch (JSONException e) {
					Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
			}
		});
	}

	private String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
