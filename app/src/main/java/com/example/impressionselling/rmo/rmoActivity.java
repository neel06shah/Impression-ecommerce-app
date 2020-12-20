package com.example.impressionselling.rmo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.example.impressionselling.R;
import com.example.impressionselling.products;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class rmoActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,database;
    String party,number;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmo);

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        number = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
        assert number != null;
        String add = number.replace("+91","");

        databaseReference.child("Users").child(add).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                party = dataSnapshot.child("party").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               createNew();
            }
        });
    }

    public void createNew() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // set the custom layout
        final View customLayout = (LayoutInflater.from(this)).inflate(R.layout.new_service_box, null);
        builder.setView(customLayout);
        // add a button
        final AutoCompleteTextView prodName = customLayout.findViewById(R.id.etName);
        final TextView date = customLayout.findViewById(R.id.queDate);
        final TextView ID = customLayout.findViewById(R.id.queID);
        final String id = String.valueOf(System.currentTimeMillis());

        final ArrayAdapter<String> names = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        database = FirebaseDatabase.getInstance().getReference().child("Products");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot children : snapshot.getChildren()) {
                    products products = children.getValue(com.example.impressionselling.products.class);
                    assert products != null;
                    String name = products.getName();
                    String company = products.getCompany();
                    if(company.equals("Zebronics") || company.equals("German") || company.equals("i ball")) {
                        names.add(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        prodName.setAdapter(names);

        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);
        date.setText(formattedDate);
        ID.setText(id);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Service");
                serviceProduct sp = new serviceProduct(date.getText().toString(),ID.getText().toString(),party,prodName.getText().toString(),"Generated",null,null,null,null,number);
                data.child(id).setValue(sp);
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragent(new GeneratedFragment(), "Generated");
        viewPagerAdapter.addFragent(new IssuedFragment(), "Given");
        viewPagerAdapter.addFragent(new CompletedFragment(), "Received");
        viewPager.setAdapter(viewPagerAdapter);
    }
}