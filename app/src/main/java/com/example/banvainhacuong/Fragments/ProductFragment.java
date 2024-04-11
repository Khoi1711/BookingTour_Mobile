package com.example.banvainhacuong.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Activities.ProductManagementActivity;
import com.example.banvainhacuong.Adapters.ProductAdapter;
import com.example.banvainhacuong.MainActivity;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Product;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.Query;



public class ProductFragment extends Fragment {

    private FirebaseHelper firebaseHelper;
    private ProductAdapter adapter;
    private ProgressBar progressBar;

    private OnAdapterUpdatedListener mListener;

    // Tạo một interface
    public interface OnAdapterUpdatedListener {
        void onAdapterUpdated();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAdapterUpdatedListener) {
            mListener = (OnAdapterUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAdapterUpdatedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        firebaseHelper = new FirebaseHelper("products", getActivity());
        progressBar = view.findViewById(R.id.progressBarProduct);
        RecyclerView recyclerView = view.findViewById(R.id.productRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(firebaseHelper.myRef, Product.class)
                .build();
        adapter = new ProductAdapter(options);
        if (getActivity() instanceof MainActivity) {
            adapter.setOnItemClickListener((MainActivity) getActivity());
        } else if (getActivity() instanceof ProductManagementActivity) {
            adapter.setOnItemClickListener((ProductManagementActivity) getActivity());
        }
        recyclerView.setAdapter(adapter);

        registerDataObserver();

        SearchView searchView = view.findViewById(R.id.searchViewProduct);
        setupSearchView(searchView);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutProduct);
        setupTabLayout(tabLayout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void registerDataObserver() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query newQuery = firebaseHelper.myRef.orderByChild("productName").startAt(newText).endAt(newText + "\uf8ff");
                FirebaseRecyclerOptions<Product> newOptions = new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(newQuery, Product.class)
                        .build();
                adapter.updateOptions(newOptions);
                if (mListener != null) {
                    mListener.onAdapterUpdated();
                }
                return false;
            }
        });
    }
    private void setupTabLayout(TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                progressBar.setVisibility(View.VISIBLE);
                Query newQuery;
                switch (tab.getPosition()) {
                    case 0:
                        newQuery = firebaseHelper.myRef;
                        break;
                    case 1:
                        newQuery = firebaseHelper.myRef.orderByChild("type").equalTo("drink");
                        break;
                    case 2:
                        newQuery = firebaseHelper.myRef.orderByChild("type").equalTo("food");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected tab position: " + tab.getPosition());
                }
                FirebaseRecyclerOptions<Product> newOptions = new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(newQuery, Product.class)
                        .build();
                adapter.updateOptions(newOptions);
                if (mListener != null) {
                    mListener.onAdapterUpdated();
                }
                registerDataObserver();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    public ProductAdapter getAdapter(){
        return adapter;
    }

}

