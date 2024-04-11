package com.example.banvainhacuong.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.banvainhacuong.Activities.BillManagementActivity;
import com.example.banvainhacuong.Activities.ProductManagementActivity;
import com.example.banvainhacuong.Activities.TableManagementActivity;
import com.example.banvainhacuong.R;


public class ManageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        ImageButton iBtnProductManagement = view.findViewById(R.id.iBtnProductManagement);
        iBtnProductManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo intent để mở ProductManagementActivity
                Intent intent = new Intent(getActivity(), ProductManagementActivity.class);

                // Start the activity
                startActivity(intent);
            }
        });

        ImageButton iBtnTableManage = view.findViewById(R.id.iBtnTableManage);
        iBtnTableManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo intent để mở ProductManagementActivity
                Intent intent = new Intent(getActivity(), TableManagementActivity.class);

                // Start the activity
                startActivity(intent);
            }
        });

        ImageButton iBtnBillManage = view.findViewById(R.id.iBtnBillManage);
        iBtnBillManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo intent để mở ProductManagementActivity
                Intent intent = new Intent(getActivity(), BillManagementActivity.class);

                // Start the activity
                startActivity(intent);
            }
        });


        return view;
    }
}