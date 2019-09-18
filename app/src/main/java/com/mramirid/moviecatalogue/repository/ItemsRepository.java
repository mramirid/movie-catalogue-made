package com.mramirid.moviecatalogue.repository;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mramirid.moviecatalogue.BuildConfig;
import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsRepositoryCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.ITEMS_NOT_RECEIVED;
import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.ITEMS_RECEIVED;

public class ItemsRepository {

	public ItemsRepository(ItemsRepositoryCallback callback, final String itemType) {
		final WeakReference<ItemsRepositoryCallback> weakItemsViewModel = new WeakReference<>(callback);
		final ArrayList<Item> itemsList = new ArrayList<>();
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "https://api.themoviedb.org/3/discover/" + itemType + "?api_key=" + BuildConfig.API_KEY + "&language=en-US";

		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					String result = new String(responseBody);
					JSONObject responseObject = new JSONObject(result);
					JSONArray list = responseObject.getJSONArray("results");

					for (int i = 0; i < list.length(); ++i) {
						JSONObject itemData = list.getJSONObject(i);
						Item item = new Item(itemData, itemType);
						itemsList.add(item);
					}

					weakItemsViewModel.get().onItemsReceived(itemsList, ITEMS_RECEIVED);
				} catch (JSONException e) {
					Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
				weakItemsViewModel.get().onItemsReceived(new ArrayList<Item>(), ITEMS_NOT_RECEIVED);
			}
		});
	}
}
