package edu.kvcc.cis298.cis298assignment4.repositories;

import android.content.Context;

import edu.kvcc.cis298.cis298assignment4.models.Beverage;

/**
 * Created by doc on 11/6/16.
 */

public abstract class AbstractBeverageRepository implements MappedRepository<String, Beverage> {
    private Context mContext;
    protected boolean mBusy;

    AbstractBeverageRepository(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    public abstract void reloadBeverages();

    public boolean isBusy() {
        return mBusy;
    }
}
