package com.example.pelotasmeneonas;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class pelotaMenu {
    private float x;
    private float y;
    private float radio;
    private float vx;
    private float vy;
    private Juego juego;
    private Paint paint;
    private boolean ultimo;

    public boolean ultimo() {
        return ultimo;
    }

    public boolean isUltimo() {
        return ultimo;
    }

    public void setUltimo(boolean ultimo) {
        this.ultimo = ultimo;

    }

    public pelotaMenu(float x, float y , float radio, float v, float dir, int color, Juego juego) {
        this.x = x;
        this.y = y;
        this.radio = radio;
        vx = v * (float) Math.cos(dir);
        vy = v * (float) Math.sin(dir);
        this.juego = juego;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        ultimo=false;
    }

    public void mover(float lapso) {
        x += vx * lapso / 500000000f;
        if (x + radio >= juego.getWidth()) {
            x -= (x + radio - juego.getWidth()) * 2;
            vx = -vx;
        } else if (x - radio <= 0) {
            x += (radio - x) * 2;
            vx = -vx;
        }
        y += vy * lapso / 500000000f;
        if (y + radio >= juego.getHeight()) {
            y -= (y + radio - juego.getHeight()) * 2;
            vy = -vy;
        } else if (y - radio <= 0) {
            y += (radio - y) * 2;
            vy = -vy;
        }
    }
    public boolean estaEnArea(float x, float y) {
        float dx = x - this.x;
        float dy = y - this.y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        return distancia <= radio;
    }

    public void paint(Canvas canvas,Bitmap bitmap) {

        canvas.drawBitmap(bitmap,x-bitmap.getWidth(),y-bitmap.getHeight(),paint);

    }

}
