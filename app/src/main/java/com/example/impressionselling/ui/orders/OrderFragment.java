package com.example.impressionselling.ui.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.impressionselling.R;
import com.example.impressionselling.orders;
import com.example.impressionselling.ui.home.HomeFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class OrderFragment extends Fragment {

    private RecyclerView list_view;
    private Query reference;
    LinearLayout linearLayout;
    Button button;

    public OrderFragment(){}
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        requireActivity().setTitle("Your Orders");
        setHasOptionsMenu(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

        reference= FirebaseDatabase.getInstance().getReference().child("Order").orderByChild("contact").equalTo(current);
        reference.keepSynced(true);

        list_view=view.findViewById(R.id.listView);
        list_view.setHasFixedSize(true);
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
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) {
                   list_view.setVisibility(View.VISIBLE);
                   linearLayout.setVisibility(View.GONE);
                   FirebaseRecyclerAdapter<orders, ordersViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter <orders, ordersViewHolder>
                           (orders.class,R.layout.list_order, ordersViewHolder.class,reference)
                   {
                       @Override
                       protected void populateViewHolder(ordersViewHolder ordersViewHolder , orders orders, int i) {
                           ordersViewHolder.setId(orders.getId());
                           ordersViewHolder.setPayment(orders.getPayment());
                           ordersViewHolder.setTotal(orders.getTotal());
                           ordersViewHolder.setDate(orders.getDate());
                           ordersViewHolder.setState(orders.getState());
                           ordersViewHolder.setAddress(orders.getAddress());
                       }
                   };
                   list_view.setAdapter(firebaseRecyclerAdapter);
               }
               else {
                   list_view.setVisibility(View.GONE);
                   linearLayout.setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public static class ordersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String ID,Total,Date,Payment,Add, State;
        Button orderDetails;
        Button feedback;
        LinearLayout ll;

        public ordersViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

            orderDetails = mView.findViewById(R.id.orderDetails);
            feedback = mView.findViewById(R.id.feedback);
            ll = mView.findViewById(R.id.ll);

            orderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new orderList();

                    Bundle arguments = new Bundle();
                    arguments.putString("id",ID );
                    arguments.putString("total", Total);
                    arguments.putString("date",Date );
                    arguments.putString("payment", Payment);
                    arguments.putString("address", Add);
                    arguments.putString("state",State);

                    myFragment.setArguments(arguments);
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, myFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScTT_Q3yhoNr4UFYCJ3SL7VzdG50nF_hoDC4WvOme-YIoqTkw/viewform"));
                    mView.getContext().startActivity(i);

                }
            });
        }

        public void setId(String id) {
            ID = id;
            TextView Id = mView.findViewById(R.id.orderID);
            Id.setText(id);
        }

        void setPayment(String payment) {
            Payment = payment;
            TextView Id = mView.findViewById(R.id.orderType);
            Id.setText("Payment Type : "+payment);
        }

        public void setTotal(String total) {
            Total = total;
            TextView Id = mView.findViewById(R.id.orderAmount);
            Id.setText("Total amount : \u20b9"+total);
        }
        void setDate(String date) {
            Date = date;
            TextView Id = mView.findViewById(R.id.orderDate);
            Id.setText("Date : "+date);
        }

        void setState(String state) {
            State = state;
            TextView Id = mView.findViewById(R.id.orderState);
            Id.setText(state);

            if(state.equals("Order Cancelled")){
                ll.setBackgroundColor(Color.parseColor("#FFBABA"));
            }
            else {
                ll.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

        public void setAddress(String address) {
            Add = address;
        }
    }
}
