package com.example.suivichantier;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateImage extends Dialog {

    private Uri photoURI;
    private final DrawingView drawingView;
    private final Bitmap bitmap;
    private final String file;
    float scaleFactor ;//= 3.0f;
    Matrix matrix1 = new Matrix();

    public UpdateImage(@NonNull Context context, Bitmap bitmap, String file) {
        super(context);
        this.bitmap = bitmap;
        this.file = file;

        setContentView(R.layout.update_image);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout layout = findViewById(R.id.lay);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        drawingView = new DrawingView(getContext(), bitmap);
        drawingView.setLayoutParams(params);
        layout.addView(drawingView);
        // Affichez l'image chargée dans l'ImageView
        //scaleFactor = (float) drawingView.getWidth() / (float) bitmap.getWidth() ;
        //drawingView.canvas.scale(scaleFactor, scaleFactor);
        //float dx = (float) drawingView.getWidth() / 2 - (float) bitmap.getWidth() / 2 ;//* scaleFactor;
        //float dy = (float) drawingView.getHeight() / 2 - (float) bitmap.getHeight() / 2;// * scaleFactor;
        //drawingView.canvas.translate(dx, dy);


        ImageButton save = findViewById(R.id.save);
        ImageButton close = findViewById(R.id.close);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageWithDrawing(drawingView, file);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });


        show();

    }


    private void saveImageWithDrawing(DrawingView drawingView, String file) {
        Bitmap bitmapWithDrawing = drawingView.getBitmap();
        saveBitmap(bitmapWithDrawing, file);
    }

    private void saveBitmap(Bitmap bitmap, String path) {
        FileOutputStream out = null;
        try {
            File file = new File(path);
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Classe pour la vue de dessin personnalisée
    public class DrawingView extends View {

        private Paint paint;
        private Path path;
        private final Bitmap bitmap;
        protected Canvas canvas;
        private final Paint bitmapPaint;

        public DrawingView(Context context, Bitmap backgroundBitmap) {
            super(context);
            initPaint();
            bitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(bitmap);
            bitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        private void initPaint() {
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    canvas.drawPath(path, paint);
                    path.reset();
                    break;
            }
            invalidate();
            return true;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }


}
