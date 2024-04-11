package com.example.banvainhacuong.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.banvainhacuong.Fragments.ManageFragment;
import com.example.banvainhacuong.Fragments.OverviewFragment;
import com.example.banvainhacuong.Fragments.ProductFragment;
import com.example.banvainhacuong.Fragments.SettingFragment;
import com.example.banvainhacuong.MainActivity;

public class FragmentAdapter extends FragmentStateAdapter {
    private MainActivity mainActivity;
    private ProductFragment productFragment;
    private int NumOfTab;
    public FragmentAdapter(FragmentActivity     fa, int _NumOfTab) {
        super(fa);
        NumOfTab = _NumOfTab;
        this.mainActivity = (MainActivity) fa;
    }
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public FragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new OverviewFragment();
            case 1:
                productFragment = new ProductFragment();
                return productFragment;
            case 2:
                return new ManageFragment();
            case 3:
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NumOfTab;
    }

    public ProductFragment getProductFragment() {
        return productFragment;
    }
}