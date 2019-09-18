package com.mramirid.moviecatalogue.fragment;


import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.activity.ItemDetailActivity;
import com.mramirid.moviecatalogue.adapter.ItemsAdapter;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.fragment.recycleviewhelper.SpacesItemDecoration;
import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.viewmodel.favoritesviewmodel.FavoritesViewModel;
import com.mramirid.moviecatalogue.viewmodel.viewmodelfactory.FavoritesViewModelFactory;

import java.util.ArrayList;

import static com.mramirid.moviecatalogue.activity.ItemDetailActivity.REMOVE_RESULT_CODE;

public class FavoritesFragment extends Fragment {

	private String itemType;
	private ItemsAdapter itemsAdapter;
	private FavoritesViewModel favoritesViewModel;
	private ProgressBar progressBar;
	private TextView tvDataEmpty;

	private static final int REQUEST_CODE = 100;

	private int selectedItemPosition;

	public FavoritesFragment() {
		// Required empty public constructor
	}

	public FavoritesFragment(String itemType) {
		this.itemType = itemType;
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_items, container, false);

		itemsAdapter = new ItemsAdapter(getContext());
		itemsAdapter.notifyDataSetChanged();

		RecyclerView recyclerView = view.findViewById(R.id.rv_items);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(itemsAdapter);

		int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.rv_item_margin);
		recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

		progressBar = view.findViewById(R.id.progress_bar);
		tvDataEmpty = view.findViewById(R.id.tv_data_empty);

		favoritesViewModel = ViewModelProviders.of(this, new FavoritesViewModelFactory(itemType)).get(FavoritesViewModel.class);
		favoritesViewModel.getFavoritesLiveData().observe(this, getFavorites);

		setItems();

		itemsAdapter.setOnItemClickCallback(new ItemsAdapter.OnItemClickCallback() {
			@Override
			public void onItemClicked(Item item, ItemsAdapter.ItemViewHolder holder, int position) {
				selectedItemPosition = position;

				// Set transition
				Pair[] pairs = new Pair[3];
				pairs[0] = new Pair<View, String>(holder.imgPoster, "posterTransition");
				pairs[1] = new Pair<View, String>(holder.tvName, "nameTransition");
				pairs[2] = new Pair<View, String>(holder.ratingBar, "ratingBarTransition");
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);

				Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
				Uri uriFavoriteId = Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + itemsAdapter.getItemsList().get(position).getId());
				intent.setData(uriFavoriteId);
				intent.putExtra(ItemDetailActivity.ITEM_EXTRA, item);
				startActivityForResult(intent, REQUEST_CODE, options.toBundle());
			}
		});

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == REMOVE_RESULT_CODE) {
				itemsAdapter.removeItem(selectedItemPosition);

				if (itemsAdapter.getItemCount() == 0)
					showEmpty();
			}
		}
	}

	private void setItems() {
		favoritesViewModel.loadFavoritesFromDb(getContext());
		showLoading(true);
	}

	private void showLoading(boolean state) {
		if (state)
			progressBar.setVisibility(View.VISIBLE);
		else
			progressBar.setVisibility(View.GONE);
	}

	private void showEmpty() {
		tvDataEmpty.setText(getString(R.string.empty_favorites, itemType));
		tvDataEmpty.setVisibility(View.VISIBLE);
	}

	private Observer<ArrayList<Item>> getFavorites = new Observer<ArrayList<Item>>() {
		@Override
		public void onChanged(ArrayList<Item> items) {
			if (items != null) {
				itemsAdapter.setData(items);
				showLoading(false);

				if (itemsAdapter.getItemCount() == 0)
					showEmpty();
			}
		}
	};
}
