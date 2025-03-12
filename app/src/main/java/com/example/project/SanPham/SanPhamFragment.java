package com.example.project.SanPham;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.project.LoaiSanPham.LoaiSanPham;
import com.example.project.LoaiSanPham.LoaiSanPhamDAO;
import com.example.project.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SanPhamFragment extends Fragment {
    private static final String TAG = "SanPhamFragment";
    private RecyclerView recycleViewSanPham;
    private FloatingActionButton btnThemSanPham;
    private Spinner spChonLoaiSanPham;
    SanPhamDAO sanPhamDAO;
    ArrayList<SanPham> list = new ArrayList<>();


    public SanPhamFragment() {

    }

    public static SanPhamFragment newInstance() {
        SanPhamFragment fragment = new SanPhamFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_san_pham, container, false);

        recycleViewSanPham = view.findViewById(R.id.recycleViewSanPham);
        btnThemSanPham = view.findViewById(R.id.btnThemSanPham);
        btnThemSanPham.setOnClickListener(new View.OnClickListener() {
            EditText edtThemTenSanPham, edtThemLinkAnhSanPham,
                    edtThemGiaSanPham, edtThemGiamGia, edtThemSoLuongTrongKho, edtThemNgaySanXuat, edtThemHanSuDung;
            Button btnDialogHuyThemSanPham, btnDialogThemSanPham, btnXemAnhSanPham;
            ImageView imgXemTruocSanPham;
            private SanPhamDAO sanPhamDAO = new SanPhamDAO(getContext());
            private LoaiSanPhamDAO loaiSanPhamDAO = new LoaiSanPhamDAO(getContext());
            private List<LoaiSanPham> list = (ArrayList<LoaiSanPham>) loaiSanPhamDAO.getAll();

            @Override
            public void onClick(View view1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater1 = ((Activity) getContext()).getLayoutInflater();
                view1 = inflater1.inflate(R.layout.item_themsanpham, null);
                builder.setView(view1);
                Dialog dialog = builder.create();

                // Find all views
                edtThemTenSanPham = view1.findViewById(R.id.edtThemTenSanPham);
                edtThemLinkAnhSanPham = view1.findViewById(R.id.edtThemLinkAnhSanPham);
                edtThemGiaSanPham = view1.findViewById(R.id.edtThemGiaSanPham);
                edtThemGiamGia = view1.findViewById(R.id.edtThemGiamGia);
                edtThemSoLuongTrongKho = view1.findViewById(R.id.edtThemSoLuongTrongKho);
                edtThemNgaySanXuat = view1.findViewById(R.id.edtThemNgaySanXuat);
                edtThemHanSuDung = view1.findViewById(R.id.edtThemHanSuDung);
                btnDialogThemSanPham = view1.findViewById(R.id.btnDialogThemSanPham);
                btnDialogHuyThemSanPham = view1.findViewById(R.id.btnDialogHuyThemSanPham);
                btnXemAnhSanPham = view1.findViewById(R.id.btnXemAnhSanPham);
                imgXemTruocSanPham = view1.findViewById(R.id.imgXemTruocSanPham);
                spChonLoaiSanPham = view1.findViewById(R.id.spChonLoaiSanPham);

                // Setup spinner
                spChonLoai();

                // Handle image preview
                setupImagePreview();

                btnDialogHuyThemSanPham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnDialogThemSanPham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tenSanPham = edtThemTenSanPham.getText().toString().trim();
                        String linkAnhSanPham = edtThemLinkAnhSanPham.getText().toString().trim();
                        String giaSanPham = edtThemGiaSanPham.getText().toString().trim();
                        String soLuongTrongKho = edtThemSoLuongTrongKho.getText().toString().trim();
                        String ngaySanXuat = edtThemNgaySanXuat.getText().toString().trim();
                        String hanSuDung = edtThemHanSuDung.getText().toString().trim();
                        String giamgia = edtThemGiamGia.getText().toString().trim();

                        // Validate all fields
                        if (!validateInputs(tenSanPham, giaSanPham, giamgia, soLuongTrongKho, ngaySanXuat, hanSuDung)) {
                            return;
                        }

                        // Create the SanPham object
                        SanPham sanPham = new SanPham();
                        sanPham.setTensanpham(tenSanPham);
                        sanPham.setLinkanhsanpham(linkAnhSanPham); // Set the image link
                        sanPham.setGiasanpham(giaSanPham);
                        sanPham.setGiamgia(giamgia);
                        sanPham.setSoluongtrongkho(Integer.parseInt(soLuongTrongKho));
                        sanPham.setNgaysanxuat(ngaySanXuat);
                        sanPham.setHansudung(hanSuDung);

                        // Set the loai (category)
                        for (LoaiSanPham loaiSanPham : list) {
                            if (loaiSanPham.getTenLoaiSanPham().equals(spChonLoaiSanPham.getSelectedItem().toString())) {
                                sanPham.setMaloai(loaiSanPham.getMaLoaiSanPham());
                            }
                        }

                        // Insert into database
                        if (sanPhamDAO.insert(sanPham) > 0) {
                            Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                        reload();
                    }
                });

                dialog.show();
            }

            private void setupImagePreview() {
                btnXemAnhSanPham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String imageUrl = edtThemLinkAnhSanPham.getText().toString().trim();

                        if (imageUrl.isEmpty()) {
                            Toast.makeText(getContext(), "Vui lòng nhập link ảnh", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!isValidImageUrl(imageUrl)) {
                            Toast.makeText(getContext(), "Link ảnh không hợp lệ. Hãy đảm bảo link bắt đầu bằng http:// hoặc https://", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Load image preview
                        loadImagePreview(imageUrl, imgXemTruocSanPham);
                    }
                });
            }
        });

        reload();
        return view;
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        // Basic URL validation
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }

        return true;
    }

    private void loadImagePreview(String imageUrl, ImageView imageView) {
        try {
            Glide.with(getContext())
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.holo_red_light)
                    .into(imageView);

            Toast.makeText(getContext(), "Đang tải ảnh...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            imageView.setImageDrawable(null);
            imageView.setBackgroundColor(Color.RED);
            Toast.makeText(getContext(), "Lỗi khi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String tenSanPham, String giaSanPham, String giamgia,
                                   String soLuongTrongKho, String ngaySanXuat, String hanSuDung) {
        // Kiểm tra tên sản phẩm
        if (tenSanPham.isEmpty()) {
            Toast.makeText(getContext(), "Tên sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra giá sản phẩm
        if (giaSanPham.isEmpty()) {
            Toast.makeText(getContext(), "Giá sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                double gia = Double.parseDouble(giaSanPham);
                if (gia <= 0) {
                    Toast.makeText(getContext(), "Giá sản phẩm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá sản phẩm phải là số", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Kiểm tra giảm giá
        if (giamgia.isEmpty()) {
            Toast.makeText(getContext(), "Giảm giá sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                double gia = Double.parseDouble(giamgia);
                if (gia < 0) {
                    Toast.makeText(getContext(), "Giảm giá sản phẩm không được âm", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giảm giá sản phẩm phải là số", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Kiểm tra số lượng trong kho
        if (soLuongTrongKho.isEmpty()) {
            Toast.makeText(getContext(), "Số lượng trong kho không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                int soLuong = Integer.parseInt(soLuongTrongKho);
                if (soLuong <= 0) {
                    Toast.makeText(getContext(), "Số lượng trong kho không được âm", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số lượng trong kho phải là số", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Kiểm tra ngày sản xuất
        if (ngaySanXuat.isEmpty()) {
            Toast.makeText(getContext(), "Ngày sản xuất không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            try {
                sdf.parse(ngaySanXuat);
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Định dạng ngày tháng không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Kiểm tra hạn sử dụng
        if (hanSuDung.isEmpty()) {
            Toast.makeText(getContext(), "Hạn sử dụng không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            try {
                Date ngaySanXuatDate = sdf.parse(ngaySanXuat);
                Date hanSuDungDate = sdf.parse(hanSuDung);
                if (hanSuDungDate.before(ngaySanXuatDate)) {
                    Toast.makeText(getContext(), "Hạn sử dụng phải sau ngày sản xuất", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Định dạng ngày tháng không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void spChonLoai() {
        LoaiSanPhamDAO loaiSanPhamDAO1 = new LoaiSanPhamDAO(getContext());
        List<String> lst = new ArrayList<>();
        List<LoaiSanPham> list = (ArrayList<LoaiSanPham>) loaiSanPhamDAO1.getAll();
        for (LoaiSanPham loaiSanPham : list) {
            lst.add(loaiSanPham.getTenLoaiSanPham());
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lst);
        spChonLoaiSanPham.setAdapter(adapter);
    }

    public void reload() {
        sanPhamDAO = new SanPhamDAO(getContext());
        list = (ArrayList<SanPham>) sanPhamDAO.getAll();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycleViewSanPham.setLayoutManager(linearLayoutManager);
        Adapter_SanPham sanPhamAdapter = new Adapter_SanPham(getContext());
        sanPhamAdapter.setData(list);
        recycleViewSanPham.setAdapter(sanPhamAdapter);
    }
}