package com.example.impressionselling.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.impressionselling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    public ProfileFragment() { }
    TextView ProfNumber;
    EditText ProfName, ProfAddress, profCity, profPincode, profWhatsapp, profParty, profGST;
    Button btnEdit;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ProfName = view.findViewById(R.id.ProfName);
        ProfNumber=view.findViewById(R.id.ProfNumber);
        ProfAddress=view.findViewById(R.id.ProfAddress);
        profPincode = view.findViewById(R.id.ProfPinCode);
        profCity = view.findViewById(R.id.ProfCity);
        profWhatsapp = view.findViewById(R.id.ProfWhatsapp);
        profParty = view.findViewById(R.id.ProfParty);
        profGST = view.findViewById(R.id.ProfGST);

        btnEdit=view.findViewById(R.id.btnEdit);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        final String email = firebaseAuth.getCurrentUser().getPhoneNumber();

        final String em = email.replace("+91","");
        ProfNumber.setText(email);

        databaseReference.child(em).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String type = dataSnapshot.child("address").getValue().toString();
                String company = dataSnapshot.child("party").getValue().toString();
                String city = dataSnapshot.child("city").getValue().toString();
                String pincode = dataSnapshot.child("pincode").getValue().toString();
                String wapp = dataSnapshot.child("whatsapp").getValue().toString();
                String gst = dataSnapshot.child("gst").getValue().toString();

                profGST.setText(gst);
                profCity.setText(city);
                profParty.setText(company);
                profPincode.setText(pincode);
                profWhatsapp.setText(wapp);
                ProfName.setText(name);
                ProfAddress.setText(type);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newName = ProfName.getText().toString();
                final String newAddress = ProfAddress.getText().toString();
                final String newNumber = profWhatsapp.getText().toString();
                final String newCity = profCity.getText().toString();
                final String newPin = profPincode.getText().toString();
                final String newGSTNumber = profGST.getText().toString();

                if (!newGSTNumber.isEmpty()) {
                    if (newGSTNumber.length() != 15) {
                        profWhatsapp.setError("Please enter Valid GST number");
                        profWhatsapp.requestFocus();
                    }
                }

                if (newNumber.length() != 10) {
                    profGST.setError("Please enter Valid number");
                    profGST.requestFocus();

                } else {
                    databaseReference.child(em).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().child("name").setValue(newName);
                            dataSnapshot.getRef().child("address").setValue(newAddress);
                            dataSnapshot.getRef().child("city").setValue(newCity);
                            dataSnapshot.getRef().child("pincode").setValue(newPin);
                            dataSnapshot.getRef().child("gst").setValue(newGSTNumber);
                            dataSnapshot.getRef().child("whatsapp").setValue(newNumber);

                            Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        return view;
    }
}