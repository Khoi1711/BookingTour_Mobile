package com.example.banvainhacuong.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Adapters.CartAdapter;
import com.example.banvainhacuong.Models.Bill;
import com.example.banvainhacuong.Models.Cart;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BillDetailDialog extends DialogFragment {

    private RecyclerView recyclerViewBillDetail;
    private CartAdapter cartAdapter;
    private List<Cart> listCart;
    private Bill bill;
    private TextView lblSelectedTable;

    public BillDetailDialog(List<Cart> listCart, Bill bill) {
        this.listCart = listCart;
        this.bill = bill;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_cart_detail, null);

        recyclerViewBillDetail = view.findViewById(R.id.recyclerViewCartDetail);
        recyclerViewBillDetail.setLayoutManager(new LinearLayoutManager(getContext()));

        cartAdapter = new CartAdapter(listCart);
        recyclerViewBillDetail.setAdapter(cartAdapter);

        lblSelectedTable = view.findViewById(R.id.lblSelectedTable);

        Button btnPay = view.findViewById(R.id.btnPay);
        btnPay.setVisibility(View.GONE);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCartDetail);
        setupSpinner(spinner);

        TextView lblTotal = view.findViewById(R.id.lblTotalCartCost);
        lblTotal.setText(String.format("%.0f", bill.getTotalCost()) + "đ");

        builder.setView(view)
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       try {
                           new AlertDialog.Builder(getContext())
                                   .setTitle("Xác nhận xóa")
                                   .setMessage("Bạn có chắc chắn muốn xóa hóa đơn này không?")
                                   .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           deleteBillFromFirebase(bill.getBillId());
                                       }
                                   })
                                   .setNegativeButton("Không", null)
                                   .show();
                       } catch (Exception e){
                           Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                       }
                    }
                })
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                }).setCustomTitle(setupTitle());

        return builder.create();
    }

    private TextView setupTitle(){
        TextView title = new TextView(getActivity());
        title.setText("Chi tiết hóa đơn");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        return title;
    }

    public void deleteBillFromFirebase(final String billId) {
        try {
            FirebaseHelper productBillHelper = new FirebaseHelper("product_bills", getContext());
            FirebaseHelper billHelper = new FirebaseHelper("bills", getContext());
            productBillHelper.myRef.orderByChild("bill/billId").equalTo(billId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        productBillHelper.deleteItem(postSnapshot.getKey(), null);
                    }
                    // Xóa Bill sau khi đã xóa tất cả ProductBill
                    billHelper.deleteItem(billId, null);
                    dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupSpinner(Spinner spinner) {
        try {
            spinner.setEnabled(false);
            spinner.setClickable(false);

            ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this.getActivity(), android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerAdapter.add("Mang về");
            spinnerAdapter.add("Tại bàn");

            spinner.setAdapter(spinnerAdapter);
            if (bill.getTable() == null) {
                spinner.setSelection(spinnerAdapter.getPosition("Mang về"));
                lblSelectedTable.setVisibility(View.GONE);
            } else {
                spinner.setSelection(spinnerAdapter.getPosition("Tại bàn"));
                lblSelectedTable.setVisibility(View.VISIBLE);
                lblSelectedTable.setText(bill.getTable().getTableName()); // giả sử getTableName() trả về tên của bàn
            }
        } catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}

