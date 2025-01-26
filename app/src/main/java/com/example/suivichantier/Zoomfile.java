package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.room.Room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private   Matrix matrix = new Matrix();
    private float lastTouchX;
    private float lastTouchY;
    protected  FrameLayout layout;
    private OkHttpClient client;
    private     Bitmap bitmap;
    private ProgressDialog progressDialog;
    private ExecutorService executorService;
    private Handler handler;

    private GestureDetector gestureDetector;
    private String fichier ;
    private String type;
    private String nomClient;
    private int chantierID;
    private int entrepriseID;
    private  String nomChantier;
    private  String typeEntreprise;
    private  String nomEntreprise;
    private static File appDir;
    private String parentActivity;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        ((ConstraintLayout)findViewById(R.id.vue_principale)).setBackground(null);
        client = new OkHttpClient();
        Intent intent = getIntent();
        fichier = intent.getStringExtra("file");
        type = intent.getStringExtra("type");
        nomClient = intent.getStringExtra("nomClient");
        nomChantier = intent.getStringExtra("nomChantier");
        nomEntreprise = intent.getStringExtra("nomEntreprise");
        chantierID = intent.getIntExtra("chantierID",0);
        entrepriseID = intent.getIntExtra("entrepriseID",0);
        typeEntreprise = intent.getStringExtra("typeEntreprise");
        parentActivity = intent.getStringExtra("parentActivity");
        appDir = getFilesDir();
        Log.d("nomEntreprise", "onResponse: "+nomEntreprise);
        Log.d("nomchantier", "onResponse: "+nomChantier);



        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        layout = findViewById(R.id.layout);
        imageView1 = findViewById(R.id.image1);

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

        scaleFactor = 1.0f;
        matrix = new Matrix();
        imageView1.setImageMatrix(matrix);
        handlePdfFile();

    }



//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 6.5f));
//            matrix.setScale(scaleFactor, scaleFactor);
//            //imageView1.setImageMatrix(matrix);
//            float  dx= (float) imageView1.getWidth() /2- (float)bitmap.getWidth()/2*scaleFactor ;
//            float  dy= (float) imageView1.getHeight() /2- (float) bitmap.getHeight() /2*scaleFactor ;
//            matrix.postTranslate(dx, dy);
//            imageView1.setImageMatrix(matrix);
//           return false;
//        }
//    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Calculer le facteur de zoom relatif
            float scaleFactor = detector.getScaleFactor();
            float previousScaleFactor = Zoomfile.this.scaleFactor;
            Zoomfile.this.scaleFactor *= scaleFactor;
            Zoomfile.this.scaleFactor = Math.max(1.0f, Math.min(Zoomfile.this.scaleFactor, 6.5f));

            // Obtenir le foyer du zoom (point entre les deux doigts)
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            // Appliquer le zoom en gardant le foyer du zoom fixe
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            // Mettre à jour l'imageView avec la nouvelle matrice
            imageView1.setImageMatrix(matrix);

            // Mettre à jour les marques (si nécessaire)
            //updateImageViewMarkPosition(markView);

            return true; // Indique que l'événement a été géré
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


    private void displayPdf(String fichier, String type) {
        try {

            File subDir = new File(appDir, nomEntreprise+"/"+nomChantier+"/"+type); // Sous-répertoire

            File file = new File(subDir, fichier);
            Log.d("Debug", "File Path: " + file.getAbsolutePath());
            Log.d("Debug", "File Exists: " + file.exists());

            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

            if (pdfRenderer != null && pdfRenderer.getPageCount() > 0) {
                Log.d("PDF_Debug", "PDF Loaded Successfully with " + pdfRenderer.getPageCount() + " pages.");
            } else {
                Log.e("PDF_Debug", "Failed to load PDF.");
            }

            PdfRenderer.Page page = pdfRenderer.openPage(0); // Ouvrir la première page

           bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

            Log.d("Bitmap_Debug", "Bitmap Width: " + bitmap.getWidth() + ", Height: " + bitmap.getHeight());

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Afficher la page rendue dans l'ImageView
            runOnUiThread(() -> {
                imageView1.setImageBitmap(bitmap);
                scaleFactor = 3.0f;//(float) imageView1.getWidth() / (float) bitmap.getWidth();
                float dx = (float) imageView1.getWidth() / 2 - (float) bitmap.getWidth() / 2 * scaleFactor;
                float dy = (float) imageView1.getHeight() / 2 - (float) bitmap.getHeight() / 2 * scaleFactor;

                Log.d("scalefactor zoomfile", "Bitmap scalefactor: "+scaleFactor);

                matrix.reset();
                matrix.postTranslate(dx, dy);
                matrix.setScale(scaleFactor, scaleFactor);
                imageView1.setImageMatrix(matrix);
                layout.invalidate();
            });

            // Fermer la page et le renderer
            page.close();


        } catch (IOException e) {
            Log.e("displayPdf", "Erreur lors de l'affichage du PDF : " + e.getMessage(), e);

        }
    }

    protected CompletableFuture<Boolean> downloadFileAsync(Context context, OkHttpClient clientHttp, String url, String file) {
        return downloadFile(context, clientHttp, url, file, type, nomClient, nomChantier, nomEntreprise);
    }




    public static CompletableFuture<Boolean> downloadFile(Context context, OkHttpClient client, String url, String fileName, String type, String nomClient, String nomChantier, String nomEntreprise) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(url + "/" + nomClient + "/" + nomChantier + "/" + type + "/" + fileName)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Download", "Download failed: " + e.getMessage());
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show());
                }
                result.complete(false);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        Log.e("Download", "Server returned error: " + response.code());
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                        //return;
                    }



                    File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + type);

                    if (!subDir.exists() && !subDir.mkdirs()) {
                        Log.e("Download", "Failed to create directory");
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                        //return;
                    }

                    File file = new File(subDir, fileName);
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(true);
                    } catch (IOException e) {
                        Log.e("Download", "Error saving file: " + e.getMessage());
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                    }
                } catch (Exception e) {
                    Log.e("Download", "Unexpected error: " + e.getMessage(), e);
                    result.complete(false);
                }
            }
        });

        return result;
    }


    public void handlePdfFile() {

        File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + type);
        File file = new File(subDir, fichier);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Téléchargement en cours...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (file.exists()) {
            Log.d("File_Debug", "File exists: " + file.getAbsolutePath());
            progressDialog.dismiss();
            displayPdf(fichier, type);
        } else {
            Log.e("File_Debug", "File does not exist: " + file.getAbsolutePath());
            downloadFileAsync(this, client, "http://" + MainActivity.ip + ":3000/download", fichier)
                    .thenAccept(success -> {
                        progressDialog.dismiss();
                        if (success) {
                            displayPdf(fichier, type);
                        } else {
                            Toast.makeText(this, "Téléchargement échoué", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .exceptionally(ex -> {
                        progressDialog.dismiss();
                        Log.e("File_Debug", "Error: " + ex.getMessage());
                        Toast.makeText(this, "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        return null;
                    });
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = null;
        if(parentActivity.equals("MyAdapterCom")) intent = new Intent(this, AnnexeCom.class);
        else if(parentActivity.equals("MyAdapterPv")) intent = new Intent(this, AnnexePV.class);
        else if(parentActivity.equals("MyAdapterPlanExecution")) intent = new Intent(this, AnnexePlanExecution.class);
        intent.putExtra("nomEntreprise", nomEntreprise);
        intent.putExtra("entrepriseID", entrepriseID);
        intent.putExtra("typeEntreprise", typeEntreprise);
        intent.putExtra("nomChantier", nomChantier);
        intent.putExtra("nomClient", nomClient);
        intent.putExtra("chantierID", chantierID);
        finish();
        return true;
    }




//    public void handlePdfFile() {
//
//        File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + type);
//        File file = new File(subDir, fichier);
//
//        // Initialisation du ProgressDialog
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Téléchargement en cours...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        if (file.exists()) {
//            runOnUiThread(() -> {
//                progressDialog.dismiss(); // Fermer le ProgressDialog si le fichier est localement disponible
//                Toast.makeText(this, "Fichier local trouvé", Toast.LENGTH_SHORT).show();
//            });
//
//            displayPdf(fichier, type);
//
//
//        } else {
//            CompletableFuture<Boolean> downloadFuture = downloadFileAsync(
//                    this,
//                    new OkHttpClient(),
//                    "http://" + MainActivity.ip + ":3000/download",
//                    fichier
//            );
//
//            downloadFuture.thenAccept(success -> {
//                runOnUiThread(() -> {
//                    progressDialog.dismiss(); // Fermer le ProgressDialog après le téléchargement
//                    if (success) {
//                        displayPdf(fichier, type) ;
//
//
//                    } else {
//                        Toast.makeText(this, "Téléchargement échoué", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }).exceptionally(ex -> {
//                runOnUiThread(() -> {
//                    progressDialog.dismiss(); // Fermer le ProgressDialog en cas d'exception
//                    Toast.makeText(this, "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//                return null;
//            });
//        }
//    }
//

}
