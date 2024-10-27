package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Zoomfile extends AppCompatActivity {

    private   final int PERMISSION_REQUEST_CODE = 100;
    private   final String TAG = "Zoom";
    protected   ImageView imageView1;
    //private ImageView imageView2;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private   Matrix matrix = new Matrix();
    private   float[] matrixValues = new float[9];

    private   AppDatabase mDatabase;
    private float lastTouchX;
    private float lastTouchY;
    protected    FrameLayout layout;
    private LinearLayout bareFichier;


    private OkHttpClient client;

    private ProgressDialog progressDialog;
    private ExecutorService executorService;
    private Handler handler;

    private GestureDetector gestureDetector;
    //public   int hauteur;
    private String fichier ;
    private String type;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        client = new OkHttpClient();
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        Intent intent = getIntent();
        fichier = intent.getStringExtra("file");
         type = intent.getStringExtra("type");





        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        layout = findViewById(R.id.layout);
        imageView1 = findViewById(R.id.image1);
        bareFichier = findViewById(R.id.bareFichiers);
        //hauteur = getSupportActionBar().getHeight();
        //bareFichier.setPadding(0, getSupportActionBar().getHeight(), 0, 0);
        displayFile(fichier,type);


        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());




        imageView1.setOnTouchListener((v, event) -> {

            scaleGestureDetector.onTouchEvent(event);

            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    //isLongClick = false;
                    //handler.postDelayed(longClickRunnable, LONG_CLICK_DURATION);
                    final float x = event.getX();
                    final float y = event.getY();

                    lastTouchX = x;
                    lastTouchY = y;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final float x = event.getX();
                    final float y = event.getY();

                    final float dx = x - lastTouchX;
                    final float dy = y - lastTouchY;

                    //posX += dx;
                    //posY += dy;

                    matrix.postTranslate(dx, dy);
                    imageView1.setImageMatrix(matrix);

                    lastTouchX = x;
                    lastTouchY = y;

                    break;
                }


                default:
                    return false;
            }
            return true;
        });


    }

    private void displayFile(String fichier, String type) {


        showProgressDialog();
        executorService.execute(Zoomfile.this::downloadFile);
        scaleFactor = 1.0f;
        matrix = new Matrix();
        imageView1.setImageMatrix(matrix);
        displayPdf(fichier, type);
        handler.post(Zoomfile.this::hideProgressDialog);
    }


    private void downloadFile() {
        File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
        File subDir = new File(appDir, type); // Sous-répertoire

        File file = new File(subDir, fichier);
        if(!file.exists()){
            runOnUiThread(() -> {
                Toast.makeText(Zoomfile.this, "telechargement du fichier ", Toast.LENGTH_SHORT).show();
            });
            downloadFile("http://" + MainActivity.ip + ":3000/download", fichier, type);
        }
        else {
            runOnUiThread(() -> {
                Toast.makeText(Zoomfile.this, "fichier local ", Toast.LENGTH_SHORT).show();
            });
        }
    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }




    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            imageView1.setImageMatrix(matrix);
            return false;
        }
    }














    private void displayPdf(String fichier, String type) {
        try {
            File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
            File subDir = new File(appDir, type); // Sous-répertoire

            File file = new File(subDir, fichier);
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0); // Ouvrir la première page


            // Créer un bitmap pour rendre la page PDF
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Afficher la page rendue dans l'ImageView
            imageView1.setImageBitmap(bitmap);



            // Fermer la page et le renderer
            page.close();
            pdfRenderer.close();
            fileDescriptor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startDownload();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    private void downloadFile(String url, String fileName, String type) {
        Request request = new Request.Builder().url(url + "/" + type + "/" + fileName).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Download failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(Zoomfile.this, "Download failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server returned error: " + response.code());
                    runOnUiThread(() -> Toast.makeText(Zoomfile.this, "Server error", Toast.LENGTH_SHORT).show());
                    return;
                }

                File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
                File subDir = new File(appDir, type); // Sous-répertoire

                // Créez le sous-répertoire s'il n'existe pas
                if (!subDir.exists()) {
                    if (!subDir.mkdirs()) {
                        Log.e(TAG, "Failed to create directory");
                        runOnUiThread(() -> Toast.makeText(Zoomfile.this, "Failed to create directory", Toast.LENGTH_SHORT).show());
                        return;
                    }
                }

                File file = new File(subDir, fileName);
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    runOnUiThread(() -> Toast.makeText(Zoomfile.this, "Download complete", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    Log.e(TAG, "Error saving file: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(Zoomfile.this, "Error saving file", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


}
