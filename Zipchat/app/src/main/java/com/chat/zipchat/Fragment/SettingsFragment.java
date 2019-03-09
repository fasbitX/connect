package com.chat.zipchat.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.zipchat.R;

import static com.chat.zipchat.Common.BaseClass.logout_User;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    TextView mDeleteAccount;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mDeleteAccount = view.findViewById(R.id.mDeleteAccount);
        mDeleteAccount.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.mDeleteAccount:
//                logout_User(getActivity());
                break;
        }
    }
}
