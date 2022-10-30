package com.example.curity.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.curity.R;
import com.example.curity.SignUp.SelectContact;
import com.example.curity.databinding.ActivityHomePageBinding;
import com.example.curity.firstFragment;
import com.example.curity.login.Login;
import com.example.curity.secondFragment;
import com.example.curity.thirdFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityHomePageBinding binding;

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new firstFragment());

        // inserting name at the sidebar
        // Retrieve the data first using firebase realtime
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                String fName = String.valueOf(dataSnapshot.child("firstName").getValue());
                                String lName = String.valueOf(dataSnapshot.child("lastName").getValue());
                                String phone = String.valueOf(dataSnapshot.child("phone").getValue());


                                // getting the header of the side bar
                                View headView = navigationView.getHeaderView(0);
                                TextView setName = headView.findViewById(R.id.userName);
                                TextView setPhoneNum = headView.findViewById(R.id.userNumber);

                                setName.setText(fName +" "+ lName);
                                setPhoneNum.setText(phone);
                            }
                        }
                    }
                });

        // bottom navigation (Home, Map, and Chats)
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.firstFragment:
                    replaceFragment(new firstFragment());
                    break;
                case R.id.secondFragment:
                    replaceFragment(new secondFragment());
                    break;
                case R.id.thirdFragment:
                    replaceFragment(new thirdFragment());
                    break;
            }

            return true;
        });


        /*------------------Hooks------------------*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        /*------------------Tool Bar------------------*/
        setSupportActionBar(toolbar);

        /*------------------Navigation Drawer Menu------------------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            /*----------------Fragments----------------*/
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        new firstFragment()).commit();

                binding.bottomNavigationView.setSelectedItemId(R.id.firstFragment);
                break;

            case R.id.nav_maps:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                    new secondFragment()).commit();

                binding.bottomNavigationView.setSelectedItemId(R.id.secondFragment);
                break;

            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        new thirdFragment()).commit();

                binding.bottomNavigationView.setSelectedItemId(R.id.thirdFragment);
                break;

            /*----------------Activities----------------*/
            case R.id.nav_profile:
                startActivity(new Intent(HomePage.this, profile.class));
                break;

            case R.id.select_contact:
//                startActivity(new Intent(HomePage.this, SelectContact.class));
                Intent intent = new Intent(HomePage.this, SelectContact.class);
                intent.putExtra("Source", "from Homepage");
                startActivity(intent);
                break;

            case R.id.nav_setting:
                startActivity(new Intent(HomePage.this, settings.class));
                break;

            case R.id.nav_logout:
                //logout to the firebase
                FirebaseAuth.getInstance().signOut();

                //going back to the login Page
                startActivity(new Intent(HomePage.this, Login.class));
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}