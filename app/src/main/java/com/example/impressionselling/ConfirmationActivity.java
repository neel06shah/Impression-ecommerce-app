package com.example.impressionselling;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.impressionselling.ui.orders.orderList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ConfirmationActivity extends AppCompatActivity {

    TextView conOrder, conNumber, conTotal,conDate,conGrandTotal,conRounding;
    EditText conAddress;
    Button btnSubmit;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,from,to,clear,reference;
    RadioGroup rdGroup;
    String payment,name,pay,id;
    RecyclerView list_view;
    final int UPI_PAYMENT = 0;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        conOrder=findViewById(R.id.conOrder);
        conNumber=findViewById(R.id.conNumber);
        conTotal=findViewById(R.id.conTotal);
        conAddress=findViewById(R.id.conAddress);
        btnSubmit=findViewById(R.id.btnSubmit);
        rdGroup = findViewById(R.id.rdGroup);
        conDate = findViewById(R.id.conDate);
        conGrandTotal = findViewById(R.id.conGrandTotal);
        conRounding = findViewById(R.id.conRounding);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        final String number = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
        assert number != null;
        String add = number.replace("+91","");

        databaseReference.child("Users").child(add).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String address = dataSnapshot.child("address").getValue(String.class);
                name = dataSnapshot.child("name").getValue(String.class);
                pay = dataSnapshot.child("payment").getValue(String.class);
                conAddress.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent i = getIntent();
        id = i.getStringExtra("id");
        final float t = Float.parseFloat(Objects.requireNonNull(i.getStringExtra("total")));
        @SuppressLint("DefaultLocale")
        float t1 = Math.round(t);
        @SuppressLint("DefaultLocale") final String total = String.format("%.2f",t1);
        float round = t1 - t;
        conRounding.setText(String.format("%.2f",round));
        conTotal.setText("\u20b9 "+total);
        conGrandTotal.setText("\u20b9 "+total);
        conOrder.setText(id);
        conNumber.setText(number);

        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);
        conDate.setText(formattedDate);

        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbCOD) {
                    payment="Cash on delivery";
                }
                else if (checkedId == R.id.rbUPI) {
                    payment="UPI";
                    Uri uri = new Uri.Builder()
                                    .scheme("upi")
                                    .authority("pay")
                                    .appendQueryParameter("pa", "neel06shah@okaxis")       // virtual ID
                                    .appendQueryParameter("pn", "Neel Shah")               // name
                                    .appendQueryParameter("tn", "Order ID :"+id)           // any note about payment
                                    .appendQueryParameter("am", total)                     // amount
                                    .appendQueryParameter("cu", "INR")                     // currency
                                    .build();

                    Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                    upiPayIntent.setData(uri);
                    Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                    if(null != chooser.resolveActivity(getPackageManager())) {
                        startActivityForResult(chooser, UPI_PAYMENT);
                    } else {
                        Toast.makeText(ConfirmationActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
                    }
                }
                else if (checkedId == R.id.rbBank) {
                    payment="Bank";
                    final AlertDialog.Builder alert = new AlertDialog.Builder(ConfirmationActivity.this);
                    alert.setTitle("Online bank payment");
                    alert.setMessage("Beneficiary Name :\n" +
                            "Neel K. Shah\n" +
                            "\n" +
                            "Bank name : \n" +
                            "Shamrao Vithal co-op bank ltd.\n" +
                            "\n" +
                            "Branch name : \n" +
                            "Rajaji Path- Dombivali east\n" +
                            "\n" +
                            "IFSC code :\n" +
                            "SVCB0000182\n" +
                            "\n" +
                            "Account No. :\n" +
                            "118203130001444\n");

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("Share", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            String shareBody = "Here is the share content body";
                            intent.setType("text/plain");
                            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Beneficiary Name :\n" +
                                    "Neel K. Shah\n" +
                                    "\n" +
                                    "Bank name : \n" +
                                    "Shamrao Vithal co-op bank ltd.\n" +
                                    "\n" +
                                    "Branch name : \n" +
                                    "Rajaji Path- Dombivali east\n" +
                                    "\n" +
                                    "IFSC code :\n" +
                                    "SVCB0000182\n" +
                                    "\n" +
                                    "Account No. :\n" +
                                    "118203130001444\n" );
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(intent, "Share Details"));
                        }
                    });
                    alert.show();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pay != null) {
                    if(rdGroup.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(ConfirmationActivity.this, "Please select a Payment Type", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String a = conAddress.getText().toString();
                        from = FirebaseDatabase.getInstance().getReference().child("Cart").child(number);
                        to = FirebaseDatabase.getInstance().getReference().child("Order").child(id);
                        to.child("id").setValue(id);
                        to.child("name").setValue(name);
                        to.child("address").setValue(a);
                        to.child("contact").setValue(number);
                        to.child("payment").setValue(payment);
                        to.child("total").setValue(total);
                        to.child("date").setValue(formattedDate);
                        to.child("state").setValue("Waiting for conformation");

                        Toast.makeText(ConfirmationActivity.this, "Order added successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ConfirmationActivity.this, Dash.class);
                        startActivity(i);
                        finish();
                    }
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmationActivity.this);
                    builder.setTitle("Not Verified");
                    builder.setMessage("Your account is not verified by our owner. Due to that your order cannot be placed.\n\nSorry for inconvenience.").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clear = FirebaseDatabase.getInstance().getReference().child("Order");
                            clear.child(id).removeValue();
                            ConfirmationActivity.this.finish();
                        }
                    }).show();
                }
            }
        });

        reference = databaseReference.child("Order").child(id).child("Products");
        list_view=findViewById(R.id.productList);
        list_view.setLayoutManager(new LinearLayoutManager(ConfirmationActivity.this));
    }

    @Override
    protected void onStart() {
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

    @Override
    public void onBackPressed() {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        clear = FirebaseDatabase.getInstance().getReference().child("Order");
                        clear.child(id).removeValue();
                        ConfirmationActivity.this.finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conformation");
        builder.setMessage("Are you sure you want to cancel your order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}
