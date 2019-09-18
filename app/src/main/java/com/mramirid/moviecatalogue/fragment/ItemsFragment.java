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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.activity.ItemDetailActivity;
import com.mramirid.moviecatalogue.adapter.ItemsAdapter;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.database.GenresDbContract;
import com.mramirid.moviecatalogue.fragment.recycleviewhelper.SpacesItemDecoration;
import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.preference.AppPreference;
import com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel;
import com.mramirid.moviecatalogue.viewmodel.viewmodelfactory.ItemsViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.GENRES_NOT_RECEIVED;
import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.GENRES_RECEIVED;
import static com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel.ITEMS_NOT_RECEIVED;

public class ItemsFragment extends Fragment {

	private String itemType;
	private ItemsAdapter itemsAdapter;
	private ItemsViewModel itemsViewModel;
	private ProgressBar progressBar;
	private TextView tvRequestFailed;

	private AppPreference appPreference;

	public ItemsFragment() {
		// Required empty public constructor
	}

	public ItemsFragment(String itemType) {
		this.itemType = itemType;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appPreference = new AppPreference(Objects.requireNonNull(getContext()));
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_items, container, false);

		itemsAdapter = new ItemsAdapter(getContext());
		itemsAdapter.notifyDataSetChanged();

		RecyclerView recyclerView = view.findViewById(R.id.rv_items);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(itemsAdapter);

		int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.rv_item_margin);
		recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

		progressBar = view.findViewById(R.id.progress_bar);
		tvRequestFailed = view.findViewById(R.id.request_failed);

		itemsViewModel = ViewModelProviders.of(this, new ItemsViewModelFactory(itemType)).get(ItemsViewModel.class);
		itemsViewModel.getItemsLiveData().observe(this, getItemsList);
		itemsViewModel.getItemsRequestStatus().observe(this, getItemsRequestStatus);
		itemsViewModel.getGenresRequestStatus().observe(this, getGenresRequestStatus);

		setItems();

		itemsAdapter.setOnItemClickCallback(new ItemsAdapter.OnItemClickCallback() {
			@Override
			public void onItemClicked(Item item, ItemsAdapter.ItemViewHolder holder, int position) {
				// Set transition
				Pair[] pairs = new Pair[3];
				pairs[0] = new Pair<View, String>(holder.imgPoster, "posterTransition");
				pairs[1] = new Pair<View, String>(holder.tvName, "nameTransition");
				pairs[2] = new Pair<View, String>(holder.ratingBar, "ratingBarTransition");
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);

				Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
				Uri uriItemId = Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + itemsAdapter.getItemsList().get(position).getId());
				intent.setData(uriItemId);
				intent.putExtra(ItemDetailActivity.ITEM_EXTRA, item);
				startActivity(intent, options.toBundle());
			}
		});

		return view;
	}

	private void setItems() {
		Boolean firstRun = appPreference.getAppFirstRun();

		if (firstRun) {
			itemsViewModel.requestGenresFromApi(getContext(), GenresDbContract.CONTENT_URI);
			itemsViewModel.requestItemsFromApi();
		}
		else {
			itemsViewModel.requestItemsFromApi();
		}

		showLoading(true);
	}

	private void showLoading(boolean state) {
		if (state)
			progressBar.setVisibility(View.VISIBLE);
		else
			progressBar.setVisibility(View.GONE);
	}

	private void showRequestFailed(boolean state) {
		if (state) {
			tvRequestFailed.setText(R.string.request_failed);
			tvRequestFailed.setVisibility(View.VISIBLE);
		}
		else {
			tvRequestFailed.setVisibility(View.GONE);
		}
	}

	private Observer<ArrayList<Item>> getItemsList = new Observer<ArrayList<Item>>() {
		@Override
		public void onChanged(ArrayList<Item> items) {
			if (items != null) {
				itemsAdapter.setData(items);
				showRequestFailed(false);
				showLoading(false);
			}
		}
	};

	private Observer<Integer> getItemsRequestStatus = new Observer<Integer>() {
		@Override
		public void onChanged(Integer integer) {
			if (integer == ITEMS_NOT_RECEIVED) {
				Toast.makeText(getActivity(), R.string.request_items_failed, Toast.LENGTH_SHORT).show();
				showRequestFailed(true);
				showLoading(false);
			}
		}
	};

	private Observer<Integer> getGenresRequestStatus = new Observer<Integer>() {
		@Override
		public void onChanged(Integer integer) {
			switch (integer) {
				case GENRES_RECEIVED:
					appPreference.setAppFirstRun(false);
					break;
				case GENRES_NOT_RECEIVED:
					Toast.makeText(getActivity(), R.string.request_genres_failed, Toast.LENGTH_SHORT).show();
					appPreference.setAppFirstRun(true);
					break;
			}
		}
	};
}
