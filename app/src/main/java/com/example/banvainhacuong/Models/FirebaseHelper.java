package com.example.banvainhacuong.Models;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FirebaseHelper {

    public DatabaseReference myRef;
    //private String url = "https://coffeeshop-680d2-default-rtdb.asia-southeast1.firebasedatabase.app";
    private String url = "https://bookingtour-cfe14-default-rtdb.asia-southeast1.firebasedatabase.app";
    private Context context; // Thêm biến context
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private LoadDataListener loadDataListener;

    public interface LoadDataListener {
        void onDataLoaded();
    }

    public FirebaseHelper(String tableName, Context context) {
        myRef = FirebaseDatabase.getInstance(url).getReference(tableName);
        this.context = context; // Gán biến context
    }

    public void addImageToFirebase(Uri imageUri, OnSuccessListener<Uri> onSuccess, OnFailureListener onFailure) {
        // Tạo một tham chiếu mới cho hình ảnh
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());

        // Tải hình ảnh lên Firebase
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Khi tải lên thành công, lấy Uri của hình ảnh
                imageRef.getDownloadUrl().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
            }
        }).addOnFailureListener(onFailure);
    }

    public void addItem(Object item, String id) {
        myRef.child(id).setValue(item);
    }

    public void updateItem(String itemId, Object item) {
        myRef.child(itemId).setValue(item);
    }

    public void deleteImageFromFirebase(String imageUrl, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        //Lấy tên hình ảnh trong FireBase
        String imageName = DecodeImageName(imageUrl);
        // Kiểm tra xem imageName có tồn tại hay không
        if (imageName != null && !imageName.isEmpty()) {
            // Nếu imageName tồn tại, tiếp tục xóa hình ảnh từ Firebase Storage
            StorageReference imageRef = storage.getReference().child("images/" + imageName);
            imageRef.delete().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
        }
    }

    public void deleteItem(String itemId, String imageUrl) {
        // Xóa sản phẩm từ Firebase Database
        myRef.child(itemId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Kiểm tra xem imageUrl có phải là null không
                    if (imageUrl != null) {
                        // Xóa hình ảnh từ Firebase Storage
                        deleteImageFromFirebase(imageUrl,
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Thêm toast
                                    }
                                },
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        //Thêm toast
                                    }
                                }
                        );
                    }
                } else {
                    // Đã xảy ra lỗi khi xóa sản phẩm từ Firebase Database
                    Toast.makeText(context, "Đã xảy ra lỗi khi xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setLoadDataListener(LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    public void getProductBillListByBillId(final List<Cart> listCart, String billId) {
        myRef.orderByChild("bill/billId").equalTo(billId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listCart.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ProductBill productBill = postSnapshot.getValue(ProductBill.class);
                    Cart cart = new Cart(productBill.getProduct(), productBill.getAmount());
                    listCart.add(cart);
                }
                if (loadDataListener != null) {
                    loadDataListener.onDataLoaded(); // Gọi phương thức onDataLoaded khi dữ liệu đã được tải xong
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Có lỗi xảy ra, bạn có thể xử lý ở đây
            }
        });
    }


    //Hàm phân tích URL lấy tên hình ảnh
    public static String DecodeImageName(String imageUrl) {
        try {
            // Tạo một đối tượng URI từ URL hình ảnh
            URI uri = new URI(imageUrl);

            // Lấy đường dẫn từ URI
            String path = uri.getPath();

            // Tách đường dẫn bằng dấu '/'
            String[] pathParts = path.split("/");

            // Lấy phần cuối cùng của đường dẫn, đó là tên hình ảnh
            String imageName = pathParts[pathParts.length - 1];

            // Giải mã tên hình ảnh
            String decodedImageName = URLDecoder.decode(imageName, StandardCharsets.UTF_8.toString());

            return decodedImageName;
        } catch (Exception e) {
            // Xử lý lỗi
            System.out.println("Đã xảy ra lỗi khi giải mã tên hình ảnh: " + e.getMessage());
            return null;
        }
    }
}

