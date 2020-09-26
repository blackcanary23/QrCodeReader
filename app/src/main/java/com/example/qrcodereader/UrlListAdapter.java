package com.example.qrcodereader;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class UrlListAdapter extends RecyclerView.Adapter<UrlListAdapter.ViewHolder> {

    private ArrayList<String> urlList;

    public UrlListAdapter(ArrayList<String> urlList) {

        this.urlList = urlList;
    }

    @NonNull
    @Override
    public UrlListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.url_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UrlListAdapter.ViewHolder holder, int position) {

        holder.url.setText(urlList.get(position));
    }

    @Override
    public int getItemCount() {

        return urlList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView url;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            url = itemView.findViewById(R.id.url);
        }
    }
}
