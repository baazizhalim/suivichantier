package com.example.suivichantier;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SuiviChantier extends AppCompatActivity{

    private ImageView imageView1;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float scaleFactor = 1.0f;
    private float focusX, focusY;
    private float image1X, image1Y;
    private ConstraintLayout layout;
    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suivi_chantier);

        imageView1 = findViewById(R.id.image1);

        // Afficher le fichier PDF dans imageView1
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "sample.pdf");
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0);

            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageView1.setImageBitmap(bitmap);

            page.close();
            pdfRenderer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        layout = findViewById(R.id.constraint_layout);

        // Ajouter des marqueurs
        addMarker(100, 200); // Par exemple, ajouter un marqueur à (100, 200)
        addMarker(300, 400); // Par exemple, ajouter un marqueur à (300, 400)

        // Initialiser le ScaleGestureDetector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new GestureListener());
    }

    private void addMarker(float x, float y) {
        ImageView marker = new ImageView(this);
        marker.setImageResource(R.drawable.icons8_note_40); // Remplacez par l'ID de votre image de marqueur
        marker.setId(View.generateViewId());

        layout.addView(marker);

        Marker markerData = new Marker(marker, x, y);
        markers.add(markerData);

        updateMarkerPosition(markerData);
    }

    private void updateMarkerPosition(Marker markerData) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        float scaledX = markerData.x * scaleFactor + image1X;
        float scaledY = markerData.y * scaleFactor + image1Y;

        constraintSet.connect(markerData.marker.getId(), ConstraintSet.LEFT, imageView1.getId(), ConstraintSet.LEFT, (int) scaledX);
        constraintSet.connect(markerData.marker.getId(), ConstraintSet.TOP, imageView1.getId(), ConstraintSet.TOP, (int) scaledY);
        constraintSet.applyTo(layout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float previousScaleFactor = scaleFactor;
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f)); // Limiter le facteur de mise à l'échelle

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            float scaleAdjustmentX = focusX - imageView1.getWidth() / 2;
            float scaleAdjustmentY = focusY - imageView1.getHeight() / 2;

            image1X += scaleAdjustmentX * (scaleFactor - previousScaleFactor);
            image1Y += scaleAdjustmentY * (scaleFactor - previousScaleFactor);

            imageView1.setScaleX(scaleFactor);
            imageView1.setScaleY(scaleFactor);
            imageView1.setTranslationX(image1X);
            imageView1.setTranslationY(image1Y);

            // Mettre à jour les positions des marqueurs
            for (Marker marker : markers) {
                updateMarkerPosition(marker);
            }

            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            image1X -= distanceX;
            image1Y -= distanceY;

            imageView1.setTranslationX(image1X);
            imageView1.setTranslationY(image1Y);

            // Mettre à jour les positions des marqueurs
            for (Marker marker : markers) {
                updateMarkerPosition(marker);
            }
            return true;
        }
    }

    private static class Marker {
        ImageView marker;
        float x, y;

        Marker(ImageView marker, float x, float y) {
            this.marker = marker;
            this.x = x;
            this.y = y;
        }
    }
}




/*
import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.Manifest;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_display = 1;
    private static final int PERMISSION_REQUEST_CODE_download = 100;
    private ImageView pdfImageView;
    private ImageView imageView;

    private float image1X, image1Y;


    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    float scaleFactor = 1.0f;
    int imageViewWidth ;
    int imageViewHeight;
    ConstraintLayout constraintLayout;

    private int anchorX = -200; // Position X de l'image ancrée
    private int anchorY = -200; // Position Y de l'image ancrée

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout=findViewById(R.id.main);
        pdfImageView = findViewById(R.id.pdfImageView);
        imageView = new ImageView(this);
        imageView.setId(View.generateViewId());
        image1X = 0;
        image1Y = 0;
        // Appliquer la position à imageView2 avec le gravity top|left
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_exclamation_48);


        // Fixer les dimensions souhaitées
        int desiredWidth = 60; // largeur souhaitée en pixels
        int desiredHeight = 60; // hauteur souhaitée en pixels

        // Redimensionner le Bitmap
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

        // Affichez le Bitmap redimensionné dans l'ImageView
        imageView.setImageBitmap(resizedBitmap);
        // Fixer les bitmaps aux ImageViews
        constraintLayout.addView(imageView);


        // Initialiser le ScaleGestureDetector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new GestureListener() );
        // Définir la position de imageView2 sur imageView1


//        EdgeToEdge.enable(this);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("Suivi Chantier");
//        }
        pdfImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Retirer le listener pour éviter des appels multiples
                pdfImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Obtenir les dimensions de l'ImageView
                imageViewWidth = pdfImageView.getWidth();
                imageViewHeight = pdfImageView.getHeight();

                // Charger et afficher le PDF maintenant que nous avons les dimensions correctes
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_display);
                } else {
                    displayPdf(imageViewWidth, imageViewHeight);

                    //positionImageView2(-100, -100); // Exemple de position fixe (x=100, y=200)
                    updateImage2Position();
                }

            }
        });

//       showNavigationBar();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST_CODE_download);
//        } else {
//            startDownload();
//        }
    }



    private void positionImageView2(int x, int y) {

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(imageView.getId(), ConstraintSet.LEFT, pdfImageView.getId(), ConstraintSet.LEFT, x);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, pdfImageView.getId(), ConstraintSet.TOP, y);
        constraintSet.applyTo(constraintLayout);

    }

    private void updateImage2Position() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        float scaledX = anchorX * scaleFactor + image1X;
        float scaledY = anchorY * scaleFactor + image1Y;

        constraintSet.connect(imageView.getId(), ConstraintSet.LEFT, pdfImageView.getId(), ConstraintSet.LEFT, (int) scaledX);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, pdfImageView.getId(), ConstraintSet.TOP, (int) scaledY);
        constraintSet.applyTo(constraintLayout);

    }












//    private void showNavigationBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.show(WindowInsets.Type.navigationBars());
//            }
//        } else {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_VISIBLE
//            );
//        }
//    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            image1X -= distanceX;
            image1Y -= distanceY;

            pdfImageView.setTranslationX(image1X);
            pdfImageView.setTranslationY(image1Y);

            // Mettre à jour la position de imageView2 en fonction du déplacement de imageView1
            //positionImageView2((int) (anchorX * scaleFactor + image1X), (int) (anchorY * scaleFactor + image1Y));
            updateImage2Position();
            return true;
        }
    }
        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
            float previousScaleFactor = scaleFactor;
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f)); // Limiter le facteur de mise à l'échelle

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            float scaleAdjustmentX = focusX - (float) pdfImageView.getWidth() / 2;
            float scaleAdjustmentY = focusY - (float) pdfImageView.getHeight() / 2;

            image1X += scaleAdjustmentX * (scaleFactor - previousScaleFactor);
            image1Y += scaleAdjustmentY * (scaleFactor - previousScaleFactor);

                pdfImageView.setScaleX(scaleFactor);
                pdfImageView.setScaleY(scaleFactor);
                pdfImageView.setTranslationX(image1X);
                pdfImageView.setTranslationY(image1Y);

                // Mettre à jour la position de imageView2 en fonction du zoom de imageView1
                //positionImageView2((int) (pdfImageView.getTranslationX()+(anchorX * scaleFactor )), (int) (pdfImageView.getTranslationY()+anchorY * scaleFactor));
                updateImage2Position();

                return true;
            }
        }



    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (!isInitialPositionCaptured) {
//                    initialImage2X = imageView.getX() - pdfImageView.getX();
//                    initialImage2Y = imageView.getY() - pdfImageView.getY();
//                    isInitialPositionCaptured = true;
//                }
//                dX = pdfImageView.getX() - event.getX();
//                dY = pdfImageView.getY() - event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                pdfImageView.animate()
//                        .x(event.getX() + dX)
//                        .y(event.getY() + dY)
//                        .setDuration(0)
//                        .start();
//                updateImage2Position();
//                break;
//            default:
//                return false;
//        }
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = event.getX();
//                lastY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float deltaX = event.getX() - lastX;
//                float deltaY = event.getY() - lastY;
//                // Mettez à jour les coordonnées de votre vue ici (par exemple, translatez l'image).
//                pdfImageView.setTranslationX(pdfImageView.getTranslationX() + deltaX);
//                pdfImageView.setTranslationY(pdfImageView.getTranslationY() + deltaY);
//                lastX = event.getX();
//                lastY = event.getY();
//                constraintLayout.invalidate(); // Redessinez la vue
//                updateImage2Position(scaleFactor);
//                break;
//        }

        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }



    private void startDownload() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://example.com/")  // Remplace par l'URL de base de ton serveur
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FileDownloadService service = retrofit.create(FileDownloadService.class);
        Call<ResponseBody> call = service.downloadFile("path/to/your/file.zip");  // Remplace par le chemin réel de ton fichier

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean isDownloaded = writeResponseBodyToDisk(response.body());
                    if (isDownloaded) {
                        Toast.makeText(MainActivity.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to download file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Server contact failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip");

            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    Log.d("MainActivity", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    private void displayPdf(int width, int height) {
        try {
            File cacheFile = new File(getCacheDir(), "sample.pdf");
            if (isAssetNewer("sample.pdf", cacheFile)) {
                copyAssetToFile("sample.pdf", cacheFile);
            }

            // Copier le fichier PDF dans le répertoire de téléchargement spécifique à l'application
            File externalFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "sample.pdf");
            copyFile(cacheFile, externalFile);

            // Ouvrir le fichier PDF
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(externalFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0); // Ouvrir la première page


            // Créer un bitmap pour rendre la page PDF
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Afficher la page rendue dans l'ImageView
            pdfImageView.setImageBitmap(bitmap);


            // Fermer la page et le renderer
            page.close();
            pdfRenderer.close();
            fileDescriptor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAssetNewer(String assetFileName, File cacheFile) {
        try {
            InputStream asset = getAssets().open(assetFileName);
            long assetTimestamp = asset.available(); // Utilisation de la taille comme approximation du timestamp
            asset.close();
            return assetTimestamp != cacheFile.length();
        } catch (IOException e) {
            return true; // Si une erreur se produit, considérez l'asset comme plus récent
        }
    }

    private void copyAssetToFile(String assetFileName, File destFile) {
        try (InputStream in = getAssets().open(assetFileName);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_download) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSION_REQUEST_CODE_display) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayPdf(imageViewWidth,imageViewHeight);
            } else {
                Log.e("MainActivity", "Permission denied!");
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

        if (id == R.id.action_search) {
            // Handle search action
            return true;
        } else if (id == R.id.action_settings) {
            // Handle settings action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
*/

