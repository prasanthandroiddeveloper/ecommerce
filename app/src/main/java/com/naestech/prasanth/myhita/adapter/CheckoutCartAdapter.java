package com.naestech.prasanth.myhita.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.naestech.prasanth.myhita.R;
import com.naestech.prasanth.myhita.model.Cart;
import com.naestech.prasanth.myhita.util.localstorage.LocalStorage;

import java.util.List;

public class CheckoutCartAdapter extends RecyclerView.Adapter<CheckoutCartAdapter.MyViewHolder> {

    List<Cart> cartList;
    Context context;

    String _subtotal, _price, _quantity;
    LocalStorage localStorage;
    Gson gson;


    public CheckoutCartAdapter(List<Cart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart1, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Cart cart = cartList.get(position);
        localStorage = new LocalStorage(context);
        gson = new Gson();
        holder.title.setText(cart.getTitle());


        _price = cart.getPrice();
        _quantity = cart.getQuantity();
        holder.quantity.setText(_quantity);
        holder.price.setText(_price);
        _subtotal = String.valueOf(Double.parseDouble(_price) * Integer.parseInt(_quantity));
        holder.subTotal.setText(_subtotal);
    }

    @Override
    public int getItemCount() {

        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView price, quantity, subTotal;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            title = itemView.findViewById(R.id.product_title);
            quantity = itemView.findViewById(R.id.quantity);
            subTotal = itemView.findViewById(R.id.sub_total);
            price = itemView.findViewById(R.id.product_price);

        }
    }
}
