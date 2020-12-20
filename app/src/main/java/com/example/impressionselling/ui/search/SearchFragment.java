package com.example.impressionselling.ui.search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.impressionselling.R;
import com.example.impressionselling.adapters.AdapterClass;
import com.example.impressionselling.products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView list_view;
    ImageButton filter;
    androidx.appcompat.widget.SearchView searchView;
    ArrayList<products> list;
    public SearchFragment() { }
    ArrayList<products> myList;
    ArrayList<products> fil;
    ArrayList<String> companies, areas;
    DatabaseReference Company, fDatabase;
    AdapterClass adapterClass;
    int Count = 0;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        list_view = view.findViewById(R.id.listView);
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        list_view.setLayoutManager(gridLayoutManager);

        filter = view.findViewById(R.id.filter);

        filter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.filter_box, null);
                dialogBuilder.setTitle("Filter");
                dialogBuilder.setIcon(R.drawable.ic_filter_list_black_24dp);
                dialogBuilder.setMessage("Please Select Company / Category below : ");
                dialogBuilder.setView(dialogView);

                final Spinner spCompany = dialogView.findViewById(R.id.spCompany);
                final Spinner spCategory = dialogView.findViewById(R.id.spCategory);

                Company = FirebaseDatabase.getInstance().getReference().child("Company");
                Company.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        companies=new ArrayList<>();
                        companies.add("Select Company");
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            String name = child.child("title").getValue(String.class);
                            companies.add(name);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, companies);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCompany.setAdapter(areasAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                fDatabase=FirebaseDatabase.getInstance().getReference().child("Sub-Category");
                fDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        areas = new ArrayList<>();
                        areas.add("Select Category");
                        for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("title").getValue(String.class);
                            areas.add(areaName);
                        }
                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, areas);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCategory.setAdapter(areasAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Please Connect Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });



                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String company = spCompany.getSelectedItem().toString();
                        String categroy = spCategory.getSelectedItem().toString();
                        if(myList == null) {
                            if (company.equals("Select Company") && !categroy.equals("Select Category")) {
                                myList = new ArrayList<>();
                                for (products object : list) {
                                    if (object.getActivity_category().equals(true + "_" + categroy)) {
                                        myList.add(object);
                                    }
                                }
                                final AdapterClass adapterClass = new AdapterClass(myList);
                                list_view.setAdapter(adapterClass);
                            }
                            if (!company.equals("Select Company") && categroy.equals("Select Category")) {
                                myList = new ArrayList<>();
                                for (products object : list) {
                                    if (object.getActivity_company().equals(true + "_" + company)) {
                                        myList.add(object);
                                    }
                                }
                                final AdapterClass adapterClass = new AdapterClass(myList);
                                list_view.setAdapter(adapterClass);
                            }
                            if (!company.equals("Select Company") && !categroy.equals("Select Category")) {
                                myList = new ArrayList<>();
                                for (products object : list) {
                                    if (object.getActivity_company().equals(true + "_" + company) && object.getActivity_category().equals(true + "_" + categroy)) {
                                        myList.add(object);
                                    }
                                }
                                final AdapterClass adapterClass = new AdapterClass(myList);
                                list_view.setAdapter(adapterClass);
                            }
                        }
                    }
                });
                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
        list_view.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query myRef = FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("activity").equalTo(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Count++;
                       list.add(ds.getValue(products.class));
                    }
                    searchView.setQueryHint("Search from "+Count+" products");
                    adapterClass = new AdapterClass(list);
                    list_view.setAdapter(adapterClass);
                }
                if(searchView != null){
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            search(query);
                            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            search(newText);
                            return true;
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void search(String s) {
        ArrayList<products> searchList = new ArrayList<>();
        final String replace = s.toLowerCase().replace(".", "•").replace("/", "\\");
        if(myList == null) {
            for (products object : list) {
                if (object.getName().toLowerCase().contains(replace)) {
                    searchList.add(object);
                }
            }
            final AdapterClass adapterClass = new AdapterClass(searchList);
            list_view.setAdapter(adapterClass);
        }
        else {
            for (products object : myList) {
                if (object.getName().toLowerCase().contains(replace)) {
                    searchList.add(object);
                }
            }
            final AdapterClass adapterClass = new AdapterClass(searchList);
            list_view.setAdapter(adapterClass);
        }
    }
}