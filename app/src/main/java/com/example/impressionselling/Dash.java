package com.example.impressionselling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.impressionselling.rmo.rmoActivity;
import com.example.impressionselling.ui.cart.CartFragment;
import com.example.impressionselling.ui.home.HomeFragment;
import com.example.impressionselling.ui.orders.OrderFragment;
import com.example.impressionselling.ui.profile.ProfileFragment;
import com.example.impressionselling.ui.search.SearchFragment;
import com.example.impressionselling.ui.wish.WishList;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    TextView tvDashName, tvDashType;
    FirebaseAuth firebaseAuth;
    DatabaseReference dataref;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        dataref = FirebaseDatabase.getInstance().getReference("Users");

        NavigationView navigationView = findViewById(R.id.nav_view);
        final View headerView = navigationView.getHeaderView(0);

        tvDashName = headerView.findViewById(R.id.UpName);
        tvDashType = headerView.findViewById(R.id.UpMobile);

        email = firebaseAuth.getCurrentUser().getPhoneNumber();
        String ph = email.replace("+91", "");
        tvDashType.setText(ph);

        dataref.child(ph).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                tvDashName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Dash.this, "Invalid Login", Toast.LENGTH_SHORT).show();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash, menu);

        MenuItem menuItem, search;
        menuItem = menu.findItem(R.id.action_cart);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new CartFragment()).addToBackStack(null).commit();
                return false;
            }
        });

        search = menu.findItem(R.id.action_search);
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new SearchFragment()).addToBackStack(null).commit();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new OrderFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new ProfileFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_setting:
                Intent k = new Intent(Dash.this, MainActivity.class);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(k);
                finish();
                break;

            case R.id.nav_call:
                String phone = "+919323610419";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
                break;


            case R.id.nav_send:
                Intent j = new Intent(Intent.ACTION_VIEW);
                j.setData(Uri.parse("http://" + "wa.me/+919323610419"));
                startActivity(j);
                break;

            case R.id.nav_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new CartFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_wish:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new WishList()).addToBackStack(null).commit();
                break;

            case R.id.nav_help:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdn4-6SH0UadYXJ82iXW7VGIOMkniyOyCJnS04yGfk8XV5H8w/viewform"));
                startActivity(i);
                break;

            case R.id.nav_about:
                Intent link = new Intent(Intent.ACTION_VIEW);
                link.setData(Uri.parse("https://k-p-software-developers.business.site"));
                startActivity(link);

            case R.id.nav_service :
                Intent a = new Intent(Dash.this, rmoActivity.class);
                startActivity(a);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
