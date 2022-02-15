package com.damian.custonote.ui.labels;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.damian.custonote.R;
import com.damian.custonote.databinding.FragmentLabelsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LabelsFragment extends Fragment {

    private LabelsViewModel labelsViewModel;
    private FragmentLabelsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        labelsViewModel = new ViewModelProvider(this).get(LabelsViewModel.class);
        binding = FragmentLabelsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FloatingActionButton fabAddLabel = getActivity().findViewById(R.id.fabAdd);
        final TextView textView = binding.textLabels;

        labelsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        /*((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });*/


        fabAddLabel.setImageResource(R.drawable.ic_add_label);
        fabAddLabel.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Labels", Toast.LENGTH_LONG).show();
        });

        return root;
    }

    public void displayImage(Context context, Object path, ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource((Integer)path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}