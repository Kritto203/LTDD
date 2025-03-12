package com.example.project.SanPham;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.project.LoaiSanPham.LoaiSanPham;
import com.example.project.LoaiSanPham.LoaiSanPhamDAO;
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_SanPham extends RecyclerView.Adapter<Adapter_SanPham.ViewHolder> {
    private static final String TAG = "Adapter_SanPham";

    List<SanPham> list;
    Context context;
    private SanPhamDAO sanPhamDAO;
    LoaiSanPhamDAO loaiSanPhamDAO;

    public Adapter_SanPham(Context context) {
        this.context = context;
        loaiSanPhamDAO = new LoaiSanPhamDAO(context);
        sanPhamDAO = new SanPhamDAO(context);
    }

    public void setData(List<SanPham> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public Adapter_SanPham(ArrayList<SanPham> list, Context context, SanPhamDAO sanPhamDAO) {
        this.list = list;
        this.context = context;
        this.sanPhamDAO = sanPhamDAO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_recyclesanpham, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        List<LoaiSanPham> loaiSanPham1 = loaiSanPhamDAO.getAll();
        String tenLoaiSanPham = "";
        for (LoaiSanPham loaiSanPham : loaiSanPham1) {
            if (loaiSanPham.getMaLoaiSanPham() == list.get(position).getMaloai()) {
                tenLoaiSanPham = loaiSanPham.getTenLoaiSanPham();
            }
        }
        holder.txtLoaiSanPham.setText(tenLoaiSanPham);
        holder.txtTenSanPham.setText(list.get(position).getTensanpham());
        holder.txtLinkAnhSanPham.setText(list.get(position).getLinkanhsanpham());
        holder.txtGiaSanPham.setText(list.get(position).getGiasanpham());
        holder.txtGiamGia.setText(list.get(position).getGiamgia());
        holder.txtSoLuongTrongKho.setText(String.valueOf(list.get(position).getSoluongtrongkho()));
        holder.txtNgaySanXuat.setText(list.get(position).getNgaysanxuat());
        holder.txtHanSuDung.setText(list.get(position).getHansudung());

        // Load product image from link
        String imageUrl = list.get(position).getLinkanhsanpham();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("NULL")) {
            try {
                Glide.with(context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(android.R.color.darker_gray)
                        .error(android.R.color.holo_red_light)
                        .into(holder.txtAnhSanPham);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image for product: " + e.getMessage());
                holder.txtAnhSanPham.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            // Set a default image
            holder.txtAnhSanPham.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.ibDeleteSanPham.setOnClickListener(new View.OnClickListener() {
            Button btnDiaLogXoaSanPham, btnDiaLogHuyXoaSanPham;
            private SanPhamDAO sanPhamDAO = new SanPhamDAO(context);

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater1 = ((Activity) context).getLayoutInflater();
                v = inflater1.inflate(R.layout.item_deleltesanpham, null);
                builder.setView(v);
                Dialog dialog = builder.create();
                btnDiaLogHuyXoaSanPham = v.findViewById(R.id.btnDiaLogHuyXoaSanPham);
                btnDiaLogXoaSanPham = v.findViewById(R.id.btnDiaLogXoaSanPham);

                btnDiaLogHuyXoaSanPham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnDiaLogXoaSanPham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int check = sanPhamDAO.delete(list.get(position).getMasanpham());
                        switch (check) {
                            case 1:
                                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                list.clear();
                                list = (ArrayList<SanPham>) sanPhamDAO.getAll();
                                notifyDataSetChanged();
                                dialog.dismiss();
                                break;
                            case 0:
                                Toast.makeText(context, "Xóa không thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                break;
                            case -1:
                                Toast.makeText(context, "Xóa không thành công vì đang có hóa đơn", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                break;

                        }
                    }
                });
                dialog.show();
            }
        });

        holder.ibUpdateSanPham.setOnClickListener(new View.OnClickListener() {

            EditText edtSuaTenSanPham, edtSuaAnhSanPham, edtSuaLinkAnhSanPham, edtSuaGiaSanPham, edtSuaGiamGia, edtSuaSoLuongTrongKho, edtSuaNgaySanXuat, edtSuaHanSuDung;
            Spinner spUpdateChonLoaiSanPham;
            Button btnDialodHuySuaSanPham, btnDialogSuaSanPham, btnXemAnhSuaSanPham;
            ImageView imgXemTruocSuaSanPham;
            SanPhamDAO sanPhamDAO = new SanPhamDAO(context);

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater1 = ((Activity) context).getLayoutInflater();
                v = inflater1.inflate(R.layout.item_updatesanpham, null);
                builder.setView(v);
                Dialog dialog = builder.create();

                // Find views
                edtSuaTenSanPham = v.findViewById(R.id.edtSuaTenSanPham);
                //edtSuaAnhSanPham = v.findViewById(R.id.edtSuaAnhSanPham);
                edtSuaLinkAnhSanPham = v.findViewById(R.id.edtSuaLinkAnhSanPham);
                edtSuaGiaSanPham = v.findViewById(R.id.edtSuaGiaSanPham);
                edtSuaGiamGia = v.findViewById(R.id.edtSuaGiamGia);
                edtSuaSoLuongTrongKho = v.findViewById(R.id.edtSuaSoLuongTrongKho);
                edtSuaNgaySanXuat = v.findViewById(R.id.edtSuaNgaySanXuat);
                edtSuaHanSuDung = v.findViewById(R.id.edtSuaHanSuDung);
                btnDialodHuySuaSanPham = v.findViewById(R.id.btnDialodHuySuaSanPham);
                btnDialogSuaSanPham = v.findViewById(R.id.btnDialogSuaSanPham);
                spUpdateChonLoaiSanPham = v.findViewById(R.id.spUpdateChonLoaiSanPham);

                // Image preview components
                btnXemAnhSuaSanPham = v.findViewById(R.id.btnXemAnhSuaSanPham);
                imgXemTruocSuaSanPham = v.findViewById(R.id.imgXemTruocSuaSanPham);

                // Set current values
                edtSuaTenSanPham.setText(list.get(position).getTensanpham());
                edtSuaAnhSanPham.setText(list.get(position).getAnhsanpham());
                edtSuaLinkAnhSanPham.setText(list.get(position).getLinkanhsanpham());
                edtSuaGiaSanPham.setText(list.get(position).getGiasanpham());
                edtSuaGiamGia.setText(list.get(position).getGiamgia());
                edtSuaSoLuongTrongKho.setText(String.valueOf(list.get(position).getSoluongtrongkho()));
                edtSuaNgaySanXuat.setText(list.get(position).getNgaysanxuat());
                edtSuaHanSuDung.setText(list.get(position).getHansudung());

                // Load current image
                String currentImageUrl = list.get(position).getLinkanhsanpham();
                if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.equals("NULL")) {
                    try {
                        Glide.with(context)
                                .load(currentImageUrl)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .placeholder(android.R.color.darker_gray)
                                .error(android.R.color.holo_red_light)
                                .into(imgXemTruocSuaSanPham);
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading current image: " + e.getMessage());
                    }
                }

                // Setup image preview button
                if (btnXemAnhSuaSanPham != null) {
                    btnXemAnhSuaSanPham.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String imageUrl = edtSuaLinkAnhSanPham.getText().toString().trim();

                            if (imageUrl.isEmpty()) {
                                Toast.makeText(context, "Vui lòng nhập link ảnh", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!isValidImageUrl(imageUrl)) {
                                Toast.makeText(context, "Link ảnh không hợp lệ. Hãy đảm bảo link bắt đầu bằng http:// hoặc https://", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Load image preview
                            try {
                                Glide.with(context)
                                        .load(imageUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .placeholder(android.R.color.darker_gray)
                                        .error(android.R.color.holo_red_light)
                                        .into(imgXemTruocSuaSanPham);

                                Toast.makeText(context, "Đang tải ảnh...", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error loading image: " + e.getMessage());
                                Toast.makeText(context, "Lỗi khi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Setup spinner
                SanPham sanPham = new SanPham();
                List<String> lst = new ArrayList<>();
                List<LoaiSanPham> ok = (ArrayList<LoaiSanPham>) loaiSanPhamDAO.getAll();
                for (LoaiSanPham loaiSanPham : ok) {
                    lst.add(loaiSanPham.getTenLoaiSanPham());
                }

                // Set correct spinner selection
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, lst);
                spUpdateChonLoaiSanPham.setAdapter(adapter);

                // Select the correct category
                for (int i = 0; i < lst.size(); i++) {
                    if (loaiSanPham1.get(i).getMaLoaiSanPham() == list.get(position).getMaloai()) {
                        spUpdateChonLoaiSanPham.setSelection(i);
                        break;
                    }
                }

                btnDialodHuySuaSanPham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnDialogSuaSanPham.setOnClickListener(new View.OnClickListener() {
                    private List<LoaiSanPham> ok = (ArrayList<LoaiSanPham>) loaiSanPhamDAO.getAll();

                    @Override
                    public void onClick(View v) {
                        SanPham sanPham1 = list.get(position);
                        sanPham1.setTensanpham(edtSuaTenSanPham.getText().toString());
                        sanPham1.setAnhsanpham(edtSuaAnhSanPham.getText().toString());
                        sanPham1.setLinkanhsanpham(edtSuaLinkAnhSanPham.getText().toString());
                        sanPham1.setGiasanpham(edtSuaGiaSanPham.getText().toString());
                        sanPham1.setGiamgia(edtSuaGiamGia.getText().toString());

                        try {
                            sanPham1.setSoluongtrongkho(Integer.parseInt(edtSuaSoLuongTrongKho.getText().toString()));
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Số lượng phải là số", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sanPham1.setNgaysanxuat(edtSuaNgaySanXuat.getText().toString());
                        sanPham1.setHansudung(edtSuaHanSuDung.getText().toString());

                        for (LoaiSanPham loaiSanPham : ok) {
                            if (loaiSanPham.getTenLoaiSanPham().equals(spUpdateChonLoaiSanPham.getSelectedItem().toString())) {
                                sanPham1.setMaloai(loaiSanPham.getMaLoaiSanPham());
                            }
                        }

                        if (sanPhamDAO.update(sanPham1) > 0) {
                            list.clear();
                            list = (ArrayList<SanPham>) sanPhamDAO.getAll();
                            setData(list);
                            dialog.dismiss();
                            Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        // Basic URL validation
        return url.startsWith("http://") || url.startsWith("https://");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLoaiSanPham, txtTenSanPham, txtLinkAnhSanPham,
                txtGiaSanPham, txtGiamGia, txtSoLuongTrongKho, txtNgaySanXuat, txtHanSuDung;

        ImageView ibUpdateSanPham, ibDeleteSanPham, txtAnhSanPham;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLoaiSanPham = itemView.findViewById(R.id.txtLoaiSanPham);
            txtTenSanPham = itemView.findViewById(R.id.txtTenSanPham);
            txtAnhSanPham = itemView.findViewById(R.id.txtAnhSanPham);
            txtLinkAnhSanPham = itemView.findViewById(R.id.txtLinkAnhSanPham);
            txtGiaSanPham = itemView.findViewById(R.id.txtGiaSanPham);
            txtGiamGia = itemView.findViewById(R.id.txtGiamGia);
            txtSoLuongTrongKho = itemView.findViewById(R.id.txtSoLuongTrongKho);
            txtNgaySanXuat = itemView.findViewById(R.id.txtNgaySanXuat);
            txtHanSuDung = itemView.findViewById(R.id.txtHanSuDung);
            ibUpdateSanPham = itemView.findViewById(R.id.ibUpdateSanPham);
            ibDeleteSanPham = itemView.findViewById(R.id.ibDeleteSanPham);
        }
    }
}