package com.example.suivichantier;

import android.widget.ImageView;

public class MarkView {

        private Mark mark;
        private ImageView imageView;

        public MarkView(Mark mark, ImageView imageView) {
            this.mark = mark;
            this.imageView = imageView;
        }

        public Mark getMark() {
            return this.mark;
        }

        public void setMark(Mark mark) {
            this.mark = mark;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }



}

