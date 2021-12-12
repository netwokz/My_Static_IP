package com.netwokz.mystaticip;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditIpDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText mIpEdit;
    private EditText mMacEdit;
    private EditText mTypeEdit;
    private EditText mNameEdit;
    int mType;
    long mRecordId;

    private Button mSaveButton;
    private Button mCancelButton;

    public EditIpDialog() {

    }

    public static EditIpDialog newInstance() {
        return new EditIpDialog();
    }

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null)
            onDismissListener.onDismiss(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRecordId = getArguments().getLong("id");
        StaticIpRecord mRecord = getIpRecord(mRecordId);
        View mView = inflater.inflate(R.layout.new_ip_dialog, container);
        mCancelButton = mView.findViewById(R.id.btn_cancel);
        mSaveButton = mView.findViewById(R.id.btn_save);
        mCancelButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        mIpEdit = mView.findViewById(R.id.et_ip);
        mIpEdit.setText(mRecord.mIpAddress);
        mMacEdit = mView.findViewById(R.id.et_mac);
        mMacEdit.setText(mRecord.mMacAddress);
        mNameEdit = mView.findViewById(R.id.et_name);
        mNameEdit.setText(mRecord.mName);

        mMacEdit.addTextChangedListener(macWatcher);

        Spinner spinner = mView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.device_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(setType(mRecord.mType) - 1);
        spinner.setOnItemSelectedListener(this);
        Log.d("EditIpDialog", "Type: " + setType(mRecord.mType));
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private final TextWatcher macWatcher = new TextWatcher() {
        String mPreviousMac = null;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            String enteredMac = mMacEdit.getText().toString().toUpperCase();
            String cleanMac = clearNonMacCharacters(enteredMac);
            String formattedMac = formatMacAddress(cleanMac);

            int selectionStart = mMacEdit.getSelectionStart();
            formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
            int lengthDiff = formattedMac.length() - enteredMac.length();

            setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
        }

        /**
         * Strips all characters from a string except A-F and 0-9.
         * @param mac       User input string.
         * @return String containing MAC-allowed characters.
         */
        private String clearNonMacCharacters(String mac) {
            return mac.toString().replaceAll("[^A-Fa-f0-9]", "");
        }

        /**
         * Adds a colon character to an unformatted MAC address after
         * every second character (strips full MAC trailing colon)
         * @param cleanMac      Unformatted MAC address.
         * @return Properly formatted MAC address.
         */
        private String formatMacAddress(String cleanMac) {
            int grouppedCharacters = 0;
            String formattedMac = "";

            for (int i = 0; i < cleanMac.length(); ++i) {
                formattedMac += cleanMac.charAt(i);
                ++grouppedCharacters;

                if (grouppedCharacters == 2) {
                    formattedMac += ":";
                    grouppedCharacters = 0;
                }
            }

            // Removes trailing colon for complete MAC address
            if (cleanMac.length() == 12)
                formattedMac = formattedMac.substring(0, formattedMac.length() - 1);

            return formattedMac;
        }

        /**
         * Upon users colon deletion, deletes MAC character preceding deleted colon as well.
         * @param enteredMac            User input MAC.
         * @param formattedMac          Formatted MAC address.
         * @param selectionStart        MAC EditText field cursor position.
         * @return Formatted MAC address.
         */
        private String handleColonDeletion(String enteredMac, String formattedMac, int selectionStart) {
            if (mPreviousMac != null && mPreviousMac.length() > 1) {
                int previousColonCount = colonCount(mPreviousMac);
                int currentColonCount = colonCount(enteredMac);

                if (currentColonCount < previousColonCount) {
                    formattedMac = formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(selectionStart);
                    String cleanMac = clearNonMacCharacters(formattedMac);
                    formattedMac = formatMacAddress(cleanMac);
                }
            }
            return formattedMac;
        }

        /**
         * Gets MAC address current colon count.
         * @param formattedMac      Formatted MAC address.
         * @return Current number of colons in MAC address.
         */
        private int colonCount(String formattedMac) {
            return formattedMac.replaceAll("[^:]", "").length();
        }

        /**
         * Removes TextChange listener, sets MAC EditText field value,
         * sets new cursor position and re-initiates the listener.
         * @param cleanMac          Clean MAC address.
         * @param formattedMac      Formatted MAC address.
         * @param selectionStart    MAC EditText field cursor position.
         * @param lengthDiff        Formatted/Entered MAC number of characters difference.
         */
        private void setMacEdit(String cleanMac, String formattedMac, int selectionStart, int lengthDiff) {
            mMacEdit.removeTextChangedListener(this);
            if (cleanMac.length() <= 12) {
                mMacEdit.setText(formattedMac);
                mMacEdit.setSelection(selectionStart + lengthDiff);
                mPreviousMac = formattedMac;
            } else {
                mMacEdit.setText(mPreviousMac);
                mMacEdit.setSelection(mPreviousMac.length());
            }
            mMacEdit.addTextChangedListener(this);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_save:
                saveRecord();
                dismiss();
                break;
        }
    }

    private void saveRecord() {
        String mIp = mIpEdit.getText().toString();
        String mMac = mMacEdit.getText().toString();
        String mName = mNameEdit.getText().toString();
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        String mDate = simpleDateFormat.format(new Date());

        StaticIpRecord ipRecord = StaticIpRecord.findById(StaticIpRecord.class, mRecordId);
        ipRecord.mIpAddress = mIp;
        ipRecord.mMacAddress = mMac;
        ipRecord.mName = mName;
//        ipRecord.mDateAdded = mDate;
        ipRecord.mType = mType;
        ipRecord.save();
        dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // Desktop
                mType = 1;
                Log.d("EditIpDialog", "Type: Desktop");
                break;
            case 1:
                // Laptop
                mType = 2;
                Log.d("EditIpDialog", "Type: Laptop");
                break;
            case 2:
                // Mobile
                mType = 3;
                Log.d("EditIpDialog", "Type: Mobile");
                break;
            case 3:
                // Raspberry Pi
                mType = 4;
                Log.d("EditIpDialog", "Type: Raspberry Pi");
                break;
            case 4:
                // Other
                mType = 5;
                Log.d("EditIpDialog", "Type: Other");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int setType(int type) {
        if (type == 5)
            type = 4;
        if (type == 6)
            type = 5;
        return type;
    }

    public StaticIpRecord getIpRecord(long id) {
        StaticIpRecord record = StaticIpRecord.findById(StaticIpRecord.class, id);
        return record;
    }
}
