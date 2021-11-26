package com.nhom7.den_cafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.RateUnit;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context context;
    List<RateUnit> list = new ArrayList<>();
    public ReviewAdapter(Context context, List<RateUnit> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_unit, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ReviewAdapter.ViewHolder holder, int position) {
        RateUnit rateUnit = list.get(position);
        holder.tvName.setText(rateUnit.getUserName());
        holder.tvDate.setText(rateUnit.getDateRating());
        holder.tvComment.setText(rateUnit.getComment());
        holder.bar.setRating(rateUnit.getValueRating());
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvComment;
        RatingBar bar;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserNameItemReview);
            tvDate = itemView.findViewById(R.id.tvDateItemReview);
            tvComment = itemView.findViewById(R.id.tvCommentItemReview);
            bar = itemView.findViewById(R.id.rateItemReview);
        }
    }
}
