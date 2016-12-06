package edu.kvcc.cis298.cis298assignment4.models;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by doc on 10/23/16.
 */

public class Beverage {
    private String mItemNumber;
    private String mItemDescription;
    private String mPackSize;
    private double mCasePrice;
    private Boolean mActive;

    /**
     * Create a new Beverage model
     * @param itemNumber            item/product/id number
     * @param itemDescription       description/title of the beverage
     * @param packSize              size that the beverage is sold as
     * @param casePrice             unit price per case of beverages
     * @param active                whether the beverage is currently active or not
     */
    public Beverage(String itemNumber, String itemDescription, String packSize, double casePrice, Boolean active) {
        mItemNumber = itemNumber;
        mItemDescription = itemDescription;
        mPackSize = packSize;
        mCasePrice = casePrice;
        mActive = active;
    }

    /**
     * Create a new Beverage model from an ordered array of
     * relevant beverage information
     * @param tokens array of string tokens
     * @return Beverage or null
     */
    @Nullable
    public static Beverage fromTokens(String[] tokens) {
        if (tokens.length == 5) {
            return new Beverage(tokens[0],
                    tokens[1],
                    tokens[2],
                    Double.parseDouble(tokens[3]),
                    Boolean.valueOf(tokens[4]));
        } else {
            Log.d("Beverage", "Bad tokens!");
            return null;
        }
    }

    public Boolean isActive() {
        return mActive;
    }

    public void setActive(Boolean active) {
        mActive = active;
    }

    public double getCasePrice() {
        // Get the price in X.XX format
        return Math.floor(mCasePrice * 100) / 100;
    }

    public void setCasePrice(double casePrice) {
        mCasePrice = casePrice;
    }

    public String getPackSize() {
        return mPackSize;
    }

    public void setPackSize(String packSize) {
        mPackSize = packSize;
    }

    public String getItemDescription() {
        return mItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        mItemDescription = itemDescription;
    }

    public String getItemNumber() {
        return mItemNumber;
    }
}
