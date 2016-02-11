package com.studioidan.skarim.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.studioidan.skarim.data.DataStore;

/**
 * Created by PopApp_laptop on 21/01/2016.
 */
public class BaseActivity extends AppCompatActivity {
    protected DataStore ds = DataStore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    /**
     * Quick fragment replace transaction
     */
    public void replaceFragment(int containerId, Fragment fragment, String tag) {
        replaceFragment(containerId, fragment, tag, false, null);
    }

    /**
     * Quick fragment replace transaction
     */
    public void replaceFragment(int containerId, Fragment fragment, String tag, boolean addToBackStack, String backStackTag) {
        replaceFragment(containerId, fragment, tag, addToBackStack, backStackTag, -1, -1, -1, -1);
    }

    /**
     * Quick fragment replace transaction
     */
    public void replaceFragment(int containerId, Fragment fragment, String tag, boolean addToBackStack, String backStackTag, int enter, int exit, int popEnter, int popExit) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (enter != -1 && exit != -1) {
            if (popEnter != -1 && popExit != -1) {
                fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
            } else {
                fragmentTransaction.setCustomAnimations(enter, exit);
            }
        }

        fragmentTransaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(backStackTag);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }


}
