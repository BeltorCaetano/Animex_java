package beltor.caetano.animex_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import beltor.caetano.animex_java.Fragments.ChatListFragment;
import beltor.caetano.animex_java.Fragments.GroupChatsFragment;
import beltor.caetano.animex_java.Fragments.HomeFragment;
import beltor.caetano.animex_java.Fragments.NotificationsFragment;
import beltor.caetano.animex_java.Fragments.ProfileFragment;
import beltor.caetano.animex_java.Fragments.UsersFragment;
import beltor.caetano.animex_java.notifications.Token;
//firebase auth

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    BottomNavigationView navigationView;
    ActionBar actionBar;
    String myUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


            actionBar = getSupportActionBar();
            actionBar.setTitle("Profile");
            //home fragment transaction(defailt, on start)
            actionBar.setTitle("Home");




        //init
        firebaseAuth = FirebaseAuth.getInstance();

         navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
      //  navigationView.setSelectedItemId(R.id.nav_home);

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, homeFragment,"")
                .commit();

        checkUserStatus();


    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(myUID).setValue(mToken);
    }


    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
           (item) -> {
                   //handle clicks
                    if (item.getItemId() == R.id.nav_home) {
                        actionBar.setTitle("Home");
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, homeFragment,"")
                                .commit();
                        return true;
                    } else if (item.getItemId() == R.id.nav_profile) {
                        actionBar.setTitle("Profile");
                        ProfileFragment profileFragment = new ProfileFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, profileFragment,"")
                                .commit();
                        return true;
                    } else if (item.getItemId() == R.id.nav_users) {
                        actionBar.setTitle("users");
                        UsersFragment usersFragment = new UsersFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, usersFragment,"")
                                .commit();
                        return true;
                    }else if (item.getItemId() == R.id.nav_chat) {
                        actionBar.setTitle("Chats");
                        ChatListFragment chatListFragment = new ChatListFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, chatListFragment, "")
                                .commit();
                        return true;
                    }else if (item.getItemId() == R.id.nav_more) {
                        showMoreOptions();
                        return true;
                    }
                    return  false;
                };

    private void showMoreOptions() {
        //poup menu to show more options
        PopupMenu popupMenu = new PopupMenu(this, navigationView, Gravity.END);
        //items to show in menu
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Notifications");
        popupMenu.getMenu().add(Menu.NONE, 1, 0, "Group Chats");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0){
                    actionBar.setTitle("Notifications");
                    NotificationsFragment notificationsFragment = new NotificationsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content, notificationsFragment, "")
                            .commit();
                } else if (id==1) {
                    //group chats clicked
                    actionBar.setTitle("Group chats");
                     GroupChatsFragment groupChatsFragment = new GroupChatsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content, groupChatsFragment, "")
                            .commit();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged in user
          //  mProfileTv.setText(user.getEmail());
            myUID = user.getUid();
            //save uid of currently signed in user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", myUID);
            editor.apply();
            FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful()){
                                        String token = task.getResult();
                                        updateToken(token);
                                    }
                                    else {
                                        Log.e("TAG", "Error getting FCM token: " + task.getException());
                                    }
                                }
                            });
            //updateToken( FirebaseMessaging.getInstance().getToken().getResult());
        }
        else {
            //user not signed in, go to main activity
            startActivity((new Intent(DashboardActivity.this, MainActivity_Java.class)));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }
}