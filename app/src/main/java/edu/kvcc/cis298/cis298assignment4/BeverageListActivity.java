package edu.kvcc.cis298.cis298assignment4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import edu.kvcc.cis298.cis298assignment4.fragments.BeverageListFragment;

/**
 * Created by doc on 11/1/16.
 */

public class BeverageListActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        // if the fragment container is null, fill it with the BeverageListFragment
        if (fragment == null) {
            fragment = new BeverageListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
