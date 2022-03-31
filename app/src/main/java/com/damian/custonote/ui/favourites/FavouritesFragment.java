package com.damian.custonote.ui.favourites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.custonote.R;
import com.damian.custonote.databinding.FragmentFavouritesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FavouritesFragment extends Fragment {
    private FavouritesViewModel favouritesViewModel;
    private FragmentFavouritesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        favouritesViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FloatingActionButton fabAddFavourite = getActivity().findViewById(R.id.fabAdd);
        final TextView textView = binding.textFavourites;
        /*favouritesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton fabAddFavourite = getActivity().findViewById(R.id.fabAdd);
        fabAddFavourite.setImageResource(R.drawable.ic_add_favourite);
        fabAddFavourite.setOnClickListener(v -> Toast.makeText(getActivity(), "Favourites", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}