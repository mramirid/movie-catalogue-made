package com.mramirid.moviecatalogue.viewmodel.favoritesviewmodel;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mramirid.moviecatalogue.helper.MappingHelper;
import com.mramirid.moviecatalogue.model.Item;

import java.util.ArrayList;

public class FavoritesViewModel extends ViewModel implements LoadFavoritesCallback {

	private String itemType;

	private MutableLiveData<ArrayList<Item>> favoritesLiveData = new MutableLiveData<>();

	public FavoritesViewModel(String itemType) {
		this.itemType = itemType;
	}

	public void loadFavoritesFromDb(Context context) {
		new LoadFavoritesAsync(context, this, itemType).execute();
	}

	@Override
	public void postExecute(Cursor cursorFavorites) {
		ArrayList<Item> favoritesList = MappingHelper.mapItemsCursorToArrayList(cursorFavorites);
		favoritesLiveData.postValue(favoritesList);
	}

	public LiveData<ArrayList<Item>> getFavoritesLiveData() {
		return favoritesLiveData;
	}
}
