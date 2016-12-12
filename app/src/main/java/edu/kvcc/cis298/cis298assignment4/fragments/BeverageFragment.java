package edu.kvcc.cis298.cis298assignment4.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import edu.kvcc.cis298.cis298assignment4.R;
import edu.kvcc.cis298.cis298assignment4.models.Beverage;
import edu.kvcc.cis298.cis298assignment4.repositories.AbstractBeverageRepository;
import edu.kvcc.cis298.cis298assignment4.repositories.BeverageRepository;

/**
 * Created by doc on 10/24/16.
 */

public class BeverageFragment extends Fragment {

    // Log tag
    private static final String TAG = BeverageFragment.class.getSimpleName();
    // Key to fetch the itemNumber option from the bundled arguments
    private static final String ARG_ITEM_NUMBER = "item_number";

    // Request codes of on activity result
    private static final int REQUEST_CONTACT = 0;

    // Repository instance that holds all beverage information
    private AbstractBeverageRepository mBeverageRepository;

    // Beverage model
    private Beverage mBeverage;
    private Button mSelectContactButton;
    private Button mSendDetailsButton;

    private String mContactName;
    private String mContactEmail;

    /**
     * Create a new beverage fragment
     * @param itemNumber item number of the beverage we'd like to edit
     * @return fragment
     */
    public static BeverageFragment newInstance(String itemNumber) {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_NUMBER, itemNumber);

        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialize the fragments member fields
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the singleton instance of the beverage repository
        mBeverageRepository = BeverageRepository.getInstance(getActivity());
        // Get the item number we use for an index from the fragment arguments
        String itemNumber = getArguments().getString(ARG_ITEM_NUMBER);

        // Get the respective beverage from the repository
        mBeverage = mBeverageRepository.get(itemNumber);
    }

    /**
     * Inflate the fragment's view, get relevant widget handles, and set listeners on them
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        EditText beverageTitle = (EditText) view.findViewById(R.id.fragment_beverage_edit_name);
        EditText beverageId = (EditText) view.findViewById(R.id.fragment_beverage_edit_id);
        EditText editPack = (EditText) view.findViewById(R.id.fragment_beverage_edit_pack);
        EditText editPrice = (EditText) view.findViewById(R.id.fragment_beverage_edit_price);
        CheckBox checkBoxActive = (CheckBox) view.findViewById(R.id.fragment_beverage_checkbox_active);

        beverageTitle.addTextChangedListener(new NameChangedListener());
        editPack.addTextChangedListener(new PackChangedListener());
        editPrice.addTextChangedListener(new PriceChangedListener());
        checkBoxActive.setOnCheckedChangeListener(new ActiveBeverageListener());

        beverageTitle.setText(mBeverage.getItemDescription());
        beverageId.setText(mBeverage.getItemNumber());
        editPack.setText(mBeverage.getPackSize());
        editPrice.setText(String.valueOf(mBeverage.getCasePrice()));
        checkBoxActive.setChecked(mBeverage.isActive());

        // Allow the user to select a contact from their phone
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSelectContactButton = (Button) view.findViewById(R.id.fragment_beverage_select_contact_button);
        mSelectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        // If there is no default app to pick contacts, disable the button
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSelectContactButton.setEnabled(false);
        }

        mSendDetailsButton = (Button) view.findViewById(R.id.fragment_beverage_send_email);
        mSendDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send an email to the selected contact
                Uri uri = Uri.fromParts("mailto", mContactEmail == null ? "no@email.provided" : mContactEmail, null);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

                intent.putExtra(Intent.EXTRA_TEXT, getBeverageReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.beverage_report_subject));

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the result is not ok, return
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            // List out only the DisplayName field
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
                    //ContactsContract.CommonDataKinds.Email.ADDRESS
            };
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                // If there are no results, return
                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();

                // Using the same indices as from queryFields
                mContactName = cursor.getString(0);
                //mContactEmail = cursor.getString(1);

                mSelectContactButton.setText(mContactName);

            } finally {
                cursor.close();
            }
        }
    }

    private String getBeverageReport() {

        StringBuilder builder = new StringBuilder();
        builder.append(mContactName)
                .append(",\n\n")
                .append("Please review the following beverage.\n\n")
                .append(mBeverage.getItemNumber() + "\n")
                .append(mBeverage.getItemDescription() + "\n")
                .append(mBeverage.getPackSize() + "\n")
                .append(mBeverage.getCasePrice() + "\n")
                .append(mBeverage.isActive() ? "Active" : "Not Active");

        return builder.toString();
    }

    /**
     * Listen for changes in the user's input and update the model accordingly
     */
    private class NameChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing...
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.i(TAG, "NameChangedListener => onTextChanged");
            mBeverage.setItemDescription(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class PriceChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing...
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBeverage.setCasePrice(Double.parseDouble(s.toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do nothing...
        }
    }

    private class PackChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing...
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBeverage.setPackSize(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do nothing...
        }
    }

    private class ActiveBeverageListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mBeverage.setActive(isChecked);
        }
    }
}
