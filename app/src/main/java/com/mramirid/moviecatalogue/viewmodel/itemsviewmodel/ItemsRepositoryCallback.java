package com.mramirid.moviecatalogue.viewmodel.itemsviewmodel;

import com.mramirid.moviecatalogue.model.Item;

import java.util.ArrayList;

public interface ItemsRepositoryCallback {
	void onGenresReceived(int genresReceivedStatus);
	void onItemsReceived(ArrayList<Item> items, int itemsReceivedStatus);
}
