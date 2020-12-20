package com.example.impressionselling.rmo;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.impressionselling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CompletedFragment extends Fragment {
    public CompletedFragment() { }

    Query databaseReference;
    DatabaseReference database;
    ArrayList<serviceProduct> heading = new ArrayList<>();
    RecyclerView completeList;
    FirebaseAuth firebaseAuth;
    String party;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_completed, container, false);

        completeList = view.findViewById(R.id.completeList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        completeList.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();

        final String number = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
        assert number != null;
        String add = number.replace("+91","");

        database.child("Users").child(add).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                party = dataSnapshot.child("party").getValue(String.class);
                //Toast.makeText(getContext(), party, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Service");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    heading = new ArrayList<>();
                    for (DataSnapshot children : snapshot.getChildren()) {
                        serviceProduct serviceProduct = children.getValue(com.example.impressionselling.rmo.serviceProduct.class);
                        assert serviceProduct != null;
                        if(serviceProduct.getStatus().equals("Completed") && serviceProduct.getNumber().equals(number)) {
                            heading.add(serviceProduct);
                        }
                    }
                    serviceAdapter serviceAdapter = new serviceAdapter(heading);
                    completeList.setAdapter(serviceAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}