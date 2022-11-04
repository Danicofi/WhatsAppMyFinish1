package com.dan.whatsappmy.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.adapters.ViewPagerAdapter;
import com.dan.whatsappmy.fragments.ChatsFragment;
import com.dan.whatsappmy.fragments.ContactsFragment;
import com.dan.whatsappmy.fragments.PhotoFragment;
import com.dan.whatsappmy.fragments.StatusFragment;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.AppBackgroundHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class HomeW extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    //FirebaseAuth authHome;
    UsersProvider usersProvider;
    MaterialSearchBar mSearchBar;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    AuthProvider authHome;

    ChatsFragment mChatsFragment;
    ContactsFragment mContactsFragment;
    StatusFragment mStatusFragment;
    PhotoFragment mPhotoFragment;

    int tabSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_w);

        mSearchBar = findViewById(R.id.searchBar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);

        mViewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mChatsFragment = new ChatsFragment();
        mContactsFragment = new ContactsFragment();
        mStatusFragment = new StatusFragment();
        mPhotoFragment = new PhotoFragment();

        usersProvider = new UsersProvider();

        adapter.addFragment(mPhotoFragment,"");
        adapter.addFragment(mChatsFragment, "CHATS");
        adapter.addFragment(mStatusFragment, "STATUS");
        adapter.addFragment(mContactsFragment, "CONTACTS");

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(tabSelected);

        setupTabIcon();

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.menu);

        authHome = new AuthProvider();
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemSignOut) {
                    signOut();
                }
                else if (item.getItemId() == R.id.itemProfile) {
                    goToProfile();
                }else if (item.getItemId() == R.id.itemAdd) {
                    goToAddMultiUsers();
                }
                return true;
            }
        });
        createToken();
    }

    private void goToAddMultiUsers() {
        Intent i = new Intent(HomeW.this, AddMultiUser.class);
        startActivity(i);
    }

    private void createToken() {
        usersProvider.createToken(authHome.getIdAut());
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackgroundHelper.online(HomeW.this, true);

    }

    @Override
    protected void onStop() {
        super.onStop();
            AppBackgroundHelper.online(HomeW.this, false);
    }


    private void goToProfile() {
        Intent i = new Intent(HomeW.this,Profile.class);
        startActivity(i);
    }

    private void setupTabIcon() {
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        LinearLayout linearLayout = ((LinearLayout)((LinearLayout) mTabLayout.getChildAt(0)).getChildAt(0) );
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight = 0.5f;
        linearLayout.setLayoutParams(layoutParams);
    }

    private void signOut(){
        AppBackgroundHelper.online(HomeW.this, false);
        authHome.signOut();
        Intent i = new Intent (HomeW.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }


}