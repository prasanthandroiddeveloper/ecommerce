package com.naestech.prasanth.myhita.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.naestech.prasanth.myhita.R;
import com.naestech.prasanth.myhita.helper.Converter;
import com.naestech.prasanth.myhita.helper.Data;
import com.naestech.prasanth.myhita.interfaces.AddorRemoveCallbacks;
import com.naestech.prasanth.myhita.model.Cart;
import com.naestech.prasanth.myhita.model.Product;
import com.naestech.prasanth.myhita.util.localstorage.LocalStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ProductActivity extends BaseActivity {
    private static int cart_count = 0;
    Data data;
    ProductAdapter mAdapter;
    String Tag = "Grid",categorynm,categoryId;
    private RecyclerView recyclerView;
    Button listgrid;
    Bundle bun;
    ImageView imageView;
    String api,testapi;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        changeActionBarTitle(getSupportActionBar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        imageView=findViewById(R.id.wtsappbtn);
        imageView.setOnClickListener(view1 -> {
            String phoneNumberWithCountryCode = "+919393939150";
            String message = "Hi I would like to know About";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message))));
        });

        cart_count = cartCount();
        recyclerView = findViewById(R.id.product_rv);

        data = new Data();

        bun=getIntent().getExtras();
        assert bun != null;
        categorynm=bun.getString("categoryname");
        categoryId=bun.getString("categoryid");
        Log.i("catname",categorynm);
        api="https://myhitha.com/admin_panel/adminpage/items/"+categoryId;
        testapi="https://tripnetra.com/jayakumar/myhitha/adminpage/items/"+categoryId;
        setUpGridFromDb();

    }

    private void changeActionBarTitle(ActionBar actionBar) {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(getApplicationContext());
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setText("Products"); // ActionBar title text
        tv.setTextSize(20);
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);
    }




    private void setUpGridFromDb(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api, response -> {
            Log.i("Leaveresponse",response);

            try {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                JSONArray jarr = new JSONArray(response);

                List<Product> list = new ArrayList<>();

                for(int i = 0; i< jarr.length(); i++) {

                    Product adapter = new Product();
                    JSONObject json = jarr.getJSONObject(i);

                    adapter.setId(json.getString("id"));
                    adapter.setCategoryId(json.getString("categoryId"));
                    adapter.setTitle(json.getString("title"));
                    adapter.setDescription(json.getString("description"));
                    adapter.setAttribute(json.getString("quantity")+" "+json.getString("attribute"));
                    adapter.setPrice(json.getString("price"));
                    adapter.setDiscount(json.getString("discount_value"));
                   // adapter.setCurrency(json.getString("currency"));
                    adapter.setImage(json.getString("image"));

                    list.add(adapter);
                }

                mAdapter = new ProductAdapter(list,ProductActivity.this,Tag);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                recyclerView.setVisibility(View.GONE);

                  Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ProductActivity.this,"No Data",Toast.LENGTH_SHORT).show();
            }

        }, error -> {
            Toast.makeText(ProductActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =new HashMap<>();
               // params.put("categoryid",categoryId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(ProductActivity.this));
        requestQueue.add(stringRequest);

    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.cart_action:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        menuItem.setIcon(Converter.convertLayoutToImage(ProductActivity.this, cart_count, R.drawable.ic_shopping_basket));
        return true;
    }


    @Override
    public void onAddProduct() {
        cart_count++;
        invalidateOptionsMenu();

    }

    @Override
    public void onRemoveProduct() {
        cart_count--;
        invalidateOptionsMenu();

    }


    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

        List<Product> productList;
        Context context;
        String Tag;
        int pQuantity = 1;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String _quantity, _price, _attribute, _subtotal;
        Spinner quantity;
        String[] weights = { "1 kg", "500 gm", "250 gm"};
        private ArrayAdapter arrayAdapter;

        public ProductAdapter(List<Product> productList, Context context) {
            this.productList = productList;
            this.context = context;
        }

        public ProductAdapter(List<Product> productList, Context context, String tag) {
            this.productList = productList;
            this.context = context;
            Tag = tag;
            arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_item,weights);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @NonNull
        @Override
        public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            if (Tag.equalsIgnoreCase("List")) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_products, parent, false);
            } else {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_grid_products, parent, false);
            }


            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductAdapter.MyViewHolder holder, final int position) {

            final Product product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();
            holder.title.setText(product.getTitle());
            holder.offer.setText(String.format("%s %% Off", product.getDiscount()));
            holder.attribute.setText((product.getAttribute()));
            holder.price.setText((product.getPrice()));


            Picasso.get()
                    .load(product.getImage())
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Error : ", e.getMessage());
                        }
                    });


            if (product.getDiscount() == null || product.getDiscount().length() == 0) {
                holder.offer.setVisibility(View.GONE);
            }

            if (!cartList.isEmpty()) {
                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).getId().equalsIgnoreCase(product.getId())) {
                        holder.addToCart.setVisibility(View.GONE);
                        holder.subTotal.setVisibility(View.VISIBLE);
                        holder.quantity.setText(cartList.get(i).getQuantity());
                        _quantity = cartList.get(i).getQuantity();
                        _price = product.getPrice();
                        _subtotal = String.valueOf(Double.parseDouble(_price) * Integer.parseInt(_quantity));
                        holder.subTotal.setText(_quantity + "X" + _price + "= Rs." + _subtotal);
                        Log.d("Tag : ", cartList.get(i).getId() + "-->" + product.getId());
                    }
                }
            } else {

                holder.quantity.setText("1");
            }

            holder.plus.setOnClickListener(v -> {
                pQuantity = Integer.parseInt(holder.quantity.getText().toString());
                if (pQuantity >= 1) {
                    int total_item = Integer.parseInt(holder.quantity.getText().toString());
                    total_item++;
                    holder.quantity.setText(total_item + "");
                    for (int i = 0; i < cartList.size(); i++) {

                        if (cartList.get(i).getId().equalsIgnoreCase(product.getId())) {

                            // Log.d("totalItem", total_item + "");

                            _subtotal = String.valueOf(Double.parseDouble(holder.price.getText().toString()) * total_item);
                            cartList.get(i).setQuantity(holder.quantity.getText().toString());
                            cartList.get(i).setSubTotal(_subtotal);
                            holder.subTotal.setText(total_item + "X" + holder.price.getText().toString() + "= Rs." + _subtotal);
                            String cartStr = gson.toJson(cartList);
                            //Log.d("CART", cartStr);
                            localStorage.setCart(cartStr);
                            notifyItemChanged(position);
                        }
                    }
                }

            });
            holder.minus.setOnClickListener(v -> {
                pQuantity = Integer.parseInt(holder.quantity.getText().toString());
                if (pQuantity != 1) {
                    int total_item = Integer.parseInt(holder.quantity.getText().toString());
                    total_item--;
                    holder.quantity.setText(total_item + "");
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getId().equalsIgnoreCase(product.getId())) {

                            //holder.quantity.setText(total_item + "");
                            //Log.d("totalItem", total_item + "");
                            _subtotal = String.valueOf(Double.parseDouble(holder.price.getText().toString()) * total_item);
                            cartList.get(i).setQuantity(holder.quantity.getText().toString());
                            cartList.get(i).setSubTotal(_subtotal);
                            holder.subTotal.setText(total_item + "X" + holder.price.getText().toString() + "= Rs." + _subtotal);
                            String cartStr = gson.toJson(cartList);
                            //Log.d("CART", cartStr);
                            localStorage.setCart(cartStr);
                            notifyItemChanged(position);
                        }
                    }

                }

            });



            holder.addToCart.setOnClickListener(v -> {

                holder.addToCart.setVisibility(View.GONE);
                holder.subTotal.setVisibility(View.VISIBLE);


                _price = product.getPrice();
                _quantity = holder.quantity.getText().toString();
                _attribute = product.getAttribute();

                if (Integer.parseInt(_quantity) != 0) {
                    _subtotal = String.valueOf(Double.parseDouble(_price) * Integer.parseInt(_quantity));
                    holder.subTotal.setText(_quantity + "X" + _price + "= Rs." + _subtotal+"\n"+"Added to Cart");
                    if (context instanceof ProductActivity) {
                        Cart cart = new Cart(product.getId(), product.getTitle(), product.getImage(), product.getCurrency(), _price, _attribute, _quantity, _subtotal);
                        cartList = ((BaseActivity) context).getCartList();
                        cartList.add(cart);
                        String cartStr = gson.toJson(cartList);
                        //Log.d("CART", cartStr);
                        localStorage.setCart(cartStr);
                        ((AddorRemoveCallbacks) context).onAddProduct();
                        notifyItemChanged(position);
                    }
                } else {
                    Toast.makeText(context, "Please Add Quantity", Toast.LENGTH_SHORT).show();
                }


            });
            holder.spin.setAdapter(arrayAdapter);

        }

        @Override
        public int getItemCount() {

            return productList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView title;
            ProgressBar progressBar;
            CardView cardView;
            TextView offer, currency, price, quantity, attribute, addToCart, subTotal;
            Button plus, minus;
            Spinner spin;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.product_image);
                title = itemView.findViewById(R.id.product_title);
                progressBar = itemView.findViewById(R.id.progressbar);
                cardView = itemView.findViewById(R.id.card_view);
                offer = itemView.findViewById(R.id.product_discount);
                currency = itemView.findViewById(R.id.product_currency);
                price = itemView.findViewById(R.id.product_price);
                quantity = itemView.findViewById(R.id.quantity);
                addToCart = itemView.findViewById(R.id.add_to_cart);
                attribute = itemView.findViewById(R.id.product_attribute);
                plus = itemView.findViewById(R.id.quantity_plus);
                minus = itemView.findViewById(R.id.quantity_minus);
                subTotal = itemView.findViewById(R.id.sub_total);
                spin = itemView.findViewById(R.id.spinner);


                spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String item = adapterView.getItemAtPosition(i).toString();
                        // Showing selected spinner item
                        Toast.makeText(context, "Selected: " + item, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                itemView.setOnClickListener(view -> {
                    String id = productList.get(getAdapterPosition()).getId();
                    Intent i = new Intent(context,Selectweights.class);

                    Bundle b = new Bundle();
                    b.putString("id",id);
                    i.putExtras(b);
                    startActivity(i);
                });
            }
        }
    }

}
