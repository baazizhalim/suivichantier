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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.room.Room;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

public class Zoom extends AppCompatActivity {

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
    protected   List<Mark> marks = new ArrayList<>();
    protected   List<MarkView> markViews = new ArrayList<>();
    private Plan plan;
    private List<Plan> plans = new ArrayList<>();
    private OkHttpClient client;

    private ProgressDialog progressDialog;
    private ExecutorService executorService;
    private Handler handler;

    private GestureDetector gestureDetector;
    //public   int hauteur;

    private int entrepriseID ;
    private int chantierID ;
    private int lotID ;
    private String nomEntreprise ;
    private String nomChantier ;
    private String typeLot ;
    private String typeEntreprise;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        client = new OkHttpClient();
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        Intent intent = getIntent();
        entrepriseID = intent.getIntExtra("entrepriseID",0);
        nomEntreprise = intent.getStringExtra("nomEntreprise");
        typeEntreprise = intent.getStringExtra("typeEntreprise");
        nomChantier = intent.getStringExtra("nomChantier");
        chantierID = intent.getIntExtra("chantierID",0);
        lotID = intent.getIntExtra("lotID", 0);
        typeLot = intent.getStringExtra("typeLot");


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION}, PERMISSION_REQUEST_CODE);
//        }

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        layout = findViewById(R.id.layout);
        imageView1 = findViewById(R.id.image1);
        bareFichier = findViewById(R.id.bareFichiers);
        //hauteur = getSupportActionBar().getHeight();
        //bareFichier.setPadding(0, getSupportActionBar().getHeight(), 0, 0);
        plans = mDatabase.planDao().getAllPlans(lotID);

        displayFiles(plans);


        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new GestureListener());



        imageView1.setOnTouchListener((v, event) -> {

            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
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

                    markViews.forEach(Zoom.this::updateImageViewMarkPosition);
                    break;
                }


                default:
                    return false;
            }
            return true;
        });


    }

    private void displayFiles(List<Plan> plans) {
        for (Plan planAffiche : plans) {
            plan = planAffiche;
            ImageView imageView = new ImageView(this);
            imageView.setId(planAffiche.getPlanID());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Ajouter des marges (facultatif)
            layoutParams.setMargins(16, 16, 16, 16);

            // Appliquer les paramètres de mise en page à l'ImageView
            imageView.setLayoutParams(layoutParams);

            // Définir une image pour l'ImageView (par exemple, à partir des ressources)
            imageView.setImageResource(android.R.drawable.btn_star_big_on);

            // Ajouter l'ImageView au LinearLayout
            bareFichier.addView(imageView);

            int index = plans.indexOf(planAffiche);
            TextView textView = new TextView(this);
            textView.setText("plan_" + index);

            textView.setLayoutParams(layoutParams);

            bareFichier.addView(textView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int planID=((ImageView)v).getId();

                    Plan planaAfficher = plans.stream().filter(plan-> plan.getPlanID() == planID).findFirst().orElse(null);
                    showProgressDialog();
                    executorService.execute(Zoom.this::downloadFile);
                    markViews.forEach(markview -> {
                        layout.removeView(markview.getImageView());
                    });
                    markViews.clear();
                    marks.clear();
                    scaleFactor = 1.0f;
                    matrix = new Matrix();
                    imageView1.setImageMatrix(matrix);
                    displayPdf(planaAfficher.getFile(),planaAfficher.getDescription(), planaAfficher.getPlanID());
                    marks = getAllMarks(planAffiche.getPlanID());
                    drawAllMarks(marks);
                    handler.post(Zoom.this::hideProgressDialog);
                }
            });

        }

    }


    private void downloadFile() {
        File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
        File subDir = new File(appDir, plan.getDescription()); // Sous-répertoire

        File file = new File(subDir, plan.getFile());
        if(!file.exists()){
            runOnUiThread(() -> {
                        Toast.makeText(Zoom.this, "telechargement du fichier ", Toast.LENGTH_SHORT).show();
                    });
            downloadFile("http://" + MainActivity.ip + ":3000/download", plan.getFile(), plan.getDescription());
    }
        else {
            runOnUiThread(() -> {
                Toast.makeText(Zoom.this, "fichier local ", Toast.LENGTH_SHORT).show();
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

    protected    void createMark(MotionEvent e, String[] valeurs) {
        if (valeurs != null) {
        matrix.getValues(matrixValues);
        float posX = (e.getX() -matrixValues[Matrix.MTRANS_X])/ matrixValues[Matrix.MSCALE_X] ;
        float posY = (e.getY() -matrixValues[Matrix.MTRANS_Y])/ matrixValues[Matrix.MSCALE_Y];
        Mark mark = new Mark("mark_" + e.getX() + "_" + e.getY(), valeurs[0], valeurs[3], (int) posX, (int) posY, valeurs[1], valeurs[2], valeurs[5], valeurs[4], imageView1.getId());

        addMarker(mark);
        // traitement de creation d'une mark
        }
       
        
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            imageView1.setImageMatrix(matrix);
            markViews.forEach(Zoom.this::updateImageViewMarkPosition);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Gérer le double clic ici
            handleDoubleClick(e);
            return super.onDoubleTap(e);
        }
    }

    private void handleDoubleClick(MotionEvent e) {

            afficherFenetreMark(null,e);

    }

    private   void drawMark(Mark mark) {
        ImageView icone = new ImageView(imageView1.getContext());

// Assurez-vous que 'mark' n'est pas nul et vérifiez le type de marque
        if (mark != null) {
            switch (mark.getType()) {
                case "reserve":
                    icone.setImageResource(R.drawable.icons8_exclamation_48);
                    break;
                case "tache":
                    icone.setImageResource(R.drawable.icons8_checklist_48);
                    break;
                case "note":
                    icone.setImageResource(R.drawable.icons8_note_40);
                    break;
                default:
                    // Ajoutez un cas par défaut pour gérer les types inconnus
                    Log.e("Zoom", "Type de marque inconnu: " + mark.getType());
                    break;
            }
            icone.setContentDescription(mark.getMarkID());
            if(mark.getStatut().equals("SNT"))icone.setBackgroundColor(Color.RED);
            else if (mark.getStatut().equals("TNV"))icone.setBackgroundColor(Color.BLUE);
            else icone.setBackgroundColor(Color.GREEN);
            // Ajoutez l'imageView au layout


            // Assurez-vous que layout n'est pas nul et est bien un FrameLayout
            if (layout != null) {
                layout.addView(icone);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) icone.getLayoutParams();
                params.width = 60;
                params.height = 60;
                params.leftMargin = (int) mark.getPosx();
                params.topMargin = (int) mark.getPosy();
                icone.setLayoutParams(params);
            } else {
                Log.e("Zoom", "Le layout n'est pas un FrameLayout");
            }
        } else {
            Log.e("Zoom", "Mark est null");
        }
        //traitement de l'icone mark
        MarkView markView=new MarkView(mark, icone);
        markViews.add(markView);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MarkView mv= markViews.stream().filter(markView -> markView.getImageView().equals(view)).findFirst().orElse(null);
                afficherFenetreMark(markView,null);
            }
        });
        mDatabase.markDao().insert(mark);
        updateImageViewMarkPosition(markView);

    }



    public   void afficherFenetreMark(MarkView mv, MotionEvent e) {


        MyBottomSheetDialogFragment bottomSheet = new MyBottomSheetDialogFragment(mv, e,this);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");

         }



    private void updateImageViewMarkPosition(MarkView markView) {
        matrix.getValues(matrixValues);
        float scaledX = markView.getMark().getPosx() * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X];
        float scaledY = markView.getMark().getPosy() * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y];
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) markView.getImageView().getLayoutParams();
        params.leftMargin = (int) scaledX;
        params.topMargin = (int) scaledY;
        markView.getImageView().setLayoutParams(params);
    }

    private void chargerImage(String fichier) {

        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fichier);
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
    }

    private   void addMarker(Mark mark) {
        marks.add(mark);
        drawMark(mark);

    }

    private List<Mark> getAllMarks(int planID) {
        List<Mark> marks;
        marks = mDatabase.markDao().getAllMarks(planID);
        return marks;
    }

    private void drawAllMarks(List<Mark> marks) {
        marks.forEach(this::drawMark);
    }

    protected   MarkView findMarker(String markID){
        return markViews.stream().filter(markView -> markView.getMark().getMarkID().equals(markID)).findFirst().orElse(null);
    }

//    private void download(String file,String type) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://"+MainActivity.ip+":3000/")  // Remplace par l'URL de base de ton serveur
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            FileDownloadService service = retrofit.create(FileDownloadService.class);
//            Call<ResponseBody> call = service.downloadFile("http://"+MainActivity.ip+"3000/download/type/file");  // Remplace par le chemin réel de ton fichier
//
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//                        boolean isDownloaded = writeResponseBodyToDisk(response.body());
//                        if (isDownloaded) {
//                            Toast.makeText(Zoom.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(Zoom.this, "Failed to download file", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(Zoom.this, "Server contact failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Toast.makeText(Zoom.this, "Download failed", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//    private boolean writeResponseBodyToDisk(ResponseBody body) {
//            try {
//                File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip");
//
//                InputStream inputStream = null;
//                FileOutputStream outputStream = null;
//
//                try {
//                    byte[] fileReader = new byte[4096];
//                    long fileSize = body.contentLength();
//                    long fileSizeDownloaded = 0;
//
//                    inputStream = body.byteStream();
//                    outputStream = new FileOutputStream(futureStudioIconFile);
//
//                    while (true) {
//                        int read = inputStream.read(fileReader);
//                        if (read == -1) {
//                            break;
//                        }
//
//                        outputStream.write(fileReader, 0, read);
//                        fileSizeDownloaded += read;
//
//                        Log.d("MainActivity", "file download: " + fileSizeDownloaded + " of " + fileSize);
//                    }
//
//                    outputStream.flush();
//                    return true;
//                } catch (IOException e) {
//                    return false;
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    if (outputStream != null) {
//                        outputStream.close();
//                    }
//                }
//            } catch (IOException e) {
//                return false;
//            }
//        }


    private void displayPdf(String fichier, String type, int planID) {
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
            imageView1.setId(planID);


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

//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Gonfler le menu; cela ajoute des éléments à la barre d'outils si elle est présente
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }

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

    private void downloadFile(String url, String fileName, String type) {
        Request request = new Request.Builder().url(url + "/" + type + "/" + fileName).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Download failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(Zoom.this, "Download failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server returned error: " + response.code());
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Server error", Toast.LENGTH_SHORT).show());
                    return;
                }

                File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
                File subDir = new File(appDir, type); // Sous-répertoire

                // Créez le sous-répertoire s'il n'existe pas
                if (!subDir.exists()) {
                    if (!subDir.mkdirs()) {
                        Log.e(TAG, "Failed to create directory");
                        runOnUiThread(() -> Toast.makeText(Zoom.this, "Failed to create directory", Toast.LENGTH_SHORT).show());
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
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Download complete", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    Log.e(TAG, "Error saving file: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Error saving file", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    interface PostTraitementAction {
        void run(String[] valeurs);
    }

    class ValeursContainer {
        String[] valeurs;

        public ValeursContainer(String[] valeurs) {
            this.valeurs = valeurs;
        }

        public String[] getValeurs() {
            return valeurs;
        }

        public void setValeurs(String[] valeurs) {
            this.valeurs = valeurs;
        }
    }


    interface AfterBottomSheetClosed {
        void executeAfterSheetClosed(String[] result);
    }

}

