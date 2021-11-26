package com.nhom7.den_cafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.OrderState;
import com.nhom7.den_cafe.order.MoreDetailOrderFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListBillAdapter extends RecyclerView.Adapter<ListBillAdapter.ViewHolder> {
    Context context;
    List<Cart> list = new ArrayList<>();
    public ListBillAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ListBillAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_bill, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ListBillAdapter.ViewHolder holder, int position) {
        Cart cart = list.get(position);
        holder.tvName.setText(cart.getProductName());
        holder.tvAmount.setText(cart.getProductAmount()+"");
        holder.tvPrice.setText(cart.getProductPrice()+"");
        holder.tvTotal.setText(cart.getProductPrice()*cart.getProductAmount()+"");
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvPrice, tvTotal;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameItemBill);
            tvAmount = itemView.findViewById(R.id.tvAmountItemBill);
            tvPrice = itemView.findViewById(R.id.tvPriceItemBill);
            tvTotal = itemView.findViewById(R.id.tvTotalItemBill);
        }
    }
}
