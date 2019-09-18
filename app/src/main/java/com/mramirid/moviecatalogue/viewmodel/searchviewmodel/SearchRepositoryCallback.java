package com.mramirid.moviecatalogue.viewmodel.searchviewmodel;

import com.mramirid.moviecatalogue.model.Item;

import java.util.ArrayList;

public interface SearchRepositoryCallback {
	void onPostExecute(ArrayList<Item> items);
}
