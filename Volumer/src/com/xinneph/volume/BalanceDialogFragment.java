package com.xinneph.volume;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by piotr on 06.07.14.
 */
public class BalanceDialogFragment extends DialogFragment {

    public static final String ARG_BALANCE = "argument_balance";
    private BalanceChangeListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BalanceChangeListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
            + " must implement "+BalanceChangeListener.class.getSimpleName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.change_balance)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.balance_dialog, container, false);
////        EditText et = (EditText) v.findViewById(R.id.editText_balance);
////        int balance = getArguments().getInt(ARG_BALANCE, 2);
////        et.setText(balance);
//        return v;
//    }
}
