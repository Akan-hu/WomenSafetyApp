package com.example.kalash.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.example.kalash.R;
import com.example.kalash.databinding.LytNumberBinding;
import com.example.kalash.ui.ContactPickerActivity;
import com.example.kalash.utils.DeleteNumber;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class NumbersListAdapter extends RecyclerView.Adapter<NumbersListAdapter.GetStartedViewHolder> {
    List<String> numbersList = new ArrayList<>();
    DeleteNumber deleteNumber;

    public NumbersListAdapter(List<String> numbersList,DeleteNumber deleteNumber ) {
        this.numbersList = numbersList;
        this.deleteNumber = deleteNumber;
    }


    @NonNull
    @Override
    public GetStartedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        // Inflate the layout

        View photoView
                = inflater
                .inflate(R.layout.lyt_number,
                        parent, false);

        return new GetStartedViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull GetStartedViewHolder holder, int position) {
        holder.materialButton.setText(numbersList.get(position));
        holder.imageViewCompat.setOnClickListener (v ->
        {
            deleteNumber.deleteNumber(numbersList.get(position));
            notifyDataSetChanged();
        });





    }

    @Override
    public int getItemCount() {
        return numbersList.size();
    }

    static class GetStartedViewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageViewCompat;
            MaterialButton materialButton;
            public GetStartedViewHolder(View itemView) {
                super(itemView);
                imageViewCompat = (ImageView) itemView.findViewById(R.id.delete_number);
                materialButton =(MaterialButton) itemView.findViewById(R.id.numberBtn);
            }
        }

}
