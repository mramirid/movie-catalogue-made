package com.mramirid.moviecatalogue.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.model.Item;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<Item> itemsList = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public ItemsAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<Item> getItemsList() {
        return itemsList;
    }

    public void setData(ArrayList<Item> items) {
        itemsList.clear();
        itemsList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        itemsList.remove(position);
        notifyItemRemoved(position);
        int sizeOfNewItemsList = itemsList.size();
        notifyItemRangeChanged(position, sizeOfNewItemsList);
    }

    public void clearItems() {
        itemsList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        holder.bind(itemsList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClicked(itemsList.get(holder.getAdapterPosition()), holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getItemsList().size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgPoster;
        public TextView tvName;
        public RatingBar ratingBar;
        TextView tvYear;

        private Drawable defaultPoster;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPoster = itemView.findViewById(R.id.img_poster);
            tvName = itemView.findViewById(R.id.tv_name);
            ratingBar = itemView.findViewById(R.id.rb_star);
            tvYear = itemView.findViewById(R.id.tv_year);

            defaultPoster = itemView.getResources().getDrawable(R.drawable.movie);
        }

        void bind(Item item) {
            Glide.with(context)
                    .load(item.getPoster())
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(40)))
                    .error(defaultPoster)
                    .into(imgPoster);
            tvName.setText(item.getName());
            tvYear.setText(item.getYear());
            ratingBar.setRating(item.getRating());
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(Item item, ItemsAdapter.ItemViewHolder holder, int position);
    }
}