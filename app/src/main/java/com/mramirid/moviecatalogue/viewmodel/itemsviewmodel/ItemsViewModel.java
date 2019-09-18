package com.mramirid.moviecatalogue.viewmodel.itemsviewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.repository.GenresRepository;
import com.mramirid.moviecatalogue.repository.ItemsRepository;

import java.util.ArrayList;

public class ItemsViewModel extends ViewModel implements ItemsRepositoryCallback {

	private String itemType;

	private MutableLiveData<ArrayList<Item>> itemsLiveData = new MutableLiveData<>();
	private MutableLiveData<Integer> itemsReceivedLiveData = new MutableLiveData<>();
	private MutableLiveData<Integer> genresReceivedLiveData = new MutableLiveData<>();

	public static final int ITEMS_RECEIVED = 1;
	public static final int ITEMS_NOT_RECEIVED = -1;
	public static final int GENRES_RECEIVED = 2;
	public static final int GENRES_NOT_RECEIVED = -1;

	public ItemsViewModel(String itemType) {
		this.itemType = itemType;
	}

	public void requestGenresFromApi(Context context, Uri uriGenre) {
		new GenresRepository(context, this, uriGenre, itemType);
	}

	public void requestItemsFromApi() {
		new ItemsRepository(this, itemType);
	}

	@Override
	public void onGenresReceived(int genresReceivedStatus) {
		genresReceivedLiveData.postValue(genresReceivedStatus);
	}

	@Override
	public void onItemsReceived(ArrayList<Item> items, int itemsReceivedStatus) {
		switch (itemsReceivedStatus) {
			case ITEMS_RECEIVED:
				itemsLiveData.postValue(items);
				itemsReceivedLiveData.postValue(ITEMS_RECEIVED);
				break;
			case ITEMS_NOT_RECEIVED:
				itemsReceivedLiveData.postValue(ITEMS_NOT_RECEIVED);
				break;
		}
	}

	public LiveData<ArrayList<Item>> getItemsLiveData() {
		return itemsLiveData;
	}

	public LiveData<Integer> getGenresRequestStatus() {
		return genresReceivedLiveData;
	}

	public LiveData<Integer> getItemsRequestStatus() {
		return itemsReceivedLiveData;
	}
}

