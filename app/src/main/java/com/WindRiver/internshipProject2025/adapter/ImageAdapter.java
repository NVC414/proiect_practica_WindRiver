package com.WindRiver.internshipProject2025.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.WindRiver.internshipProject2025.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>
    {

    private int[] images;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, int[] images)
        {
        this.images = images;
        this.inflater = LayoutInflater.from(context);
        }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View view = inflater.inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
        {
        holder.imageView.setImageResource(images[position]);
        }

    @Override
    public int getItemCount()
        {
        return images.length;
        }

    static class ImageViewHolder extends RecyclerView.ViewHolder
        {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView)
            {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
