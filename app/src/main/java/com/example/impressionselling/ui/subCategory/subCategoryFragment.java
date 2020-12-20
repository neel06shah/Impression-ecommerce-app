package com.example.impressionselling.ui.subCategory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.impressionselling.R;
import com.example.impressionselling.subCategory;
import com.example.impressionselling.ui.product.ProductsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class subCategoryFragment extends Fragment {
    private RecyclerView list_view;
    private Query reference;
    String title;
    ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        imageView = view.findViewById(R.id.coming_soon);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
            requireActivity().setTitle(title);
            reference =FirebaseDatabase.getInstance().getReference().child("Sub-Category").orderByChild("category").equalTo(title);
        }

        list_view=view.findViewById(R.id.subCategoryList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        list_view.setLayoutManager(gridLayoutManager);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list_view.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    FirebaseRecyclerAdapter<subCategory, subCategoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<subCategory, subCategoryViewHolder>
                            (subCategory.class, R.layout.sub_category_list_item, subCategoryViewHolder.class, reference) {

                        @Override
                        protected void populateViewHolder(subCategoryViewHolder subCategoryViewHolder, subCategory subCategory, int i) {
                            subCategoryViewHolder.setTitle(subCategory.getTitle());
                            subCategoryViewHolder.setImage(subCategory.getImage());
                        }
                    };
                    list_view.setAdapter(firebaseRecyclerAdapter);
                }
                else {
                    imageView.setVisibility(View.VISIBLE);
                    list_view.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Please Connect Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class subCategoryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String tv;

        public subCategoryViewHolder(final View itemView) {
            super(itemView);
            mView=itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new ProductsFragment();

                    Bundle arguments = new Bundle();
                    arguments.putString("title", tv);
                    arguments.putString("from","category");

                    myFragment.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
                }
            });
        }

        public void setTitle(String title) {
            tv=title;
            TextView Title = mView.findViewById(R.id.subCategoryTitle);
            Title.setText(title);
        }

        public void setImage (String image) {
            ImageView imageView = mView.findViewById(R.id.subCategoryImage);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }
    }
}