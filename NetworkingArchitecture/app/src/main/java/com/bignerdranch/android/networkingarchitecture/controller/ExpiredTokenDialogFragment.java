package com.bignerdranch.android.networkingarchitecture.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.bignerdranch.android.networkingarchitecture.R;

public class ExpiredTokenDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.expired_token_dialog_title)
                .setMessage(R.string.expired_token_dialog_message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                Intent intent = new Intent(getActivity(),
                                        AuthenticationActivity.class);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}