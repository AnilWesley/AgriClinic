package com.novaagritech.agriclinic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.HomeActivity1;
import com.novaagritech.agriclinic.activities.VideoMoreActivity;
import com.novaagritech.agriclinic.databinding.FragmentVideosBinding;

public class VideosFragment extends Fragment {


    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentVideosBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_videos, container, false);
        View view = binding.getRoot();

        binding.novaVideos1.setOnClickListener(v -> {

            Intent intent =new Intent(getActivity(), VideoMoreActivity.class);
            intent.putExtra("channelID","UCicsA32B7SO_RUJqzDKbKNA");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });
        final View.OnClickListener onClickListener = v -> {

            Toast.makeText(getActivity(), "Coming Soon....!", Toast.LENGTH_SHORT).show();


        };
        binding.novaVideos2.setOnClickListener(onClickListener);
        binding.novaVideos3.setOnClickListener(onClickListener);
        binding.novaVideos4.setOnClickListener(onClickListener);

        return view;
    }







}