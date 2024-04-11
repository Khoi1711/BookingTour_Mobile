package com.example.banvainhacuong.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.banvainhacuong.Models.Product;
import com.example.banvainhacuong.R;

public class ChooseProductDialog extends DialogFragment {
    private Product mProduct;
    private EditText edtAmount;
    private TextView lblProductName;

    public interface OnProductChosenListener {
        void onProductChosen(Product product, int amount);
    }

    private OnProductChosenListener mListener;

    public ChooseProductDialog(Product product, OnProductChosenListener listener) {
        this.mProduct = product;
        this.mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choose_product, null);

        lblProductName = view.findViewById(R.id.lblChooseProductName);
        edtAmount = view.findViewById(R.id.edtAmountOfChooseProduct);

        lblProductName.setText(mProduct.getProductName());
        edtAmount.setText("1");

        builder.setView(view)
                .setTitle("Chọn sản phẩm")
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int amount = Integer.parseInt(edtAmount.getText().toString());
                        mListener.onProductChosen(mProduct, amount);
                    }
                });

        return builder.create();
    }
}

