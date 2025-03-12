package com.example.project.GioHang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DonMua.GioHang;
import com.example.project.DonMua.GioHangDAO2;
import com.example.project.Interface.ChangeNumberItemCartList;
import com.example.project.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {
    private static final String TAG = "CartListAdapter";
    ArrayList<GioHang> list;
    GioHangDAO2 gioHangDAO2;
    ChangeNumberItemCartList changeNumberItemCartList;
    Context context;

    public CartListAdapter(ArrayList<GioHang> list, Context context, ChangeNumberItemCartList changeNumberItemCartList) {
        this.list = list;
        gioHangDAO2 = new GioHangDAO2(context);
        this.changeNumberItemCartList = changeNumberItemCartList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.from(parent.getContext()).inflate(R.layout.activity_cart_list_adapter, parent, false);
        return new CartListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final GioHang gioHang = list.get(position);
        if (gioHang == null) {
            return;
        }

        // Set product text information
        holder.tenSanPham.setText(gioHang.getTensanpham());
        holder.tvGiaGoc.setText(gioHang.getGiasanpham() + " VND");
        holder.tvSoLuong.setText(gioHang.getSoluong() + "");
        holder.tvGiaTong.setText((Math.round(gioHang.getSoluong() * gioHang.getGiasanpham() * 100) / 100) + " VNĐ");

        // Load product image
        loadProductImage(holder.imageAvataSanPham, gioHang);

        // Set listeners for quantity buttons
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gioHangDAO2.plusNumber(list, position, context, holder.itemView.getRootView(), new ChangeNumberItemCartList() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changeNumberItemCartList.changed();
                    }
                });
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gioHangDAO2.minusNumber(list, position, holder.itemView.getContext(), new ChangeNumberItemCartList() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changeNumberItemCartList.changed();
                    }
                });
            }
        });

        // Set delete button click listener
        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete item from cart
                gioHangDAO2.delete(list.get(position));
                list.remove(position);
                notifyDataSetChanged();
                changeNumberItemCartList.changed();
            }
        });
    }

    // Method to load the product image
    private void loadProductImage(ImageView imageView, GioHang gioHang) {
        try {
            String linkAnhSanPham = gioHang.getLinkanhsanpham();
            String anhSanPham = gioHang.getAnhsanpham();

            // First try to load from URL if available
            if (linkAnhSanPham != null && !linkAnhSanPham.isEmpty() && !linkAnhSanPham.equals("NULL")) {
                // Load image from URL using AsyncTask
                new LoadImageFromURLTask(imageView).execute(linkAnhSanPham);
                Log.d(TAG, "Loading image from URL: " + linkAnhSanPham);
            }
            // Then try to load from binary data if available
            else if (anhSanPham != null && !anhSanPham.isEmpty()) {
                try {
                    // Try to convert to byte array and decode
                    byte[] imageBytes = anhSanPham.getBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        Log.d(TAG, "Loading image from binary data");
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error decoding image from binary data", e);
                }

                // If binary data loading failed, set default image
                imageView.setImageResource(R.drawable.logo_color);
            }
            // If no image sources available, set default image
            else {
                imageView.setImageResource(R.drawable.logo_color);
                Log.d(TAG, "Setting default image (no image data)");
            }
        } catch (Exception e) {
            // If any error occurs, use the default image
            imageView.setImageResource(R.drawable.logo_color);
            Log.e(TAG, "Error loading product image", e);
        }
    }

    // AsyncTask to load image from URL
    private class LoadImageFromURLTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageFromURLTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error downloading image", e);
                return null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // If loading failed, set default image
                imageView.setImageResource(R.drawable.logo_color);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CartListViewHolder extends RecyclerView.ViewHolder {
        private ImageView btnMinus, btnPlus, imageAvataSanPham, iconDelete;
        private TextView tenSanPham, tvSoLuong, tvGiaGoc, tvGiaTong;

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            tenSanPham = itemView.findViewById(R.id.tenSanPham);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvGiaGoc = itemView.findViewById(R.id.tvGiaGoc);
            tvGiaTong = itemView.findViewById(R.id.tvGiaTong);
            imageAvataSanPham = itemView.findViewById(R.id.imageAvataSanPham);
            iconDelete = itemView.findViewById(R.id.iconDelete);
        }
    }
}