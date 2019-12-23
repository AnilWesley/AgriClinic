package com.novaagritech.agriclinic.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.databinding.FragmentAboutBinding;
import com.novaagritech.agriclinic.databinding.FragmentHelpBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {


    public HelpFragment() {
        // Required empty public constructor
    }


    private FragmentHelpBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_help, container, false);
        View view = binding.getRoot();

        binding.callUs.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "040-40206238"));
            startActivity(intent);

        });

        binding.submit.setOnClickListener(v -> {

            String yourName1=binding.yourName.getText().toString().trim();
            String yourMobile1=binding.yourMobile.getText().toString().trim();
            String yourDescription1=binding.yourDescription.getText().toString().trim();


            if (!yourName1.isEmpty() && !yourMobile1.isEmpty() && !yourDescription1.isEmpty()) {

           /*     Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "agriclinic.org@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Details : " + "Name : " + yourName1 +"  "+ "Mobile : " + yourMobile1);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "" + yourDescription1);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));*/

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"agriclinic.org@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Contact Details : " + "Name : " + yourName1 +"  "+ "Mobile : " + yourMobile1);
                i.putExtra(Intent.EXTRA_TEXT   , "" + yourDescription1);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(getActivity(), "Enter Details", Toast.LENGTH_SHORT).show();
            }
        });
        binding.infoUs.setOnClickListener(v -> {
            String url = "http://agriclinic.org";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        return view;
    }


}
