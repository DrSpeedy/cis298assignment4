package edu.kvcc.cis298.cis298assignment4.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
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

    public void reloadBeverages() {
        mBusy = true;
        final AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://barnesbrothers.homeserver.com/beverageapi/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(TAG, "onSuccess()");
                String jsonString = new String(responseBody);
                Log.i(TAG, jsonString);
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String itemNumber = jsonObject.getString("id");
                        String description = jsonObject.getString("name");
                        String pack = jsonObject.getString("pack");
                        double price = jsonObject.getDouble("price");
                        boolean active = jsonObject.getInt("isActive") != 0;

                        Beverage beverage = new Beverage(itemNumber, description, pack, price, active);
                        put(itemNumber, beverage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mBusy = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v(TAG, "onFailure()");
                mBusy = false;
            }
        });
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
