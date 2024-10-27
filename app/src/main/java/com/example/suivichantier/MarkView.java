package com.example.suivichantier;

import android.widget.ImageButton;

public class MarkView {

        private Mark mark;
        private ImageButton imageButton;

        public MarkView(Mark mark, ImageButton imageButton) {
            this.mark = mark;
            this.imageButton = imageButton;
        }

        public Mark getMark() {
            return this.mark;
        }

        public void setMark(Mark mark) {
            this.mark = mark;
        }

        public ImageButton getImageButton() {
            return this.imageButton;
        }

        public void setImageView(ImageButton imageButton) {
            this.imageButton = imageButton;
        }



}

