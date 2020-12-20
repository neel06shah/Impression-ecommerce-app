package com.example.impressionselling.ui.home;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.impressionselling.R;
import com.example.impressionselling.category;
import com.example.impressionselling.company;
import com.example.impressionselling.products;
import com.example.impressionselling.rmo.rmoActivity;
import com.example.impressionselling.ui.cart.CartFragment;
import com.example.impressionselling.ui.product.ProductsFragment;
import com.example.impressionselling.ui.productDetails.ProductDetails;
import com.example.impressionselling.ui.search.SearchFragment;
import com.example.impressionselling.ui.subCategory.subCategoryFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    public HomeFragment() { }
    private ViewFlipper viewFlipper;
    private RecyclerView list_view, companyView, bestSellingList;
    private DatabaseReference reference;
    private DatabaseReference company;
    private DatabaseReference bestProducts;
    private ArrayList<String> list;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private ImageView adImage;
    private TextView adHead;
    private FloatingActionButton floatingActionButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        requireActivity().setTitle("Kanchan Traders");

        adImage = view.findViewById(R.id.adImage);
        adHead = view.findViewById(R.id.adHead);
        DatabaseReference advertise = FirebaseDatabase.getInstance().getReference().child("Advertise");

        advertise.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String head = dataSnapshot.child("name").getValue(String.class);
                    String image = dataSnapshot.child("image").getValue(String.class);
                    final String category = dataSnapshot.child("category").getValue(String.class);

                    adHead.setText(head);
                    Glide.with(view.getContext()).load(image).into(adImage);

                    adImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            Fragment myFragment = new ProductsFragment();

                            Bundle arguments = new Bundle();
                            arguments.putString("title", category);
                            arguments.putString("from","category");

                            myFragment.setArguments(arguments);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        progressBar = view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.linearLayout);

        viewFlipper=view.findViewById(R.id.viewFlipper);
        DatabaseReference images = FirebaseDatabase.getInstance().getReference().child("Slides");
        images.keepSynced(true);

        images.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    list.add(child.getValue(String.class));
                }
                for (int i =0; i < list.size(); i++) {
                    String downloadImageUrl = list.get(i);
                    setFlipperImage(downloadImageUrl);
                }
                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        companyView = view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1,LinearLayoutManager.HORIZONTAL,false);companyView.setLayoutManager(gridLayoutManager);

        reference= FirebaseDatabase.getInstance().getReference().child("Category");
        reference.keepSynced(true);

        company = FirebaseDatabase.getInstance().getReference().child("Company");
        company.keepSynced(true);

        list_view=view.findViewById(R.id.gridView);
        list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        bestSellingList=view.findViewById(R.id.bestSellingList);
        GridLayoutManager grid = new GridLayoutManager(getContext(),2);
        bestSellingList.setLayoutManager(grid);

        bestProducts = FirebaseDatabase.getInstance().getReference().child("Best_Selling");

        Button explore = view.findViewById(R.id.btnViewMore);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new ProductsFragment();

                Bundle arguments = new Bundle();
                arguments.putString("from","best_selling");
                arguments.putString("title", "");

                myFragment.setArguments(arguments);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();

            }
        });

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), rmoActivity.class);
                startActivity(i);
            }
        });

        NestedScrollView nsv = view.findViewById(R.id.nsv);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }
        });


        return view;
    }
    private void setFlipperImage(String res) {
        ImageView image = new ImageView(getContext());
        Glide.with(this).load(res).into(image);

        viewFlipper.addView(image);

        viewFlipper.setFlipInterval(2500);
        viewFlipper.setAutoStart(true);

        viewFlipper.startFlipping();
        viewFlipper.setInAnimation(getContext(),R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getContext(),R.anim.slide_out_right);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<category, categoryViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter <category, categoryViewHolder>
                (category.class,R.layout.grid_item, categoryViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(categoryViewHolder categoryViewHolder, category category, int i) {
                categoryViewHolder.setTitle(category.getTitle());
                categoryViewHolder.setDescription(category.getDescription());
                categoryViewHolder.setImage(category.getImage());

            }
        };

        list_view.setAdapter(firebaseRecyclerAdapter);

        FirebaseRecyclerAdapter<company, companyViewHolder> firebaseRecycler= new FirebaseRecyclerAdapter <company, companyViewHolder>
                (company.class,R.layout.company_list, companyViewHolder.class,company) {
            @Override
            protected void populateViewHolder(companyViewHolder companyViewHolder, com.example.impressionselling.company company, int i) {
                companyViewHolder.setTitle(company.getTitle());
                companyViewHolder.setImage(company.getImage());
            }
        };

        companyView.setAdapter(firebaseRecycler);

        FirebaseRecyclerAdapter<products, productsViewHolder> adapter = new FirebaseRecyclerAdapter<products, productsViewHolder>
                (products.class, R.layout.best_selling_product, productsViewHolder.class, bestProducts) {
            @Override
            protected void populateViewHolder(productsViewHolder productsViewHolder, products products, int i) {
                if(i < 4) {
                    productsViewHolder.setName(products.getName());
                    productsViewHolder.setQuantity(products.getQuantity());
                    productsViewHolder.setMrp(products.getMrp());
                    productsViewHolder.setRate(products.getRate());
                    productsViewHolder.setDiscount(products.getDiscount());
                    productsViewHolder.setDescription(products.getDescription());
                    productsViewHolder.setImage(products.getImage());
                    productsViewHolder.setScheme(products.getScheme());
                    productsViewHolder.setCompany(products.getCompany());
                    productsViewHolder.setCategory(products.getCategory());
                }
                else {
                    productsViewHolder.mView.setVisibility(View.GONE);
                }
            }
        };
        bestSellingList.setAdapter(adapter);
    }

    public static class productsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String title,desc,Mrp,Rate,Discount,Image,quan,sch,cmp,cat;
        ImageView imageView;
        TextView tvPrintTitle;
        int m,r;

        public productsViewHolder(final View itemView) {
            super(itemView);
            mView=itemView;

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
                    arguments.putString("category",cat);

                    myFragment.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
                }
            });
        }

        public void setImage(String image) {
            Image=image;
            imageView = mView.findViewById(R.id.productImage);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }

        public void setName(String name) {
            title=name;
            tvPrintTitle = mView.findViewById(R.id.productName);
            tvPrintTitle.setText(name);
        }
        void setDescription(String description) {
            desc = description;
        }
        public void setQuantity(String quantity) {
            quan=quantity;
        }
        public void setMrp(String mrp) {
            Mrp=mrp;
        }
        public void setRate(String rate) {
            Rate=rate;
        }
        public void setDiscount(String discount) {
            Discount=discount;
        }
        public void setScheme (String scheme) {
            sch = scheme;
        }
        public void setCompany (String company) {
            cmp = company;
        }
        public void setCategory (String category) {
            cat = category;
        }

    }

    public static class categoryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String tv;

        public categoryViewHolder(final View itemView) {
            super(itemView);
            mView=itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new subCategoryFragment();

                    Bundle arguments = new Bundle();
                    arguments.putString("title", tv);

                    myFragment.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
                }
            });
        }

        public void setTitle(String title) {
            tv=title;
            TextView Title = mView.findViewById(R.id.tvCategory);
            Title.setText(title);
        }
        void setDescription(String description) {
            TextView Title = mView.findViewById(R.id.tvDescription);
            Title.setText(description);
        }

        public void setImage (String image) {
            ImageView imageView = mView.findViewById(R.id.categoryImage);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }
    }

    public static class companyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public companyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(final String title) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new ProductsFragment();

                    Bundle arguments = new Bundle();
                    arguments.putString("from","company");
                    arguments.putString("title", title);

                    myFragment.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
                }
            });
        }

        public void setImage(String image) {
            ImageView imageView = mView.findViewById(R.id.companyImage);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }
    }
}