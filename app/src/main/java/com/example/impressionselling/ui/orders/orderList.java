package com.example.impressionselling.ui.orders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.impressionselling.R;
import com.example.impressionselling.list;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class orderList extends Fragment {

    public orderList() { }

    RecyclerView list_view;
    DatabaseReference reference, databaseReference;
    TextView finalT,orderID, orderDate, tvpayment, tvaddress,TOTAL;
    String id,total,date,payment,address,state;
    Button contact, cancel;


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getString("id");
            total = bundle.getString("total");
            date = bundle.getString("date");
            payment = bundle.getString("payment");
            address = bundle.getString("address");
            state = bundle.getString("state");

        }
        orderDate = view.findViewById(R.id.conDate);
        orderDate.setText(date);

        orderID = view.findViewById(R.id.order_id);
        orderID.setText(id);

        finalT=view.findViewById(R.id.conGrandTotal);
        TOTAL = view.findViewById(R.id.conTotal);
        float t = Float.parseFloat(total);
        finalT.setText("\u20b9 "+String.format("%.2f",t));
        TOTAL.setText("\u20b9 "+String.format("%.2f",t));

        tvpayment = view.findViewById(R.id.payment_method);
        tvpayment.setText(payment);

        tvaddress = view.findViewById(R.id.delivery_address);
        tvaddress.setText(address);

        contact = view.findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+919323610419";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        cancel = view.findViewById(R.id.cancel);

        if(state.equals("Order Cancelled")){
            cancel.setVisibility(View.GONE);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(id);
                                databaseReference.child("state").setValue("Order Cancelled");

                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                Fragment myFragment = new OrderFragment();
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Conformation");
                builder.setMessage("Are you sure you want to cancel your order?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("Order").child(id).child("Products");

        list_view=view.findViewById(R.id.productList);
        list_view.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<list, listViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter <list, listViewHolder>
                (list.class,R.layout.products, listViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(listViewHolder productsViewHolder, list products, int i) {
                productsViewHolder.setName(products.getName());
                productsViewHolder.setRate(products.getRate());
                productsViewHolder.setTotal(products.getTotal());
                productsViewHolder.setScheme(products.getScheme());
            }
        };
        list_view.setAdapter(firebaseRecyclerAdapter);
    }

    public static class listViewHolder extends RecyclerView.ViewHolder {
        View mView;
        float r,quan,s;

        public listViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name) {
            TextView Name = mView.findViewById(R.id.prodName);
            Name.setText(name);
        }

        @SuppressLint("DefaultLocale")
        public void setRate(String rate) {
            TextView Rate = mView.findViewById(R.id.prodRate);
            r = Float.parseFloat(rate);
            Rate.setText(String.format("%.2f",r));
        }

        @SuppressLint("DefaultLocale")
        public void setTotal(String total) {
            TextView Total = mView.findViewById(R.id.prodQuantity);
            Total.setText(total);
            quan = Float.parseFloat(total);
        }
        @SuppressLint("DefaultLocale")
        public void setScheme (String scheme) {
            TextView fin = mView.findViewById(R.id.prodTotal);
            TextView sch = mView.findViewById(R.id.prodScheme);
            sch.setText(scheme);
            if(scheme.equals("")) {
                float t = r*quan;
                fin.setText(String.format("%.2f",t));
            }
            else {
                s = Float.parseFloat(scheme);
                float nm = r - ((s*r)/100);
                float t = nm*quan;
                fin.setText(String.format("%.2f",t));
            }
        }

    }
}
