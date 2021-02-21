package com.ugur.mychatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Fragments.ChatsFragment;
import com.ugur.mychatapp.Fragments.FriendRequestsFragment;
import com.ugur.mychatapp.Fragments.FriendsFragment;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ViewPager viewPager;
    TabLayout tabLayout;

    ChatsFragment chatsFragment;
    FriendsFragment friendsFragment;
    FriendRequestsFragment friendRequestsFragment;
    static boolean worked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        chatsFragment = new ChatsFragment();
        friendsFragment = new FriendsFragment();
        friendRequestsFragment = new FriendRequestsFragment();
        tabLayout.setupWithViewPager(viewPager);

        setFragments();

        try {
            getCall();
        } catch (Exception e) {
        }
    }

    public void setFragments() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addFragment(chatsFragment, "CHATS");
        viewPagerAdapter.addFragment(friendsFragment, "FRIENDS");
        viewPagerAdapter.addFragment(friendRequestsFragment, "REQUESTS");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                return true;
            case R.id.profile:
                System.out.println("PORFILE");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }

    public void getCall() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(User.getInstance().getUuid()).child("Calls");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (worked == false) {
                        System.out.println("GETCALL");
                        answerCall();
                        worked = true;
                    } else {
                        System.out.println("NO GET CALL");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void answerCall() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(User.getInstance().getUuid()).child("Calls");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("ANSWER CALL");
                Intent intent = new Intent(getApplicationContext(), AudioCallActivity.class);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String callerImageUri = ds.child("CallerImageUri").getValue(String.class);
                    String callerId = ds.getKey();
                    String callId = ds.child("CallId").getValue(String.class);
                    String callerName = ds.child("CallerName").getValue(String.class);
                    System.out.println("BURASI ÖNEMLİ... callID: " + callId + " callerName: " + callerName + "  caller Image Uri: " + callerImageUri);
                    intent.putExtra("pairImageUri", callerImageUri);
                    intent.putExtra("pairId", callerId);
                    intent.putExtra("pairName", callerName);
                    intent.putExtra("callId", callId);
                }

                try {
                    Thread.sleep(200);
                    System.out.println("Audio Call Activity'ye Gidiyom");
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}