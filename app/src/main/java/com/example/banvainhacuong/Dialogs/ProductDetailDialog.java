package com.example.banvainhacuong.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Product;
import com.example.banvainhacuong.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailDialog extends DialogFragment {
    private Product mProduct;
    private EditText edtProductName, edtInventory, edtProductPrice;
    private ActivityResultLauncher<String> mGetContent;
    private String newImageName;
    private Spinner spinnerType;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;

    public ProductDetailDialog(Product product) {
        this.mProduct = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_manage_product, null);
        firebaseHelper = new FirebaseHelper("products", getActivity());
        progressBar = view.findViewById(R.id.progressBarDialog);
        progressBar.setVisibility(View.GONE);

        ImageView imgProduct = view.findViewById(R.id.imgProductMangage);
        Glide.with(this)
                .load(mProduct.getProductImage()) // URL của hình ảnh
                .into(imgProduct);

        edtProductName = view.findViewById(R.id.edtProductName);
        edtInventory = view.findViewById(R.id.edtInventory);
        edtProductPrice = view.findViewById(R.id.edtProductPrice);

        edtProductName.setText(mProduct.getProductName());
        edtInventory.setText(String.valueOf(mProduct.getInventory()));
        edtProductPrice.setText(String.valueOf(mProduct.getProductPrice()));
        spinnerType = view.findViewById(R.id.spinnerType);
        setupSpinner(spinnerType);
        ImageView imageView = view.findViewById(R.id.imgProductMangage);
        setupImageView(imageView);


        Button btnUpdate = view.findViewById(R.id.btnSaveProduct);
        setupButtonUpdateProduct(btnUpdate);


        Button btnDeleteProduct = view.findViewById(R.id.btnDeleteProduct);
        setupButtonDeleteProduct(btnDeleteProduct);


        Button btnClose = view.findViewById(R.id.btnCloseDialogProduct);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nhấn button "Đóng", dialog sẽ tự động đóng
                dismiss();
            }
        });


        builder.setView(view).setTitle("Chi tiết sản phẩm");


        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    private void updateProductToFirebase(Product product, String newImageName, String oldImageName) {
        progressBar.setVisibility(View.VISIBLE);;

        if (newImageName.startsWith("https://firebasestorage.googleapis.com/")) {
            firebaseHelper.updateItem(product.getId(), product);
            progressBar.setVisibility(View.GONE);
            dismiss();
        } else {
            firebaseHelper.addImageToFirebase(Uri.parse(newImageName),
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            product.setProductImage(downloadUri.toString());
                            firebaseHelper.updateItem(product.getId(), product);
                            firebaseHelper.deleteImageFromFirebase(oldImageName, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Đã xảy ra lỗi khi xóa hình ảnh cũ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            dismiss();
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi khi tải hình ảnh lên: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
        }
    }
    private void deleteProduct(Product product){
        if (product != null && product.getId() != null) {
            firebaseHelper.deleteItem(product.getId(), product.getProductImage());
            progressBar.setVisibility(View.GONE);
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupButtonUpdateProduct(Button btnUpdateProduct){
        HashMap<String, String> typeMap = new HashMap<>();
        typeMap.put("Đồ ăn", "food");
        typeMap.put("Thức uống", "drink");

        btnUpdateProduct.setText("Cập nhật");
        btnUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String oldImageName = mProduct.getProductImage();
                    mProduct.setProductImage(newImageName);
                    mProduct.setProductName(edtProductName.getText().toString());
                    mProduct.setProductPrice(Integer.parseInt(edtProductPrice.getText().toString()));
                    mProduct.setInventory(Integer.parseInt(edtInventory.getText().toString()));
                    String selectedType = spinnerType.getSelectedItem().toString();
                    mProduct.setType(typeMap.get(selectedType));
                    updateProductToFirebase(mProduct, newImageName, oldImageName);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setupButtonDeleteProduct(Button btnDeleteProduct) {
        btnDeleteProduct.setVisibility(View.VISIBLE);
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(mProduct);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    //Hàm setup Spinner
    private void setupSpinner(Spinner spinner) {
        List<String> categories = new ArrayList<>();
        categories.add("Đồ ăn");
        categories.add("Thức uống");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        int position = categories.indexOf(mProduct.getType().equals("food") ? "Đồ ăn" : "Thức uống");
        spinner.setSelection(position);
    }
    private void setupImageView(ImageView imageView) {
        newImageName = mProduct.getProductImage();
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imageView.setImageURI(uri);
                        newImageName = uri.toString();
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở trình quản lý tập tin hình ảnh
                mGetContent.launch("image/*");
            }
        });
    }
}
