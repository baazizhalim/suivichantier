package com.example.suivichantier;

import android.Manifest;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Zoom1 extends AppCompatActivity {
    private   final int PERMISSION_REQUEST_CODE = 100;
    private   final String TAG = "Zoom";
    protected   ImageView imageView1;
    private float scaleFactor = 1.0f;
    private   Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private   AppDatabase mDatabase;
    private float lastTouchX;
    private float lastTouchY;
    private Bitmap bitmap;
    protected    FrameLayout layout;
    public  Mark mark ;
    public  MarkView markView ;
    private Plan plan;
    private int pagex,pagey;
    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private ExecutorService executorService;
    private Handler handler;
    private int entrepriseID ;
    private int chantierID ;
    private String nomChantier ;
    private String typeLot ;
    private int planID ;
    private String markID ;
    private String nomEntreprise;
    private String typeEntreprise;
    private String lot;
    private int lotID;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private String nomClient;
    private String parentActivity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom1);
        client = new OkHttpClient();
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        Intent intent = getIntent();
        entrepriseID = intent.getIntExtra("entrepriseID",0);
        nomEntreprise = intent.getStringExtra("nomEntreprise");
        typeEntreprise = intent.getStringExtra("typeEntreprise");
        chantierID = intent.getIntExtra("chantierID",0);
        nomChantier = intent.getStringExtra("nomChantier");
        nomClient = intent.getStringExtra("nomClient");
        markID = intent.getStringExtra("markID");
        planID = intent.getIntExtra("planID", 0);
        lotID = intent.getIntExtra("lotID", 0);
        typeLot = intent.getStringExtra("typeLot");
        lot = intent.getStringExtra("lot");
        parentActivity = intent.getStringExtra("parentActivity");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        layout = findViewById(R.id.layout);
        imageView1 = findViewById(R.id.image1);
        plan = mDatabase.planDao().getOnePlan(planID);
        mark=mDatabase.markDao().getOneMark(markID);

        scaleGestureDetector = new ScaleGestureDetector(this, new Zoom1.ScaleListener());
        gestureDetector = new GestureDetector(this, new Zoom1.GestureListener());

        imageView1.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            scaleGestureDetector.onTouchEvent(event);

            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {

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

                    updateImageViewMarkPosition(markView);
                    break;
                }


                default:
                    return false;
            }
            return true;
        });

        //scaleFactor = 1.0f;
        //matrix = new Matrix();
        //imageView1.setImageMatrix(matrix);
        handlePdfFile();


    }




//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            // Gérer le double clic ici
//            //handleDoubleClick(e);
//            return false; //super.onDoubleTap(e);
//        }
//
//    }
//    private void handleDoubleClick(MotionEvent e) {
//
//        afficherFenetreMark(null,e);
//
//    }


    protected CompletableFuture<Boolean> downloadFileAsync(Context context, OkHttpClient clientHttp, String url, String file) {
        return downloadFile(context, clientHttp, url, file, typeLot, nomClient, nomChantier, nomEntreprise);
    }




    public static CompletableFuture<Boolean> downloadFile(Context context, OkHttpClient client, String url, String fileName, String typeLot, String nomClient, String nomChantier, String nomEntreprise) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(url + "/" + nomClient + "/" + nomChantier + "/" + typeLot + "/" + fileName)
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
                        return;
                    }

                    File appDir = context.getFilesDir();
                    File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + typeLot);

                    if (!subDir.exists() && !subDir.mkdirs()) {
                        Log.e("Download", "Failed to create directory");
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                        return;
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
        File appDir = getFilesDir();
        File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + typeLot);
        File file = new File(subDir, plan.getFile());

        // Initialisation du ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Téléchargement en cours...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (file.exists()) {
            runOnUiThread(() -> {
                progressDialog.dismiss(); // Fermer le ProgressDialog si le fichier est localement disponible
                //Toast.makeText(this, "Fichier local trouvé", Toast.LENGTH_SHORT).show();
            });

            if (displayPdf(file, plan.getPlanID())) {
                drawMark();
            }
        } else {
            CompletableFuture<Boolean> downloadFuture = downloadFileAsync(
                    this,
                    new OkHttpClient(),
                    "http://" + MainActivity.ip + ":3000/download",
                    plan.getFile()
            );

            downloadFuture.thenAccept(success -> {
                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Fermer le ProgressDialog après le téléchargement
                    if (success) {
                        File fileN = new File(subDir, plan.getFile());
                        if (displayPdf(fileN, plan.getPlanID())) {
                            drawMark();
                        }
                    } else {
                        Toast.makeText(this, "Téléchargement échoué", Toast.LENGTH_SHORT).show();
                    }
                });
            }).exceptionally(ex -> {
                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Fermer le ProgressDialog en cas d'exception
                    Toast.makeText(this, "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
                return null;
            });
        }
    }



    private boolean displayPdf(File file, int planID) {
        if (!file.exists()) {
            Log.e("displayPdf", "Le fichier n'existe pas : " + file.getAbsolutePath());
            return false;
        }

        try (ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
             PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor)) {

            PdfRenderer.Page page = pdfRenderer.openPage(0);
            pagex = page.getWidth();
            pagey = page.getHeight();
            bitmap = Bitmap.createBitmap(pagex, pagey, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            runOnUiThread(() -> {
                imageView1.setImageBitmap(bitmap);
                imageView1.setId(planID);
                float dx = (float) imageView1.getWidth() / 2 - (float) bitmap.getWidth() / 2;
                float dy = (float) imageView1.getHeight() / 2 - (float) bitmap.getHeight() / 2;
                scaleFactor = 1.0f;//(float) imageView1.getWidth() / (float) bitmap.getWidth();
                matrix.reset();
                matrix.postTranslate(dx, dy);
                matrix.setScale(scaleFactor, scaleFactor);
                imageView1.setImageMatrix(matrix);
                layout.invalidate();
            });

            page.close();
            return true;

        } catch (IOException e) {
            Log.e("displayPdf", "Erreur lors de l'affichage du PDF : " + e.getMessage(), e);
            return false;
        }
    }

//    private void displayPlan() {
//        showProgressDialog();
//        executorService.execute(Zoom1.this::downloadFile);
//        scaleFactor = 1.0f;
//        matrix = new Matrix();
//        imageView1.setImageMatrix(matrix);
//        displayPdf(plan.getFile(), plan.getDescription(), plan.getPlanID());
//        mark=mDatabase.markDao().getOneMark(markID);
//        drawMark();
//        handler.post(Zoom1.this::hideProgressDialog);
//    }
//
//
//    private void downloadFile() {
//        File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
//        File subDir = new File(appDir, nomEntreprise + "/" +nomChantier + "/" +plan.getDescription()); // Sous-répertoire
//
//        File file = new File(subDir, plan.getFile());
//        if(!file.exists()){
//            runOnUiThread(() -> {
//                        Toast.makeText(Zoom1.this, "telechargement du fichier ", Toast.LENGTH_SHORT).show();
//                    });
//            downloadFile("http://" + MainActivity.ip + ":3000/download", plan.getFile(), plan.getDescription());
//    }
//        else {
//            runOnUiThread(() -> {
//                Toast.makeText(Zoom1.this, "fichier local ", Toast.LENGTH_SHORT).show();
//            });
//        }
//    }
//
//    private void downloadFile(String url, String fileName, String type) {
//        Request request = new Request.Builder().url(url + "/" +nomClient+"/"+nomChantier+"/"+ type + "/" + fileName).build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "Download failed: " + e.getMessage());
//                runOnUiThread(() -> Toast.makeText(Zoom1.this, "Download failed", Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    Log.e(TAG, "Server returned error: " + response.code());
//                    runOnUiThread(() -> Toast.makeText(Zoom1.this, "Server error", Toast.LENGTH_SHORT).show());
//                    return;
//                }
//
//                File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
//                File subDir = new File(appDir, nomEntreprise + "/" +nomChantier + "/" +type); // Sous-répertoire
//
//                // Créez le sous-répertoire s'il n'existe pas
//                if (!subDir.exists()) {
//                    if (!subDir.mkdirs()) {
//                        Log.e(TAG, "Failed to create directory");
//                        runOnUiThread(() -> Toast.makeText(Zoom1.this, "Failed to create directory", Toast.LENGTH_SHORT).show());
//                        return;
//                    }
//                }
//
//                File file = new File(subDir, fileName);
//                try (InputStream inputStream = response.body().byteStream();
//                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
//                    byte[] buffer = new byte[2048];
//                    int bytesRead;
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        fileOutputStream.write(buffer, 0, bytesRead);
//                    }
//                    runOnUiThread(() -> Toast.makeText(Zoom1.this, "Download complete", Toast.LENGTH_SHORT).show());
//                } catch (IOException e) {
//                    Log.e(TAG, "Error saving file: " + e.getMessage());
//                    runOnUiThread(() -> Toast.makeText(Zoom1.this, "Error saving file", Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
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



//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 6.5f));
//            matrix.setScale(scaleFactor, scaleFactor);
//            //imageView1.setImageMatrix(matrix);
//            float  dx= (float) imageView1.getWidth() /2- (float)bitmap.getWidth()/2* scaleFactor;
//            float  dy= (float) imageView1.getHeight() /2- (float) bitmap.getHeight() /2* scaleFactor;
//            matrix.postTranslate(dx, dy);
//            imageView1.setImageMatrix(matrix);
//            updateImageViewMarkPosition(markView);
//            return false;
//        }
//    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Calculer le facteur de zoom relatif
            float scaleFactor = detector.getScaleFactor();
            float previousScaleFactor = Zoom1.this.scaleFactor;
            Zoom1.this.scaleFactor *= scaleFactor;
            Zoom1.this.scaleFactor = Math.max(1.0f, Math.min(Zoom1.this.scaleFactor, 6.5f));

            // Obtenir le foyer du zoom (point entre les deux doigts)
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            // Appliquer le zoom en gardant le foyer du zoom fixe
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            // Mettre à jour l'imageView avec la nouvelle matrice
            imageView1.setImageMatrix(matrix);

            // Mettre à jour les marques (si nécessaire)
            updateImageViewMarkPosition(markView);

            return true; // Indique que l'événement a été géré
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Gérer le double clic ici
            //handleDoubleClick(e);
            return false; //super.onDoubleTap(e);
        }

    }




    private   void drawMark() {
        ImageButton icone = new ImageButton(imageView1.getContext());

// Assurez-vous que 'mark' n'est pas nul et vérifiez le type de marque
        if (mark != null) {
            switch (mark.getType()) {
                case "reserve":
                    icone.setImageResource(R.drawable.icons8_exclamation_48);
                    break;
                case "tache":
                    icone.setImageResource(R.drawable.icons8_taache_50);
                    break;
                case "note":
                    icone.setImageResource(R.drawable.icons8_note_50);
                    break;
                default:
                    // Ajoutez un cas par défaut pour gérer les types inconnus
                    Log.e("Zoom", "Type de marque inconnu: " + mark.getType());
                    break;
            }
            icone.setContentDescription(mark.getMarkID());
            if(mark.getStatut().equals("SNT"))icone.setColorFilter(Color.RED);
            else if (mark.getStatut().equals("TNV"))icone.setColorFilter(Color.BLUE);
            else icone.setColorFilter(Color.GREEN);
            // Ajoutez l'imageView au layout


            // Assurez-vous que layout n'est pas nul et est bien un FrameLayout
            if (layout != null) {
                layout.addView(icone);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) icone.getLayoutParams();
                params.width = 60;
                params.height = 60;
                params.leftMargin = mark.getPosx();
                params.topMargin = mark.getPosy();
                icone.setLayoutParams(params);
                markView=new MarkView(mark, icone);
                icone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //MarkView mv= markViews.stream().filter(markView -> markView.getImageButton().equals(view)).findFirst().orElse(null);
                        afficherFenetreMark(markView,null);
                    }
                });

                updateImageViewMarkPosition(markView);

            } else {
                Log.e("Zoom1", "Le layout n'est pas un FrameLayout");
            }
        } else {
            Log.e("Zoom1", "Mark est null");
        }
        //traitement de l'icone mark

    }

    public   void afficherFenetreMark(MarkView mv, MotionEvent e) {


        MyBottomSheetDialogFragment1 bottomSheet = new MyBottomSheetDialogFragment1(mv, e,this,entrepriseID, nomEntreprise,typeEntreprise,nomChantier,typeLot);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");

         }

    private void updateImageViewMarkPosition(MarkView markView) {
        matrix.getValues(matrixValues);
        float scaledX = markView.getMark().getPosx() * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X];
        float scaledY = markView.getMark().getPosy() * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y];
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) markView.getImageButton().getLayoutParams();
        params.leftMargin = (int) scaledX;
        params.topMargin = (int) scaledY;
        markView.getImageButton().setLayoutParams(params);
    }

//    private void displayPdf(String fichier, String type, int planID) {
//        try {
//            File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
//            File subDir = new File(appDir, nomEntreprise + "/" +nomChantier + "/" +type); // Sous-répertoire
//
//            File file = new File(subDir, fichier);
//            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
//            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
//            PdfRenderer.Page page = pdfRenderer.openPage(0); // Ouvrir la première page
//            pagex=page.getWidth();
//            pagey=page.getHeight();
//            // Créer un bitmap pour rendre la page PDF
//            bitmap = Bitmap.createBitmap(pagex,pagey, Bitmap.Config.ARGB_8888);
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//            // Afficher la page rendue dans l'ImageView
//            imageView1.setImageBitmap(bitmap);
//            imageView1.setId(planID);
//            float  dx= (float) imageView1.getWidth() /2- (float) bitmap.getWidth() /2;
//            float  dy= (float) imageView1.getHeight() /2- (float) bitmap.getHeight() /2;
//            matrix.postTranslate(dx, dy);
//            scaleFactor= (float) imageView1.getWidth() / (float) bitmap.getWidth() ;
//            matrix.setScale(scaleFactor, scaleFactor);
//            imageView1.setImageMatrix(matrix);
//            layout.invalidate();
//            // Fermer la page et le renderer
//            page.close();
//            pdfRenderer.close();
//            fileDescriptor.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.filtre) {
//
//            return true;
//        }else if (id == R.id.action_synch_mark) {
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void synchroLocale() {
    }

    private void afficherMark() {
    }

    private void filtrer() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = null;
        if(parentActivity.equals("AnnexeReserves")) intent = new Intent(this, AnnexeReserves.class);
        else if(parentActivity.equals("AnnexeNotes")) intent = new Intent(this, AnnexeNotes.class);
        else if(parentActivity.equals("Annexetaches")) intent = new Intent(this, AnnexeTaches.class);
        intent.putExtra("nomEntreprise", nomEntreprise);
        intent.putExtra("entrepriseID", entrepriseID);
        intent.putExtra("typeEntreprise", typeEntreprise);
        intent.putExtra("nomChantier", nomChantier);
        intent.putExtra("nomClient", nomClient);
        intent.putExtra("chantierID", chantierID);
        finish();
        return true;
    }


}

