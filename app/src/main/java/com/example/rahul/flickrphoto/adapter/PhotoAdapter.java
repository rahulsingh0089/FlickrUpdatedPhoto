package com.example.rahul.flickrphoto.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rahul.flickrphoto.R;
import com.example.rahul.flickrphoto.listener.OnLoadMoreListener;
import com.example.rahul.flickrphoto.model.PhotoDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahulsingh on 1/23/2018.
 */

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private List<PhotoDetail> photoList;
    private List<PhotoDetail> photoListFiltered;
    private PhotoAdapterListener listener;

    //onScroll
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;



    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public ImageView imageView;


        public MyViewHolder(View v) {
            super(v);
            text =  v.findViewById(R.id.tvTitle);
            imageView =  v.findViewById(R.id.icon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPhotoSelected(photoListFiltered.get(getAdapterPosition()));
                }
            });

        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public PhotoAdapter(Context context,List<PhotoDetail> photoList, PhotoAdapterListener listener,RecyclerView recyclerView) {
        this.photoList = photoList;
        this.context=context;
        this.listener=listener;
        this.photoListFiltered=photoList;
        Log.d("PhotoAdapter","photoList Size"+photoList.size());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return photoList.get(position)==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_ITEM)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.row_item_layout,parent,false);
            return new MyViewHolder(view);
        }else if(viewType==VIEW_TYPE_LOADING){
            View view=LayoutInflater.from(context).inflate(R.layout.photo_loading,parent,false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder=(MyViewHolder)holder;
            PhotoDetail photoDetail = photoListFiltered.get(position);
            String image_url = "https://farm" + photoDetail.getForm_id() + ".staticflickr.com/" + photoDetail.getServer_id() + "/" + photoDetail.getId() + "_" + photoDetail.getSecret() + "_n.jpg";
            myViewHolder.text.setText(photoDetail.getTite());
            Picasso.with(context)
                    .load(image_url)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(android.R.drawable.sym_def_app_icon)
                    .into(myViewHolder.imageView);

        }else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return photoListFiltered.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    photoListFiltered = photoList;
                } else {
                    List<PhotoDetail> filteredList = new ArrayList<>();
                    for (PhotoDetail row : photoList) {

                        // here we are looking for title match
                        if (row.getTite().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    photoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = photoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                photoListFiltered = (ArrayList<PhotoDetail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface PhotoAdapterListener {
        void onPhotoSelected(PhotoDetail photoDetail);
    }

}
