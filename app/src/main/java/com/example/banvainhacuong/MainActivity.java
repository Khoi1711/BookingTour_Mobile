package com.example.banvainhacuong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.banvainhacuong.Adapters.FragmentAdapter;
import com.example.banvainhacuong.Adapters.ProductAdapter;
import com.example.banvainhacuong.Dialogs.CartDetailDialog;
import com.example.banvainhacuong.Dialogs.ChooseProductDialog;
import com.example.banvainhacuong.Fragments.ProductFragment;
import com.example.banvainhacuong.Models.Cart;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Product;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener,
        ChooseProductDialog.OnProductChosenListener, ProductFragment.OnAdapterUpdatedListener {

    private FirebaseHelper firebaseHelper;
    private ProductAdapter adapter;
    private ArrayList<Cart> lstCart;
    private Button btnCart;
    private FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstCart = new ArrayList<>();

        btnCart = findViewById(R.id.btnCart);
        setupButtonCart(btnCart);

        firebaseHelper = new FirebaseHelper("products", this);

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(firebaseHelper.myRef, Product.class)
                .build();

        adapter = new ProductAdapter(options);
        setupTabView();
        adapter.startListening();
    }

    @Override
    public void onAdapterUpdated() {
        ProductFragment productFragment = fragmentAdapter.getProductFragment();
        ProductAdapter productFragmentAdapter = productFragment.getAdapter();
        this.adapter = productFragmentAdapter;
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onProductChosen(Product product, int amount) {
        Cart existingCart = null;
        for (Cart cart : lstCart) {
            if (cart.getProduct().equals(product)) {
                existingCart = cart;
                break;
            }
        }

        if (existingCart != null) {
            existingCart.setAmount(existingCart.getAmount() + amount);
            Toast.makeText(this, "Cập nhật số lượng " + product.getProductName() + " +" + amount, Toast.LENGTH_SHORT).show();
        } else {
            Cart cart = new Cart(product, amount);
            lstCart.add(cart);
            Toast.makeText(this, "Thêm " + product.getProductName() + " x" + amount, Toast.LENGTH_SHORT).show();
        }

        updateCartButton();
    }

    private void updateCartButton() {
        if (!lstCart.isEmpty()) {
            double totalCost = 0;
            for (Cart cart : lstCart) {
                totalCost += cart.toCast();
            }
            btnCart.setText(String.format("%.0f", totalCost) + 'đ');
            btnCart.setVisibility(View.VISIBLE);
        } else {
            btnCart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(int position) {
        try {
            if (position >= 0 && position < adapter.getItemCount()) {
                Product product = adapter.getItem(position);
                ChooseProductDialog dialog = new ChooseProductDialog(product, this);
                dialog.show(getSupportFragmentManager(), "ChooseProductDialog");
            } else {
                Toast.makeText(this, "Position: " + position
                        + "\n Adapter length: " + adapter.getItemCount(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTabView() {
        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.viewPage2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabIconTint(null);

        TabLayout.Tab firstTab = tabLayout.newTab();
        tabLayout.addTab(firstTab);

        TabLayout.Tab secondTab = tabLayout.newTab();
        tabLayout.addTab(secondTab);

        TabLayout.Tab thirdTab = tabLayout.newTab();
        tabLayout.addTab(thirdTab);

        TabLayout.Tab fourTab = tabLayout.newTab();
        tabLayout.addTab(fourTab);

        fragmentAdapter = new FragmentAdapter(this, tabLayout.getTabCount());
        viewPager2.setAdapter(fragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Home");
                    tab.setIcon(R.drawable.ic_house);
                    break;
                case 1:
                    tab.setText("Product");
                    tab.setIcon(R.drawable.ic_drink);
                    break;
                case 2:
                    tab.setText("Manage");
                    tab.setIcon(R.drawable.ic_manage);
                    break;
                case 3:
                    tab.setText("Setting");
                    tab.setIcon(R.drawable.ic_setting);
                    break;
            }
        }).attach();


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        btnCart.setVisibility(View.GONE); // Ẩn button
                        break;
                    case 1:
                        updateCartButton(); // Hiển thị button nếu cần
                        break;
                    case 2:
                        btnCart.setVisibility(View.GONE); // Ẩn button
                        break;
                    case 3:
                        btnCart.setVisibility(View.GONE); // Ẩn button
                        break;
                }
            }
        });
    }

    private void setupButtonCart(Button btnCart){
        btnCart.setVisibility(View.GONE);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lstCart.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    btnCart.setVisibility(View.GONE);
                } else {
                    CartDetailDialog cartDetailDialog = new CartDetailDialog(lstCart);
                    cartDetailDialog.show(getSupportFragmentManager(), "CartDetailDialog");
                }
            }
        });
    }
}