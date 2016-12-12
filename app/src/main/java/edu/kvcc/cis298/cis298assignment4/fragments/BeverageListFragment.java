package edu.kvcc.cis298.cis298assignment4.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import edu.kvcc.cis298.cis298assignment4.R;
import edu.kvcc.cis298.cis298assignment4.WinePagerActivity;
import edu.kvcc.cis298.cis298assignment4.models.Beverage;
import edu.kvcc.cis298.cis298assignment4.repositories.AbstractBeverageRepository;
import edu.kvcc.cis298.cis298assignment4.repositories.BeverageRepository;

/**
 * Created by doc on 11/1/16.
 */

public class BeverageListFragment extends Fragment {

    // Log tag
    private static final String TAG = BeverageListFragment.class.getSimpleName();

    // RecyclerView to display all of our beverage fragments
    private RecyclerView mBeverageRecyclerView;
    // Adapter to allow our RecyclerView and ViewHolder to communicate
    // with each other
    private BeverageAdapter mBeverageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflate the beverage recycler view and set it's layout manager
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beverage_list, container, false);

        mBeverageRecyclerView = (RecyclerView) view.findViewById(R.id.beverage_recycler_view);
        mBeverageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    /**
     * Update the information displayed by the RecyclerView
     */
    private void updateUI() {
        AbstractBeverageRepository repository = BeverageRepository.getInstance(getActivity());

        // Wait for the repository to load data...
        Log.i(TAG, "Sleeping...");

        // In a pinch. I'm sure there is a much better way to wait for the repo
        // but I simply do not have time to keep messing with it.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Waking...");

        ArrayList<Beverage> beverages = new ArrayList<>(repository.getAll().values());
        Log.i(TAG, beverages.size() + " Beverages...");
        if (mBeverageAdapter == null) {
            mBeverageAdapter = new BeverageAdapter(beverages);
            mBeverageRecyclerView.setAdapter(mBeverageAdapter);
        } else {
            mBeverageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Update the UI when the fragment is resumed
     */
    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        updateUI();
        super.onResume();
    }

    private class BeverageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Beverage mBeverage;
        private TextView mTitleTextView;
        private TextView mItemNumberTextView;
        private TextView mPriceTextView;

        /**
         * Create a new ViewHolder instance to hold our beverage fragments
         * so that they may be used by the RecyclerView
         * @param itemView list item entry
         */
        public BeverageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.beverage_item_title);
            mItemNumberTextView = (TextView) itemView.findViewById(R.id.beverage_item_number);
            mPriceTextView = (TextView) itemView.findViewById(R.id.beverage_item_price);
        }

        /**
         * Bind a beverage model to the view holder and then
         * update the UI fields with relevant information
         * @param beverage Beverage model
         */
        public void bindBeverage(Beverage beverage) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String price = formatter.format(beverage.getCasePrice());

            mBeverage = beverage;
            mTitleTextView.setText(mBeverage.getItemDescription());
            mItemNumberTextView.setText(String.valueOf(mBeverage.getItemNumber()));
            mPriceTextView.setText(price);
        }

        /**
         * When an item on the RecyclerView is clicked, we create a new beverage fragment
         * with the bound (to this) beverage model
         * @param v view
         */
        @Override
        public void onClick(View v) {
            Log.i("BeverageListFragment", "onClick()");
            Intent intent = WinePagerActivity.newIntent(getActivity(), mBeverage.getItemNumber());
            startActivity(intent);
        }
    }

    private class BeverageAdapter extends RecyclerView.Adapter<BeverageHolder> {
        private List<Beverage> mBeverages;

        /**
         * Create a new adapter to allow for communication between the RecyclerView
         * and its related view holder instances
         * @param beverages list of beverage models
         */
        public BeverageAdapter(List<Beverage> beverages) {
            mBeverages = beverages;
        }

        /**
         * When a new beverage holder is needed, we must inflate it's layout and
         * create a new view so that the holder may be used to display the beverage model
         * in the RecyclerView
         * @param parent
         * @param viewType
         * @return BeverageHolder
         */
        @Override
        public BeverageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            View view = layoutInflater.inflate(R.layout.list_item_beverage, parent, false);

            return new BeverageHolder(view);
        }

        /**
         * Get the relevant beverage model and bind it to the beverage view holder
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(BeverageHolder holder, int position) {
            Beverage beverage = mBeverages.get(position);

            holder.bindBeverage(beverage);
        }

        /**
         * Get the number of items the adapter is keeping track of
         * @return int
         */
        @Override
        public int getItemCount() {
            return mBeverages.size();
        }
    }
}
