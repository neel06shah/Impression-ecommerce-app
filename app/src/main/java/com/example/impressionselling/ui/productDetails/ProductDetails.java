package com.example.impressionselling.ui.productDetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.impressionselling.ConfirmationActivity;
import com.example.impressionselling.R;
import com.example.impressionselling.SliderItem;
import com.example.impressionselling.adapters.SliderAdapterExample;
import com.example.impressionselling.cartProduct;
import com.example.impressionselling.products;
import com.example.impressionselling.ui.product.ProductsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

public class ProductDetails extends Fragment {

    public ProductDetails() { }

    private TextView tvQuantity;
    private TextView tvSave;
    private TextView Quantity;
    private TextView FinalBill;
    private TextView tv9;
    private TextView tvPacking;
    private TextView tvScheme;
    private TextView tvCompany;
    String title,mrp,description,image,rate,quantity,discount,save, sch,cmp,cat;
    Button plus,minus,btnAddCart,btnBuyNow, btnBuy, btnCancel;
    ImageButton btnWish, btnShare;
    EditText total;
    ImageView tv8;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferences,photos,firebaseDatabase;
    ArrayList<SliderItem> sliderItems ;
    ProgressBar progressBar;
    RecyclerView relatedProducts;
    Query reference;
    SliderView sliderView;
    NestedScrollView scrollView;
    String current;

    private SliderAdapterExample adapter;
    private BottomSheetBehavior mBottomSheetBehavior1;
    LinearLayout rt, sc;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        assert view != null;
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        sliderView = view.findViewById(R.id.imageSlider);
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mBottomSheetBehavior1.setPeekHeight(120);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mBottomSheetBehavior1.setPeekHeight(0);
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
            description = bundle.getString("description");
            mrp = bundle.getString("mrp");
            rate = bundle.getString("rate");
            image = bundle.getString("image");
            quantity = bundle.getString("quantity");
            discount=bundle.getString("discount");
            save=bundle.getString("save");
            sch = bundle.getString("scheme");
            cmp = bundle.getString("company");
            cat = bundle.getString("category");
        }

        reference = FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("category").equalTo(cat);
        reference.keepSynced(true);

        relatedProducts = view.findViewById(R.id.relatedProducts);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        relatedProducts.setLayoutManager(gridLayoutManager);

        scrollView = view.findViewById(R.id.nested);
        tvCompany = view.findViewById(R.id.tvCompany);
        tvCompany.setText(cmp);

        tvCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new ProductsFragment();

                Bundle arguments = new Bundle();
                arguments.putString("from","company");
                arguments.putString("title", cmp);

                myFragment.setArguments(arguments);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).addToBackStack(null).commit();
            }
        });

        tv8 = view.findViewById(R.id.imageView4);
        tv9 = view.findViewById(R.id.textView9);
        tvScheme = view.findViewById(R.id.tvScheme);

        if(sch.length() == 0){
            tv8.setVisibility(View.GONE);
            tv9.setVisibility(View.GONE);
            tvScheme.setVisibility(View.GONE);
        }
        else {
            tvScheme.setText(sch+"%");
        }
        double disc = Double.parseDouble(discount.trim());
        String dis = String.format("%.2f",disc);

        photos = FirebaseDatabase.getInstance().getReference().child("Products").child(title).child("images");
        photos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    sliderItems = new ArrayList<>();
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        sliderItems.add(child.getValue(SliderItem.class));
                    }
                    adapter = new SliderAdapterExample(sliderItems);
                    sliderView.setSliderAdapter(adapter);

                    sliderView.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                    sliderView.setIndicatorSelectedColor(Color.parseColor("#4b0082"));
                    sliderView.setIndicatorUnselectedColor(Color.parseColor("#039be5"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView tvTile = view.findViewById(R.id.tvTitle);
        tvTile.setText(title);

        rt = view.findViewById(R.id.rate);
        sc = view.findViewById(R.id.scheme);

        float m = Float.parseFloat(mrp);
        TextView tvMrp = view.findViewById(R.id.tvMrp);
        tvMrp.setText("MRP : Rs."+String.format("%.2f",m));

        TextView tvDescription = view.findViewById(R.id.tvDescription);
        tvDescription.setText(description);

        float r = Float.parseFloat(rate);
        TextView tvRate = view.findViewById(R.id.tvRate);
        tvRate.setText(String.format("%.2f",r));

        tvQuantity=view.findViewById(R.id.tvQuantity);
        tvQuantity.setText("Pack of "+quantity+" units");

        tvPacking=view.findViewById(R.id.tvPacking);
        tvPacking.setText("Pack of "+quantity+" units");

        tvSave = view.findViewById(R.id.tvSave);
        tvSave.setText("Retail Margin : "+dis+"%");

        Quantity = view.findViewById(R.id.totalQuantity);
        Quantity.setText("Quantity : "+quantity);

        FinalBill = view.findViewById(R.id.totalBill);
        if(sch.equals("")) {
            FinalBill.setText("Amount : \u20b9" + Integer.parseInt(quantity) * Float.parseFloat(rate));
        }
        else {
            float r1 = Float.parseFloat(rate);
            float s = Float.parseFloat(sch);
            float price = r1 - ((s * r1) / 100);
            FinalBill.setText("Amount : \u20b9" + String.format("%.2f",Integer.parseInt(quantity) * price));
        }

        total=view.findViewById(R.id.Count);
        total.setText(quantity);

        plus=view.findViewById(R.id.btnPlus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(total.getText().toString());
                int d = q/Integer.parseInt(quantity);
                d = d+1;
                int n = d * Integer.parseInt(quantity);
                total.setText(String.valueOf(n));
                Quantity.setText("Quantity : "+n);
                float r = Float.parseFloat(rate);

                if (sch.equals("")) {
                    FinalBill.setText("Amount : \u20b9" + String.format("%.2f",n * Float.parseFloat(rate)));
                }
                else {
                    float s = Float.parseFloat(sch);
                    float price = r - ((s * r) / 100);
                    FinalBill.setText("Amount : \u20b9" + String.format("%.2f",n * price));
                }
            }
        });

        minus=view.findViewById(R.id.btnMinus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(total.getText().toString()) != 0) {
                    int q = Integer.parseInt(total.getText().toString());
                    int d = q / Integer.parseInt(quantity);
                    d = d - 1;
                    int n = d * Integer.parseInt(quantity);
                    total.setText(String.valueOf(n));
                    float r = Float.parseFloat(rate);

                    Quantity.setText("Quantity : "+n);

                    if (sch.equals("")) {
                        FinalBill.setText("Amount : \u20b9" + String.format("%.2f",n * r));
                    }
                    else {
                        float s = Float.parseFloat(sch);
                        float price = r - ((s * r) / 100);
                        FinalBill.setText("Amount : \u20b9" + String.format("%.2f", n * price));
                    }
                }
                else {
                    Toast.makeText(getContext(), "Please enter a Valid Quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

        databaseReferences= FirebaseDatabase.getInstance().getReference();
        btnAddCart=view.findViewById(R.id.btnAddCart);
        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProduct products = new cartProduct(title,description,quantity,image,mrp,rate,discount,quantity,sch);
                databaseReferences.child("Cart").child(current).child(title).setValue(products);
                Typeface typeface = Typeface.DEFAULT_BOLD;
                DynamicToast.Config.getInstance().setTextSize(20).setTextTypeface(typeface).apply();
                Toast dynamicToast = DynamicToast.make(requireContext(), "Product added in cart", R.drawable.ic_shopping_cart, Color.parseColor("#B8F4FF"),3000);
                dynamicToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                dynamicToast.show();
            }
        });

        btnWish=view.findViewById(R.id.btnWish);
        btnWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products products = new products(title,description,quantity,image,mrp,rate,discount,null,null,null,null,sch,null,null);
                assert current != null;
                databaseReferences.child("Wishlist").child(current).child(title).setValue(products);
                Typeface typeface = Typeface.DEFAULT_BOLD;
                DynamicToast.Config.getInstance().setTextSize(20).setTextTypeface(typeface).apply();
                Toast dynamicToast = DynamicToast.make(requireContext(), "Product added to WIsh list", R.drawable.ic_shopping_cart, Color.parseColor("#B8F4FF"),3000);
                dynamicToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                dynamicToast.show();
            }
        });

        btnBuyNow=view.findViewById(R.id.btnBuyNow);
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        btnBuy = view.findViewById(R.id.btnBN);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = FirebaseDatabase.getInstance().getReference();
                firebaseDatabase.child("Users").child(current.replace("+91","")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("classP").exists()) {
                            String id = String.valueOf(System.currentTimeMillis());
                            String t = total.getText().toString();
                            float quan=Float.parseFloat(t);
                            float r=Float.parseFloat(rate);
                            float finalAmount;

                            if(sch.equals("")) {
                                finalAmount = r * quan;
                            }
                            else {
                                float s = Float.parseFloat(sch);
                                float price = r - ((s * r) / 100);
                                finalAmount = price * quan;
                            }

                            cartProduct products = new cartProduct(title,description,quantity,image,mrp,rate,discount,t,sch);
                            databaseReferences.child("Order").child(id).child("Products").child(title).setValue(products);
                            Intent i = new Intent(getContext(), ConfirmationActivity.class);
                            i.putExtra("id",id);
                            i.putExtra("total",String.valueOf(finalAmount));
                            startActivity(i);
                        }
                        else {
                            String phone = "+918652610419";
                            String message = "Hello, \nThis is *" +dataSnapshot.child("party").getValue(String.class)+
                                    "*\n\nI want more details on :\n\n1. "+title+"\nMRP :"+mrp+"\n"
                                    +Quantity.getText().toString()+"pcs";
                            PackageManager packageManager = requireContext().getPackageManager();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
                                i.setPackage("com.whatsapp");
                                i.setData(Uri.parse(url));
                                if (i.resolveActivity(packageManager) != null) {
                                    startActivity(i);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

//                            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    switch (which){
//                                        case DialogInterface.BUTTON_POSITIVE:
//                                            String phone = "+919323610419";
//                                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
//                                            view.getContext().startActivity(intent);
//                                            break;
//
//                                        case DialogInterface.BUTTON_NEGATIVE:
//                                            dialog.dismiss();
//                                            break;
//                                    }
//                                }
//                            };
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                            builder.setTitle("Account Not Verified");
//                            builder.setMessage("Your account is not verified by the Owner. Hence, due to security reasons you will be unable to use the following facilities :" +
//                                    "\n\n1. You cannot see the rates/discounts" +
//                                    "\n2. You cannot buy our Products" +
//                                    "\n\nWe will be verifying your account soon.").setPositiveButton("Contact Us", dialogClickListener)
//                                    .setNegativeButton("Ok", dialogClickListener).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        progressBar = view.findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);

        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                shareImage(image,getContext(),title,mrp,description,progressBar);
            }
        });
        return view;
    }

    private static void shareImage(String url, final Context context, String title, String mrp, String description, final ProgressBar progressBar) {
        final String message = title+"\n\nM.R.P. :"+mrp+"\n\nDescription : \n"+description+"\n\nPlease download our app to get more information about the Product.";
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                progressBar.setVisibility(View.GONE);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT,message);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));

                context.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private static Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("Users").child(current.replace("+91",""))
                .child("classP").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    rt.setVisibility(View.GONE);
                    sc.setVisibility(View.GONE);
                    tvSave.setVisibility(View.GONE);

                    btnBuyNow.setText("Request Rates");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<products, productsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<products, productsViewHolder>
                (products.class, R.layout.product_item, productsViewHolder.class, reference) {
            @Override
            protected void populateViewHolder(productsViewHolder productsViewHolder, products products, int i) {
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
                productsViewHolder.setBulk(products.getBulk());
            }
        };
        relatedProducts.setAdapter(firebaseRecyclerAdapter);
    }
    public static class productsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String title,desc,Mrp,Rate,Discount,Image,quan,sch,cmp,cat;
        int m,r;
        DatabaseReference firebaseDatabase, check;
        FirebaseAuth firebaseAuth;
        String current;
        Button cart;
        LinearLayout rt;


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public productsViewHolder(final View itemView) {
            super(itemView);
            mView=itemView;

            rt = mView.findViewById(R.id.rate);
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

            cart = mView.findViewById(R.id.btnAddCart);
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseAuth =FirebaseAuth.getInstance();
            current = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

            cart.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onClick(View v) {
                    cartProduct products = new cartProduct(title,desc,quan,Image,Mrp,Rate,Discount,quan,sch);
                    firebaseDatabase.child("Cart").child(current).child(title).setValue(products);

                    Typeface typeface = Typeface.DEFAULT_BOLD;
                    DynamicToast.Config.getInstance().setTextSize(20).setTextTypeface(typeface).apply();
                    Toast dynamicToast = DynamicToast.make(mView.getContext(), "Product added in cart", R.drawable.ic_shopping_cart, Color.parseColor("#B8F4FF"),3000);
                    dynamicToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                    dynamicToast.show();
                }
            });
        }

        public void setImage(String image) {
            Image=image;
            ImageView imageView = mView.findViewById(R.id.imageView);
            Glide.with(mView.getContext()).load(image).into(imageView);
        }

        public void setName(String name) {
            title=name;
            TextView tvPrintTitle = mView.findViewById(R.id.textViewTitle);
            tvPrintTitle.setText(name);
        }
        public void setDescription(String description) {
            desc = description;
        }
        public void setQuantity(String quantity) {
            quan=quantity;
            TextView tvSd = mView.findViewById(R.id.textViewShortDesc);
            tvSd.setText("Pack of "+quantity+" units");
        }
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setMrp(String mrp) {
            Mrp=mrp;
            float m = Float.parseFloat(mrp);
            TextView tvM = mView.findViewById(R.id.textViewMRP);
            tvM.setText("\u20B9 "+String.format("%.2f",m));
        }
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setRate(final String rate) {
            Rate=rate;
            final float m = Float.parseFloat(rate);
            final TextView tvR = mView.findViewById(R.id.textViewPrice);
            firebaseDatabase.child("Users").child(current.replace("+91",""))
                    .child("classP").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        rt.setVisibility(View.VISIBLE);
                        tvR.setText("\u20B9 "+String.format("%.2f",m));
                    }
                    else {
                        rt.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setScheme (final String scheme) {
            sch = scheme;
            final double d = Double.parseDouble(Discount);
            final TextView sch = mView.findViewById(R.id.textScheme);
            final TextView tvR = mView.findViewById(R.id.textViewDiscount);
            firebaseDatabase.child("Users").child(current.replace("+91",""))
                    .child("classP").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if(scheme.length() == 0) {
                            sch.setVisibility(View.GONE);
                            tvR.setText("Retail Margin : "+String.format("%.2f",d)+"%");
                        }
                        else {
                            sch.setVisibility(View.VISIBLE);
                            sch.setText("Scheme : "+scheme+"%");
                            float m = Float.parseFloat(Mrp);
                            float r = Float.parseFloat(Rate);
                            float s = Float.parseFloat(scheme);
                            float nm = r - ((s*r)/100);
                            float dis = 100 - ((nm*100)/m);
                            tvR.setText("Retail Margin + Scheme : "+String.format("%.2f",dis)+"%");
                        } }
                    else {
                        sch.setVisibility(View.GONE);
                        tvR.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setDiscount(String discount) {
            Discount=discount;
        }

        public void setCompany (String company) {
            cmp = company;
        }

        public void setCategory (String category) {
            cat = category;
        }

        public void setBulk (Boolean bulk) {
            ImageView imageView = mView.findViewById(R.id.bulkOrders);
            if(bulk == null) {
                imageView.setVisibility(View.GONE);
            }
            else {
                if(bulk) {
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}