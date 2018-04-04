package com.keepcalm.blomo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private BottomNavigationView mainBottomNav;
    private DatabaseReference mDatabase;
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private  String current_user_id;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
      //  mDatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Blomo");

       /* recyclerView = (RecyclerView) findViewById(R.id.blog_list_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); */

       if(mAuth.getCurrentUser()!=null) {

           mainBottomNav = findViewById(R.id.main_bottom_nav);

           //FRAGMENTS
           homeFragment = new HomeFragment();
           notificationFragment = new NotificationFragment();
           accountFragment = new AccountFragment();

           initializeFragment();

           mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                   Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                   switch (item.getItemId()) {

                       case R.id.bottom_action_home:
                           replaceFragment(homeFragment, currentFragment);
                           return true;

                       case R.id.bottom_action_noti:
                           replaceFragment(notificationFragment, currentFragment);
                           return true;

                       case R.id.bottom_action_account:
                           replaceFragment(accountFragment, currentFragment);
                           return true;

                       default:
                           return false;
                   }

               }
           });

       }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //start Firebase 2018

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser== null){

            sendToLogin();

        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                            if(!task.getResult().exists()){

                                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                                finish();
                            }

                        }else{
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "Error : "+errorMsg, Toast.LENGTH_SHORT).show();
                        }

                }
            });

        }

        //End Firebase 2018
    }



  /*  public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);

            mView =itemView;

        }
        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setMessage(String message){
            TextView post_message= (TextView) mView.findViewById(R.id.post_message);
            post_message.setText(message);
        }

        public void setImage(Context context , String image){
            ImageView post_image = (ImageView)mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);

        }
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_item){
            startActivity(new Intent(MainActivity.this, Post.class));
        }
        else if(item.getItemId()==R.id.googleMenu){
            startActivity(new Intent(MainActivity.this, GoogleSignActivity.class));
        }
        else if (item.getItemId()==R.id.loginPage){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        else if(item.getItemId()==R.id.action_logout){
            logOut();
        }
        else if(item.getItemId()==R.id.action_setting){
            startActivity(new Intent(MainActivity.this, AccountActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void initializeFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragment == accountFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);
        }

        if(fragment == notificationFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);
        }

        fragmentTransaction.show(fragment);

      //  fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }
}
