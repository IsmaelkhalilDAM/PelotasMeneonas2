package com.example.pelotasmeneonas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
public class Menu extends View {

    private Paint paint;
    private RectF rectFacil, rectMedio, rectDificil, rectInstrucciones;
    private String textFacil, textMedio, textDificil, textInstrucciones;

    public Menu(Context context) {
        super(context);
        init();
    }

    public Menu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Menu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);

        textFacil = "Facil";
        textMedio = "Medio";
        textDificil = "Dificil";
        textInstrucciones = "Instrucciones";

        rectFacil = new RectF(352, 100, 752, 400);
        rectMedio = new RectF(352, 500, 752, 800);
        rectDificil = new RectF(352, 900, 752, 1200);
        rectInstrucciones = new RectF(252, 1400, 852, 1700);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar los cuadros con bordes redondeados
        canvas.drawRoundRect(rectFacil, 50, 50, paint);
        canvas.drawRoundRect(rectMedio, 50, 50, paint);
        canvas.drawRoundRect(rectDificil, 50, 50, paint);
        canvas.drawRoundRect(rectInstrucciones, 50, 50, paint);

        // Dibujar el texto dentro de los cuadros
        paint.setTextSize(80);
        canvas.drawText(textFacil, rectFacil.centerX() - paint.measureText(textFacil) / 2, rectFacil.centerY() + paint.getTextSize() / 3, paint);
        canvas.drawText(textMedio, rectMedio.centerX() - paint.measureText(textMedio) / 2, rectMedio.centerY() + paint.getTextSize() / 3, paint);
        canvas.drawText(textDificil, rectDificil.centerX() - paint.measureText(textDificil) / 2, rectDificil.centerY() + paint.getTextSize() / 3, paint);
        canvas.drawText(textInstrucciones, rectInstrucciones.centerX() - paint.measureText(textInstrucciones) / 2, rectInstrucciones.centerY() + paint.getTextSize() / 3, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (rectFacil.contains(x, y)) {

                    // Acción para el botón "Facil"
                    Log.d("MenuView", "Botón Facil pulsado");
                } else if (rectMedio.contains(x, y)) {
                    // Acción para el botón "Medio"
                    Log.d("MenuView", "Botón Medio pulsado");
                } else if (rectDificil.contains(x, y)) {
                    // Acción para el botón "Dificil"
                    Log.d("MenuView", "Botón Dificil pulsado");
                } else if (rectInstrucciones.contains(x, y)) {
                    // Acción para el botón "Instrucciones"
                    Log.d("MenuView", "Botón Instrucciones pulsado");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // Si el usuario arrastra el dedo, podemos hacer algo aquí
                break;
            case MotionEvent.ACTION_UP:
                // Si el usuario levanta el dedo, podemos hacer algo aquí
                break;
        }
        return true;
    }

}

