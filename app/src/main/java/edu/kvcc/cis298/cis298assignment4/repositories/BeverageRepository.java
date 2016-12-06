package edu.kvcc.cis298.cis298assignment4.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.kvcc.cis298.cis298assignment4.R;
import edu.kvcc.cis298.cis298assignment4.models.Beverage;

/**
 * Created by doc on 11/6/16.
 */

public class BeverageRepository extends AbstractBeverageRepository {

    // Log tag
    private static final String TAG = BeverageRepository.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static BeverageRepository mBeverageRepository;

    private Map<String, Beverage> mBeverageMap;

    /**
     * Get the singleton instance of this BeverageRepository
     * @param context Hosting activity context
     * @return BeverageRepository
     */
    public static BeverageRepository getInstance(Context context) {
        if (mBeverageRepository == null) {
            mBeverageRepository = new BeverageRepository(context);
        }
        return mBeverageRepository;
    }

    /**
     * Create a new instance of BeverageRepository,
     * and load in all the data we need from the data source
     * @param context
     */
    private BeverageRepository(Context context) {
        super(context);
        mBeverageMap = new HashMap<>();
        reloadBeverages();
    }

    /**
     * Read in tokens and build beverage models from them to store
     * in the repository's mapped collection
     */
    public void reloadBeverages() {
        Resources resources = getContext().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.beverage_list);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] tokens = line.split(",");
                    Beverage beverage = Beverage.fromTokens(tokens);
                    if (beverage != null) {
                        put(beverage.getItemNumber(), beverage);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Beverage get(String id) {
        return mBeverageMap.get(id);
    }

    @Override
    public Map<String, Beverage> getAll() {
        return mBeverageMap;
    }

    @Override
    public void put(String id, Beverage beverage) {
        mBeverageMap.put(id, beverage);
    }

    @Override
    public void putAll(Map<String, Beverage> map) {
        mBeverageMap.putAll(map);
    }

    @Override
    public void delete(String id) {
        mBeverageMap.remove(id);
    }

    @Override
    public int size() {
        return mBeverageMap.size();
    }
}
