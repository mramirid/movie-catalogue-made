package com.mramirid.moviecatalogue.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.database.GenresDbContract;
import com.mramirid.moviecatalogue.helper.MappingHelper;
import com.mramirid.moviecatalogue.model.Item;

import java.util.HashMap;
import java.util.Objects;

import static android.provider.BaseColumns._ID;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.DESCRIPTION;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.GENRES;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.ITEM_TYPE;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.LANGUAGE;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.NAME;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.POSTER;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.RATING;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.FavoritesColumns.YEAR;

public class ItemDetailActivity extends AppCompatActivity {

	private ImageView imgCoverDetail, imgPhotoDetail;
	private TextView tvNameDetail, tvGenresDetail, tvYearDetail, tvLang, tvDescriptionDetail;
	private RatingBar ratingBar;
	private ToggleButton favoriteButton;
	private Item item;

	public static final String ITEM_EXTRA = "item_extra";
	public static final int REMOVE_RESULT_CODE = 101;

	private boolean isRemoved = false;
	private Uri uriId;

	public static HashMap<Integer, String> genresList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);

		findViews();

		item = getIntent().getParcelableExtra(ITEM_EXTRA);
		uriId = getIntent().getData();

		if (isInDatabase()) {
			favoriteButton.setChecked(true);
			favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_24dp));
		} else {
			favoriteButton.setChecked(false);
			favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_grey_24dp));
		}

		favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_24dp));
					insertFavorite();
					isRemoved = false;
					Toast.makeText(ItemDetailActivity.this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
				}
				else {
					favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_grey_24dp));
					deleteFavorite();
					isRemoved = true;
					Toast.makeText(ItemDetailActivity.this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
				}
			}
		});

		parseGenres();
		bindViews();
	}

	public boolean isInDatabase() {
		boolean hasInserted = false;
		Cursor cursor = getContentResolver().query(uriId,  null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns._ID));
				int idFromUri = Integer.valueOf(Objects.requireNonNull(uriId.getLastPathSegment()));
				if (id == idFromUri)
					hasInserted = true;
			}
			cursor.close();
		}

		return hasInserted;
	}

	@Override
	public void finish() {
		if (isRemoved) {
			Intent resultIntent = new Intent();
			setResult(REMOVE_RESULT_CODE, resultIntent);
		}
		super.finish();
	}

	public void insertFavorite() {
		ContentValues values = new ContentValues();
		values.put(_ID, item.getId());
		values.put(ITEM_TYPE, item.getItemType());
		values.put(POSTER, item.getPoster());
		values.put(NAME, item.getName());
		values.put(GENRES, item.getGenres());
		values.put(DESCRIPTION, item.getDescription());
		values.put(YEAR, item.getYear());
		values.put(LANGUAGE, item.getLanguage());
		values.put(RATING, item.getRating());
		getContentResolver().insert(FavoritesDbContract.CONTENT_URI, values);
	}

	public void deleteFavorite() {
		getContentResolver().delete(uriId, null, null);
	}

	private void findViews() {
		imgCoverDetail = findViewById(R.id.img_cover_detail);
		imgPhotoDetail = findViewById(R.id.img_photo_detail);
		tvNameDetail = findViewById(R.id.tv_name_detail);
		ratingBar = findViewById(R.id.rb_star);
		tvGenresDetail = findViewById(R.id.tv_genres_detail);
		tvYearDetail = findViewById(R.id.tv_year_detail);
		tvLang = findViewById(R.id.tv_lang_detail);
		tvDescriptionDetail = findViewById(R.id.tv_description_detail);
		favoriteButton = findViewById(R.id.fav_button);
	}

	private void bindViews() {
		Drawable defaultPoster = getResources().getDrawable(R.drawable.movie);

		Glide.with(this)
				.load(this.item.getPoster())
				.centerCrop()
				.into(imgCoverDetail);
		Glide.with(this)
				.load(this.item.getPoster())
				.apply(new RequestOptions().transform(new RoundedCorners(40)))
				.error(defaultPoster)
				.into(imgPhotoDetail);
		tvNameDetail.setText(this.item.getName());
		ratingBar.setRating(this.item.getRating());
		tvGenresDetail.setText(parseGenres());
		tvYearDetail.setText(this.item.getYear());
		tvLang.setText(this.item.getLanguage());
		tvDescriptionDetail.setText(this.item.getDescription());
	}

	public String parseGenres() {
		if (genresList == null) {
			Cursor genresCursor = getContentResolver().query(GenresDbContract.CONTENT_URI, null, null, null, null);
			genresList = MappingHelper.mapGenresCursorToHashMap(Objects.requireNonNull(genresCursor));
		}

		if (item.getGenres() != null) {
			String[] genresId = item.getGenres().split("_");

			StringBuilder stringBuilder = new StringBuilder();
			for (String genreId : genresId) {
				if (genreId.length() != 0) {
					String genreName = genresList.get(Integer.valueOf(genreId));
					stringBuilder.append(genreName).append(", ");
				}
			}

			String genres = stringBuilder.toString();
			if (genres.length() > 2)
				genres = genres.substring(0, genres.length() - 2);	// Hilangkan tanda koma & spasi di akhir String
			return genres;
		} else {
			return null;
		}
	}
}
