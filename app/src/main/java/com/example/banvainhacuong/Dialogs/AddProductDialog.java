package com.example.banvainhacuong.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
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

import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Product;
import com.example.banvainhacuong.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddProductDialog extends DialogFragment {

    private Uri mImageUri;
    private ActivityResultLauncher<String> mGetContent;
    private EditText edtProductName, edtInventory, edtProductPrice;
    Spinner spinnerType;
    private ProgressBar progressBar;
    FirebaseHelper firebaseHelper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_manage_product, null);
        firebaseHelper = new FirebaseHelper("products", getActivity());
        progressBar = view.findViewById(R.id.progressBarDialog);
        progressBar.setVisibility(View.GONE);
        ImageView imageView = view.findViewById(R.id.imgProductMangage);
        setupImageView(imageView);
        Button btnClose = view.findViewById(R.id.btnCloseDialogProduct);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        edtProductName = view.findViewById(R.id.edtProductName);
        edtInventory = view.findViewById(R.id.edtInventory);
        edtProductPrice = view.findViewById(R.id.edtProductPrice);
        spinnerType = view.findViewById(R.id.spinnerType);
        setupSpinner(spinnerType);
        Button btnAddProduct = view.findViewById(R.id.btnSaveProduct);
        setupButtonAddProduct(btnAddProduct);
        builder.setView(view).setTitle("Thêm sản phẩm");
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }
    private void addProductToFirebase(Product product, Uri imageUri) {
       try {
           progressBar.setVisibility(View.VISIBLE);
           firebaseHelper.addImageToFirebase(imageUri,
                   new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri downloadUri) {
                           product.setProductImage(downloadUri.toString());
                           String id = firebaseHelper.myRef.push().getKey();
                           product.setId(id);
                           firebaseHelper.addItem(product, id);
                           progressBar.setVisibility(View.GONE);
                           dismiss();
                       }
                   },
                   new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception exception) {
                           Toast.makeText(getActivity(), "Đã xảy ra lỗi khi tải lên hình ảnh", Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.GONE);
                       }
                   }
           );
       } catch (Exception e){
           Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
       }
    }
    private void setupSpinner(Spinner spinner) {
        List<String> categories = new ArrayList<>();
        categories.add("Đồ ăn");
        categories.add("Thức uống");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void setupImageView(ImageView imageView) {
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imageView.setImageURI(uri);
                        mImageUri = uri;
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
    }
    private void setupButtonAddProduct(Button btnAddProduct){
        HashMap<String, String> typeMap = new HashMap<>();
        typeMap.put("Đồ ăn", "food");
        typeMap.put("Thức uống", "drink");

        btnAddProduct.setText("Thêm");
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product();
                product.setProductImage(mImageUri.toString());
                product.setProductName(edtProductName.getText().toString());
                product.setProductPrice(Integer.parseInt(edtProductPrice.getText().toString()));
                product.setInventory(Integer.parseInt(edtInventory.getText().toString()));
                String selectedType = spinnerType.getSelectedItem().toString();
                product.setType(typeMap.get(selectedType));
                addProductToFirebase(product, mImageUri);
            }
        });
    }

}

