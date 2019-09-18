package com.mramirid.moviecatalogue.viewmodel.searchviewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.repository.SearchRepository;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel implements SearchRepositoryCallback {

	private MutableLiveData<ArrayList<Item>> itemsLiveData = new MutableLiveData<>();
	private String itemType;

	public SearchViewModel(String itemType) {
		this.itemType = itemType;
	}

	public void search(String query) {
		new SearchRepository(this, itemType, query);
	}

	@Override
	public void onPostExecute(ArrayList<Item> items) {
		itemsLiveData.postValue(items);
	}

	public LiveData<ArrayList<Item>> getItemsLiveData() {
		return itemsLiveData;
	}
}

