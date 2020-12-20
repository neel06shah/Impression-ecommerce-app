package com.example.impressionselling.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.impressionselling.R;
import com.example.impressionselling.cartProduct;
import com.example.impressionselling.products;
import com.example.impressionselling.ui.productDetails.ProductDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.myViewHolder> {

    private String title;
    private String desc;
    private String Mrp;
    private String Rate;
    private String Discount;
    private String Image;
    private String quan;
    private String sch;
    private ArrayList<products> list;
    private DatabaseReference firebaseDatabase;
    private String current;


    public AdapterClass(ArrayList<products> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new myViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        title = list.get(position).getName();
        desc = list.get(position).getDescription();
        Image = list.get(position).getImage();
        Mrp = list.get(position).getMrp();
        Rate = list.get(position).getRate();
        Discount = list.get(position).getDiscount();
        quan = list.get(position).getQuantity();
        sch = list.get(position).getScheme();
        Boolean bulk = list.get(position).getBulk();

        if(bulk == null) {
            holder.bulk_order.setVisibility(View.GONE);
        }
        else {
            if (bulk) {
                holder.bulk_order.setVisibility(View.VISIBLE);
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

        double d = Double.parseDouble(Discount);
        float m = Float.parseFloat(Mrp);
        float r = Float.parseFloat(Rate);

        Glide.with(holder.itemView.getContext()).load(Image).into(holder.imageView);
        holder.tvPrintTitle.setText(title);
        holder.tvSd.setText("Pack of "+quan+" nos");
        holder.tvR.setText("\u20b9 "+  String.format("%.2f",r));
        holder.tvM.setText("\u20B9 " +  String.format("%.2f",m));

        if(list.get(position).getScheme().equals("")){
            holder.scheme.setVisibility(View.GONE);
            holder.dis.setText(Discount+"% margin");
            holder.tvD.setText("Retail Margin : " + String.format("%.2f",d) + "%");
        }
        else{
            holder.scheme.setVisibility(View.VISIBLE);
            holder.scheme.setText("Scheme : " + list.get(position).getScheme()+"%");
            holder.dis.setText(Discount+"% margin\n+ Scheme");

            float m1 = Float.parseFloat(Mrp);
            float r1 = Float.parseFloat(Rate);
            float s1 = Float.parseFloat(list.get(position).getScheme());

            float nm = r - ((s1*r1)/100);
            double dis = 100-((nm*100)/m1);
            holder.tvD.setText("Retail Margin + Scheme : "+String.format("%.2f",dis)+"%");
        }

        firebaseDatabase.child("Users").child(current.replace("+91",""))
                .child("classP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    holder.rt.setVisibility(View.GONE);
                    holder.dis.setVisibility(View.GONE);
                    holder.scheme.setVisibility(View.GONE);
                    holder.tvD.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cartProduct products = new cartProduct(list.get(position).getName(),list.get(position).getDescription(),list.get(position).getQuantity(),list.get(position).getImage(),list.get(position).getMrp(),list.get(position).getRate(),list.get(position).getDiscount(),list.get(position).getQuantity(),list.get(position).getScheme());
                firebaseDatabase.child("Cart").child(current).child(list.get(position).getName()).setValue(products);

                Typeface typeface = Typeface.DEFAULT_BOLD;
                DynamicToast.Config.getInstance().setTextSize(20).setTextTypeface(typeface).apply();
                Toast dynamicToast = DynamicToast.make(v.getContext(), "Product added in cart", R.drawable.ic_shopping_cart, Color.parseColor("#B8F4FF"),3000);
                dynamicToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                dynamicToast.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new ProductDetails();


                Bundle arguments = new Bundle();
                arguments.putString("title", list.get(position).getName());
                arguments.putString("description", list.get(position).getDescription());
                arguments.putString("mrp", list.get(position).getMrp());
                arguments.putString("rate", list.get(position).getRate());
                arguments.putString("discount", list.get(position).getDiscount());
                arguments.putString("image", list.get(position).getImage());
                arguments.putString("quantity", list.get(position).getQuantity());
                arguments.putString("scheme", list.get(position).getScheme());
                arguments.putString("company", list.get(position).getCompany());
                arguments.putString("category", list.get(position).getCategory());

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);

                myFragment.setArguments(arguments);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, bulk_order;
        TextView tvPrintTitle, tvSd, tvM, tvR, tvD,dis,scheme;
        Button cart;
        LinearLayout rt;

        myViewHolder(@NonNull final View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvPrintTitle = itemView.findViewById(R.id.textViewTitle);
            tvSd = itemView.findViewById(R.id.textViewShortDesc);
            tvM = itemView.findViewById(R.id.textViewMRP);
            tvR = itemView.findViewById(R.id.textViewPrice);
            tvD = itemView.findViewById(R.id.textViewDiscount);
            dis = itemView.findViewById(R.id.tvDiscount);
            cart = itemView.findViewById(R.id.btnAddCart);
            scheme = itemView.findViewById(R.id.textScheme);
            bulk_order = itemView.findViewById(R.id.bulkOrders);
            rt = itemView.findViewById(R.id.rate);

        }
    }
}
