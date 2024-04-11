package com.example.banvainhacuong.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banvainhacuong.Adapters.CartAdapter;
import com.example.banvainhacuong.Models.Bill;
import com.example.banvainhacuong.Models.Cart;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.ProductBill;
import com.example.banvainhacuong.Models.Table;
import com.example.banvainhacuong.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartDetailDialog extends DialogFragment implements CartAdapter.CartChangeListener {

    private RecyclerView recyclerViewCartDetail;
    private CartAdapter cartAdapter;
    private List<Cart> listCart;
    private TextView lblSelectedTable;
    private Table selectedTable;
    private FirebaseHelper productBillHelper;
    private FirebaseHelper productHelper;
    private FirebaseHelper billHelper;
    private TextView lblToCast;
    private Button btnPay;
    PaymentSheet paymentSheet;
    String customerID, ephemeralKey, clientSecret;
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51OQBvuJU1wQYJnkWe2ge3KCK05ExkHNDfJI9rnfmyRrLHWb9UFKemuLkTA7cuHbeNDWqZJlFCobBQ2lJFYHlHSbZ00kZbGv3j3";
    private static final String STRIPE_SECRET_KEY = "sk_test_51OQBvuJU1wQYJnkWskPyjO0QZX4XL9NJNfpVRyfOOCjm52KZEK9XD82diIxhv13DKQw53Q3AogP7N9WmsSLVDtbd00dmvc4xxb";

    public CartDetailDialog(List<Cart> listCart) {
        this.listCart = listCart;
    }

    public void setTable(Table table) {
        this.selectedTable = table;
        lblSelectedTable.setText(selectedTable.getTableName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_cart_detail, null);
        btnPay = view.findViewById(R.id.btnPay);
        productHelper = new FirebaseHelper("products", getContext());
        productBillHelper = new FirebaseHelper("product_bills", getContext());
        billHelper = new FirebaseHelper("bills", getContext());
        PaymentConfiguration.init(getContext(), STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomer();
                addBillToFirebase();
            }
        });
        try {
            lblToCast = view.findViewById(R.id.lblTotalCartCost);
            lblToCast.setText(String.format("%.0f", getTotalCost()) + "đ");
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        lblSelectedTable = view.findViewById(R.id.lblSelectedTable);
        lblSelectedTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the TableListDialog
                TableListDialog tableListDialog = new TableListDialog();
                tableListDialog.show(getChildFragmentManager(), "TableListDialog");
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCartDetail);
        setupSpinner(spinner);

        recyclerViewCartDetail = view.findViewById(R.id.recyclerViewCartDetail);
        recyclerViewCartDetail.setLayoutManager(new LinearLayoutManager(getContext()));

        cartAdapter = new CartAdapter(listCart, this);
        recyclerViewCartDetail.setAdapter(cartAdapter);

        builder.setView(view)
                .setPositiveButton("Thanh Toán", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addBillToFirebase();
                        // Làm trống giỏ hàng sau khi đặt hàng
                        listCart.clear();
                        // Cập nhật giao diện
                        cartAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setCustomTitle(setupTitle());

        return builder.create();
    }

    @Override
    public void onCartChange() {
        listCart = cartAdapter.getListCart();
        cartAdapter.notifyDataSetChanged();
        lblToCast.setText(String.format("%.0f", getTotalCost()) + "đ");
    }

    private double getTotalCost() {
        double totalCost = 0;
        for (Cart cart : listCart) {
            totalCost += cart.toCast();
        }
        return totalCost;
    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this.getActivity(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapter.add("Mang về");
        spinnerAdapter.add("Tại bàn");

        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Tại bàn")) {
                    lblSelectedTable.setVisibility(View.VISIBLE);
                    // selectedTable giữ nguyên
                } else {
                    lblSelectedTable.setVisibility(View.GONE);
                    selectedTable = null; // Đặt selectedTable thành null cho đơn hàng mang về
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    private void addBillToFirebase() {
        if (!listCart.isEmpty()) {
            try {
                String billId = billHelper.myRef.push().getKey(); // Tạo một ID duy nhất cho Bill
                String createDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()); // Ngày tạo hiện tại
                double totalCost = getTotalCost(); // Tổng tiền

                Bill bill = new Bill(billId, createDate, selectedTable, totalCost);
                billHelper.addItem(bill, billId);

                for (Cart cart : listCart) {
                    ProductBill productBill = new ProductBill(bill, cart.getProduct(), cart.getAmount());
                    String productBillId = productBillHelper.myRef.push().getKey();
                    productBillHelper.addItem(productBill, productBillId);

                    int newInventory = cart.getProduct().getInventory() - cart.getAmount();
                    cart.getProduct().setInventory(newInventory);
                    productHelper.updateItem(cart.getProduct().getId(), cart.getProduct());
                }
            } catch (Exception e){
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Giỏ hàng đang trống", Toast.LENGTH_LONG).show();
        }
    }

    private TextView setupTitle(){
        TextView title = new TextView(getActivity());
        title.setText("Chi tiết giỏ hàng");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
       return title;
    }

    private void setupSpinner(Spinner spinner, String table) {
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this.getActivity(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapter.add("Mang về");
        spinnerAdapter.add("Tại bàn");

        spinner.setAdapter(spinnerAdapter);
        if (table == null) {
            spinner.setSelection(spinnerAdapter.getPosition("Mang về"));
        } else {
            spinner.setSelection(spinnerAdapter.getPosition("Tại bàn"));
            lblSelectedTable.setVisibility(View.VISIBLE);
            lblSelectedTable.setText(table);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Tại bàn")) {
                    lblSelectedTable.setVisibility(View.VISIBLE);
                } else {
                    lblSelectedTable.setVisibility(View.GONE);
                    selectedTable = null; // Đặt selectedTable thành null cho đơn hàng mang về
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            addBillToFirebase();
            listCart.clear();
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void createCustomer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            getEphemeralKey(customerID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getEphemeralKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ephemeralKey = object.getString("id");
                            getClientSecret(customerID, ephemeralKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                headers.put("Stripe-Version", "2023-10-16");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephemeralKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            clientSecret = object.getString("client_secret");
                            showPaymentSheet(clientSecret);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                headers.put("Stripe-Version", "2023-10-16");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                double total = getTotalCost();
                params.put("amount", String.format("%.0f", getTotalCost()));
                params.put("currency", "vnd");
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void showPaymentSheet(String clientSecret) {
        paymentSheet.presentWithPaymentIntent(clientSecret);
    }

}


