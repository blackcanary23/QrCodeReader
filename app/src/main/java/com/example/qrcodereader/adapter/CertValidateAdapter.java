package com.example.qrcodereader.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qrcodereader.R;
import com.example.qrcodereader.model.CertChainRepository;
import java.util.ArrayList;


public class CertValidateAdapter extends RecyclerView.Adapter<CertValidateAdapter.ViewHolder> {

    private ArrayList<CertChainRepository> chainList;

    public CertValidateAdapter(ArrayList<CertChainRepository> chainList) {

        this.chainList = chainList;
    }

    @NonNull
    @Override
    public CertValidateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cert_validate_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CertValidateAdapter.ViewHolder holder, int position) {

        holder.cert.setText(chainList.get(position).getCertChains().toString());
    }

    @Override
    public int getItemCount() {

        return chainList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView cert;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            cert = itemView.findViewById(R.id.cert);
        }
    }
}
