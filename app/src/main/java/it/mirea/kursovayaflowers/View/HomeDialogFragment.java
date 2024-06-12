package it.mirea.kursovayaflowers.View;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.addressLogic.AddressAnalysis;
import it.mirea.kursovayaflowers.room.AppDatabase;
import it.mirea.kursovayaflowers.room.Event;

public class HomeDialogFragment extends DialogFragment {

    private ImageView imageView;
    private byte[] imgBlob = new byte[0];
    private static final int SELECT_PICTURE = 200;
    private AddressAnalysis mAnalysis;

    public static HomeDialogFragment newInstance() {
        return new HomeDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено
            } else {
                // Разрешение отклонено
                Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.window_florist, null);

        builder.setView(view)
                .setNegativeButton("Отменить", (dialog, id) -> dialog.dismiss())
                .setPositiveButton("Добавить", (dialog, id) -> {
                    saveNewEvent(view);
                    Snackbar snackbar = Snackbar.make(view, "Добавлено", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    snackbarView.setLayoutParams(params);
                    snackbar.show();
                });

        imageView = view.findViewById(R.id.ivPicture);
        final ImageView loadImage = view.findViewById(R.id.image_add_photo);
        loadImage.setOnClickListener(v -> imageChooser());


        if (mAnalysis == null) {
            mAnalysis = new AddressAnalysis();
        }

        return builder.create();
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap yourBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    yourBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    imgBlob = bos.toByteArray();
                    imageView.setImageURI(selectedImageUri);
                    imageView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveNewEvent(View view) {
        EditText holidayInput = view.findViewById(R.id.product_name_field);
        EditText descriptionInput = view.findViewById(R.id.price_field);

        // Получаем контекст активности для передачи его в метод getDbInstance
        Context context = getActivity();

        // Выполняем операцию в фоновом потоке
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDbInstance(context);

            Event event = new Event();
            event.date = holidayInput.getText().toString();
            event.description = descriptionInput.getText().toString();
            event.imgBlob = imgBlob;
            db.eventDao().insertEvent(event);
        });
    }
}