package com.pkg.mychatapplication;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return  requestsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;
            case 2:
                friendsFragment friendsFragment = new friendsFragment();
                return  friendsFragment;

                default:
                    return null;

        }
    }

    @Override
    public int getCount() {
        return 3;   //number of tabs I have
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {    //Set title for each tab
        switch (position){
            case 0:
                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";

                default:
                    return null;
        }

    }
}
