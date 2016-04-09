package com.bignerdranch.android.nerdmail.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bignerdranch.android.nerdmailservice.Email;

public class EmailViewHolder extends RecyclerView.ViewHolder {

    private EmailListItemView mEmailListItemView;

    public EmailViewHolder(View itemView) {
        super(itemView);

        mEmailListItemView = (EmailListItemView) itemView;
    }


    public void bindEmail(Email email) {

        mEmailListItemView.setEmail(email);
    }
}
