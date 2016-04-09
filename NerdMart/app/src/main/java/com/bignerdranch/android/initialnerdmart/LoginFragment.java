package com.bignerdranch.android.initialnerdmart;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.android.initialnerdmart.databinding.FragmentLoginBinding;

public class LoginFragment extends NerdMartAbstractFragment {

    /*@Bind(R.id.fragment_login_username)
    EditText mUsernameEditText;
    @Bind(R.id.fragment_login_password)
    EditText mPasswordEditText;*/


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_login, container, false);
        //ButterKnife.bind(this, v);

        FragmentLoginBinding fragmentLoginBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login, container, false);
        fragmentLoginBinding.setLoginButtonClickListener(v -> {
            String username = fragmentLoginBinding.fragmentLoginUsername.getText().toString();
            String password = fragmentLoginBinding.fragmentLoginPassword.getText().toString();

            addSubscription(mNerdMartServiceManager
                    .authenticate(username, password)
                    .compose(loadingTransformer())
                    .subscribe(authenticated -> {

                        if (!authenticated) {
                            Toast.makeText(getActivity(), R.string.auth_failure_toast, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(getActivity(), R.string.auth_success_toast, Toast.LENGTH_SHORT).show();
                        Intent intent = ProductsActivity.newIntent(getActivity());
                        getActivity().finish();
                        startActivity(intent);
                    }));
        });

        return fragmentLoginBinding.getRoot();
        //return v;
    }

    /*@OnClick(R.id.fragment_login_login_button)
    public void handleLoginClick() {

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        addSubscription(mNerdMartServiceManager
                .authenticate(username, password)
                .compose(loadingTransformer())
                .subscribe(authenticated -> {
                    Toast.makeText(getActivity(), R.string.auth_success_toast, Toast.LENGTH_SHORT).show();
                    Intent intent = ProductsActivity.newIntent(getActivity());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
        );
    }*/
}
