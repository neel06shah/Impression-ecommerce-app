package com.example.impressionselling.ui.cart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.impressionselling.ConfirmationActivity;
import com.example.impressionselling.R;
import com.example.impressionselling.cartProduct;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CartFragment extends Fragment {

    public CartFragment() {}

    private RecyclerView list_view;
    private DatabaseReference reference,from,to,firebaseDatabase;
    private TextView tvTotal;
    private float finalAmount=0;
    private Button buyNow;
    String current,msg="";
    ArrayList<String> prods;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        requireActivity().setTitle("Your Cart");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
        tvTotal=view.findViewById(R.id.tvTotal);

        assert current != null;
        reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(current);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prods = new ArrayList<>();
                int count = 1;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    cartProduct products = data.getValue(cartProduct.class);

                    assert products != null;
                    String scheme = products.getScheme();
                    float r =Float.parseFloat(products.getRate());
                    float q =Float.parseFloat(products.getTotal());

                    float oneTyprProductTPrice;
                    if(scheme.equals("")) {
                        oneTyprProductTPrice = r * q;
                    }
                    else {
                        float s = Float.parseFloat(scheme);
                        float price = r - ((s * r) / 100);
                        oneTyprProductTPrice = price * q;
                    }

                    finalAmount = finalAmount + oneTyprProductTPrice;
                    tvTotal.setText("Final Amount : \u20b9"+finalAmount);

                    String name = products.getName();
                    String quantity = products.getTotal();
                    String mrp = products.getMrp();
                    prods.add(count+". "+name+"\nMRP : "+mrp+"\nQuantity : "+quantity+"pcs\n\n");
                    count++;
                }
                if(finalAmount == 0){
                    buyNow.setEnabled(false);
                }
                else {
                    buyNow.setEnabled(true);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list_view=view.findViewById(R.id.listView);
        list_view.setHasFixedSize(true);
        list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        final String id = String.valueOf(System.currentTimeMillis());


        buyNow=view.findViewById(R.id.btnBuy);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalAmount != 0) {
                    firebaseDatabase = FirebaseDatabase.getInstance().getReference();
                    firebaseDatabase.child("Users").child(current.replace("+91","")).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                            if(snapshot.child("classP").exists()) {
                                from = FirebaseDatabase.getInstance().getReference().child("Cart").child(current);
                                to = FirebaseDatabase.getInstance().getReference().child("Order").child(id);
                                from.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        to.child("Products").setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError firebaseError, @NonNull DatabaseReference firebase) {
                                                if (firebaseError != null) {
                                                    Toast.makeText(getContext(), "Please connect internet", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Intent i = new Intent(getContext(), ConfirmationActivity.class);
                                                    i.putExtra("id", id);
                                                    i.putExtra("total", String.valueOf(finalAmount));
                                                    startActivity(i);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                for(int i = 0 ; i<prods.size() ; i++) {
                                    msg = msg+prods.get(i);
                                }

                                String phone = "+918652610419";
                                String message = "Hello, \nThis is *" +snapshot.child("party").getValue(String.class)+
                                        "*\n\nI want more details on :\n\n"+ msg;
                                PackageManager packageManager = requireContext().getPackageManager();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                try {
                                    String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
                                    i.setPackage("com.whatsapp");
                                    i.setData(Uri.parse(url));
                                    if (i.resolveActivity(packageManager) != null) {
                                        startActivity(i);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Please select atleast 1 item. ", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<cartProduct, cartproductsViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter <cartProduct, cartproductsViewHolder>
                (cartProduct.class,R.layout.cart_product, cartproductsViewHolder.class,reference)
        {
            @Override
            protected void populateViewHolder(cartproductsViewHolder productsViewHolder, cartProduct products, int i) {
                productsViewHolder.setName(products.getName());
                productsViewHolder.setQuantity(products.getQuantity());
                productsViewHolder.setMrp(products.getMrp());
                productsViewHolder.setRate(products.getRate());
                productsViewHolder.setDiscount(products.getDiscount());
                productsViewHolder.setDescription(products.getDescription());
                productsViewHolder.setImage(products.getImage());
                productsViewHolder.setTotal(products.getTotal());
                productsViewHolder.setScheme(products.getScheme());
            }
        };
        list_view.setAdapter(firebaseRecyclerAdapter);

        DatabaseReference Database = FirebaseDatabase.getInstance().getReference();
        Database.child("Users").child(current.replace("+91",""))
                .child("classP").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    tvTotal.setVisibility(View.GONE);
                    buyNow.setText("Request Rates");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public static class cartproductsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String title,desc,Mrp,Rate,Discount,Image,quan,sch;
        FirebaseAuth firebaseAuth;
        Button plus,minus;
        ImageButton delete;
        EditText quantity;
        DatabaseReference myRef;
        String current;
        TextView tvR;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public cartproductsViewHolder(final View itemView) {
            super(itemView);
            mView=itemView;

            quantity=itemView.findViewById(R.id.Count);
            firebaseAuth=FirebaseAuth.getInstance();
            current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
            myRef=FirebaseDatabase.getInstance().getReference();

            plus=itemView.findViewById(R.id.btnPlus);
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = Integer.parseInt(quantity.getText().toString());
                    int d = q/Integer.parseInt(quan);
                    d = d+1;
                    int n = d * Integer.parseInt(quan);
                    quantity.setText(String.valueOf(n));

                    assert current != null;
                    myRef.child("Cart").child(current).child(title).child("total").setValue(quantity.getText().toString());

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                            new CartFragment()).commit();
                }
            });

            minus=itemView.findViewById(R.id.btnMinus);
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(Integer.parseInt(quantity.getText().toString()) > Integer.parseInt(quan)) {
                        int q = Integer.parseInt(quantity.getText().toString());
                        int d = q / Integer.parseInt(quan);
                        d = d - 1;
                        int n = d * Integer.parseInt(quan);
                        quantity.setText(String.valueOf(n));

                        assert current != null;
                        myRef.child("Cart").child(current).child(title).child("total").setValue(quantity.getText().toString());

                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                new CartFragment()).commit();
                    }
                    else {
                        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        assert current != null;
                                        Query applesQuery = ref.child("Cart").child(current).orderByChild("name").equalTo(title);

                                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                                    appleSnapshot.getRef().removeValue();
                                                    Toast.makeText(itemView.getContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();

                                                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                                            new CartFragment()).commit();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e(TAG, "onCancelled", databaseError.toException());
                                            }
                                        });
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Conformation");
                        builder.setMessage("Are you sure you want to delete this item?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }
            });

            delete=itemView.findViewById(R.id.btnDelete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    assert current != null;
                                    Query applesQuery = ref.child("Cart").child(current).orderByChild("name").equalTo(title);

                                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                                appleSnapshot.getRef().removeValue();
                                                Toast.makeText(itemView.getContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();

                                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                                        new CartFragment()).commit();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, "onCancelled", databaseError.toException());
                                        }
                                    });
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Conformation");
                    builder.setMessage("Are you sure you want to delete this item?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }


        public void setImage(String image) {
            Image=image;
            ImageView imageView = mView.findViewById(R.id.imageView);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }

        public void setName(String name) {
            title=name;
            TextView tvPrintTitle = mView.findViewById(R.id.textViewTitle);
            tvPrintTitle.setText(name);
        }
        void setDescription(String description) {
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
            float m = Float.parseFloat(rate);
            tvR = mView.findViewById(R.id.textViewPrice);
            tvR.setText("\u20B9 "+String.format("%.2f",m));
        }
        public void setDiscount(String discount) {
            Discount=discount;
        }
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setScheme (final String scheme) {
            sch = scheme;
            final double d = Double.parseDouble(Discount);
            final TextView sch = mView.findViewById(R.id.textView4);
            final TextView tvD = mView.findViewById(R.id.textViewDiscount);

            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseDatabase.child("Users").child(current.replace("+91",""))
                    .child("classP").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if(scheme.length() == 0) {
                            sch.setVisibility(View.GONE);
                            tvD.setText("Retail Margin : "+String.format("%.2f",d)+"%");
                        }
                        else {
                            sch.setVisibility(View.VISIBLE);
                            sch.setText("Scheme : "+scheme+"%");
                            float m = Float.parseFloat(Mrp);
                            float r = Float.parseFloat(Rate);
                            float s = Float.parseFloat(scheme);
                            float nm = r - ((s*r)/100);
                            float dis = 100 - ((nm*100)/m);
                            tvD.setText("Retail Margin + Scheme : "+String.format("%.2f",dis)+"%");
                        } }
                    else {
                        sch.setVisibility(View.GONE);
                        tvD.setVisibility(View.GONE);
                        tvR.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setTotal(String total) {
            quantity.setText(total);
        }
    }
}
