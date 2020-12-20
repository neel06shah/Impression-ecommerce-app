package com.example.impressionselling.ui.wish;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.impressionselling.R;
import com.example.impressionselling.cartProduct;
import com.example.impressionselling.products;
import com.example.impressionselling.ui.home.HomeFragment;
import com.example.impressionselling.ui.productDetails.ProductDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Objects;

public class WishList extends Fragment {
    public WishList() {}

        FirebaseAuth firebaseAuth;
        private RecyclerView list_view;
        private Query reference;
        String title;
        LinearLayout linearLayout;
        Button button;

        public View onCreateView (@NonNull LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
            requireActivity().setTitle("Your Wish list");

            firebaseAuth =FirebaseAuth.getInstance();
            String user = firebaseAuth.getCurrentUser().getPhoneNumber();
            reference = FirebaseDatabase.getInstance().getReference().child("Wishlist").child(user);


            list_view = view.findViewById(R.id.listView);
            list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

            button = view.findViewById(R.id.button);
            linearLayout = view.findViewById(R.id.linearLayout);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new HomeFragment();
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, myFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            return view;
        }

        @Override
        public void onStart () {
            super.onStart();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        linearLayout.setVisibility(View.GONE);
                        list_view.setVisibility(View.VISIBLE);
                        FirebaseRecyclerAdapter<products, productsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<products, productsViewHolder>
                                (products.class, R.layout.product_item, productsViewHolder.class, reference) {
                            @Override
                            protected void populateViewHolder(productsViewHolder productsViewHolder, products products, int i) {
                                productsViewHolder.setName(products.getName());
                                productsViewHolder.setQuantity(products.getQuantity());
                                productsViewHolder.setMrp(products.getMrp());
                                productsViewHolder.setRate(products.getRate());
                                productsViewHolder.setDiscount(products.getDiscount());
                                productsViewHolder.setDescription(products.getDescription());
                                productsViewHolder.setImage(products.getImage());
                                productsViewHolder.setScheme(products.getScheme());
                                productsViewHolder.setCompany(products.getCompany());
                                productsViewHolder.setBulk(products.getBulk());
                            }
                        };
                        list_view.setAdapter(firebaseRecyclerAdapter);
                    }
                    else {
                        linearLayout.setVisibility(View.VISIBLE);
                        list_view.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    public static class productsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String title,desc,Mrp,Rate,Discount,Image,quan,sch,cmp;
        int m,r;
        DatabaseReference firebaseDatabase;
        FirebaseAuth firebaseAuth;
        String current;
        Button cart;
        ImageButton delete;
        DatabaseReference reference;
        LinearLayout rt;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public productsViewHolder(final View itemView) {
            super(itemView);
            mView=itemView;

            delete = mView.findViewById(R.id.btnDelete);
            delete.setVisibility(View.VISIBLE);

            rt = mView.findViewById(R.id.rate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new ProductDetails();

                    int save = m-r;
                    String s = String.valueOf(save);

                    Bundle arguments = new Bundle();
                    arguments.putString("title", title);
                    arguments.putString("description", desc);
                    arguments.putString("mrp", Mrp);
                    arguments.putString("rate", Rate);
                    arguments.putString("discount", Discount);
                    arguments.putString("image", Image);
                    arguments.putString("quantity", quan);
                    arguments.putString("save",s);
                    arguments.putString("scheme",sch);
                    arguments.putString("company",cmp);

                    myFragment.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
                }
            });

            cart = mView.findViewById(R.id.btnAddCart);
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseAuth =FirebaseAuth.getInstance();
            current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();


            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartProduct products = new cartProduct(title,desc,quan,Image,Mrp,Rate,Discount,quan,sch);
                    firebaseDatabase.child("Cart").child(current).child(title).setValue(products);

                    Typeface typeface = Typeface.DEFAULT_BOLD;
                    DynamicToast.Config.getInstance().setTextSize(20).setTextTypeface(typeface).apply();
                    Toast dynamicToast = DynamicToast.make(mView.getContext(), "Product added in cart", R.drawable.ic_shopping_cart, Color.parseColor("#B8F4FF"),3000);
                    dynamicToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                    dynamicToast.show();
                }
            });
        }

        public void setImage(String image) {
            Image=image;
            ImageView imageView = mView.findViewById(R.id.imageView);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }

        public void setName(final String name) {
            title=name;
            TextView tvPrintTitle = mView.findViewById(R.id.textViewTitle);
            tvPrintTitle.setText(name);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference = FirebaseDatabase.getInstance().getReference().child("Wishlist").child(current).child(name);
                    reference.removeValue();
                }
            });
        }
        public void setDescription(String description) {
            desc = description;
        }
        public void setQuantity(String quantity) {
            quan=quantity;
            TextView tvSd = mView.findViewById(R.id.textViewShortDesc);
            tvSd.setText("Pack of "+quantity+" units");
        }
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setMrp(String mrp) {
            Mrp=mrp;
            float m = Float.parseFloat(mrp);
            TextView tvM = mView.findViewById(R.id.textViewMRP);
            tvM.setText("\u20B9 "+String.format("%.2f",m));
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setRate(String rate) {
            Rate=rate;
            final float m = Float.parseFloat(rate);
            final TextView tvR = mView.findViewById(R.id.textViewPrice);
            firebaseDatabase.child("Users").child(current.replace("+91",""))
                    .child("classP").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        rt.setVisibility(View.VISIBLE);
                        tvR.setText("\u20B9 "+String.format("%.2f",m));
                    }
                    else {
                        rt.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setScheme (final String scheme) {
            sch = scheme;
            final double d = Double.parseDouble(Discount);
            final TextView sch = mView.findViewById(R.id.textScheme);
            final TextView tvR = mView.findViewById(R.id.textViewDiscount);
            firebaseDatabase.child("Users").child(current.replace("+91",""))
                    .child("classP").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if(scheme.length() == 0) {
                            sch.setVisibility(View.GONE);
                            tvR.setText("Retail Margin : "+String.format("%.2f",d)+"%");
                        }
                        else {
                            sch.setVisibility(View.VISIBLE);
                            sch.setText("Scheme : "+scheme+"%");
                            float m = Float.parseFloat(Mrp);
                            float r = Float.parseFloat(Rate);
                            float s = Float.parseFloat(scheme);
                            float nm = r - ((s*r)/100);
                            float dis = 100 - ((nm*100)/m);
                            tvR.setText("Retail Margin + Scheme : "+String.format("%.2f",dis)+"%");
                        } }
                    else {
                        sch.setVisibility(View.GONE);
                        tvR.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        public void setDiscount(String discount) {
            Discount=discount;
        }

        public void setCompany (String company) {
            cmp = company;
        }

        public void setBulk (Boolean bulk) {
            ImageView imageView = mView.findViewById(R.id.bulkOrders);
            if(bulk == null) {
                imageView.setVisibility(View.GONE);
            }
            else {
                if(bulk) {
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
