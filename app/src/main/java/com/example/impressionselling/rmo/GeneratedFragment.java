package com.example.impressionselling.rmo;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class GeneratedFragment extends Fragment {
    public GeneratedFragment() { }

    Query databaseReference;
    DatabaseReference database;
    ArrayList<serviceProduct> heading = new ArrayList<>();
    RecyclerView generatedList;
    FirebaseAuth firebaseAuth;
    String party,number;
    ArrayAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_generated, container, false);

        generatedList = view.findViewById(R.id.generatedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        generatedList.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();

        number = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
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
                        if(serviceProduct.getStatus().equals("Generated") && serviceProduct.getNumber().equals(number)) {
                            heading.add(serviceProduct);
                        }
                    }
                    serviceAdapter serviceAdapter = new serviceAdapter(heading);
                    generatedList.setAdapter(serviceAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}