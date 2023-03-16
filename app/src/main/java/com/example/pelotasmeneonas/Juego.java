package com.example.pelotasmeneonas;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Juego implements Runnable, View.OnTouchListener {

    private int NUMFIGURAS;
    private Paint paint;
    private RectF rectFacil, rectMedio, rectDificil, rectInstrucciones;
    private String textFacil, textMedio, textDificil, textInstrucciones;
    private final ArrayList<Pelota> pelotas = new ArrayList<>();
    private SurfaceHolder holder;
    private float width;
    private float height;
    private volatile boolean fin;
    private Thread gameLoop;
    private float vAngular = 35;
    private float angulo = 0;
    private float px;
    private float py;
    private int vidas;
    private boolean jugando = false;
    private boolean instrucciones;
    public Juego() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        fin = false;
        vAngular = Aleatorio.sgtef((float) -Math.PI, (float) Math.PI);
        angulo = Aleatorio.sgtef((float) -Math.PI, (float) Math.PI);
        px = Aleatorio.sgtef(0, width);
        vidas=(int) (NUMFIGURAS*0.1);

    }
    public void init(SurfaceHolder holder, int width, int height) {
        this.holder = holder;
        this.width = width;
        this.height = height;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);

        textFacil = "Facil";
        textMedio = "Medio";
        textDificil = "Dificil";
        textInstrucciones = "Instrucciones";

        rectFacil = new RectF(352, 500, 752, 700);
        rectMedio = new RectF(352, 800, 752, 1000);
        rectDificil = new RectF(352, 1100, 752, 1300);
        rectInstrucciones = new RectF(252, 1500, 852, 1700);
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    public void iniciar() {


        px = width / 2f;
        py = height / 2f;

        int xmin = 50;
       // int xmax = width - 50;
        int ymin = 50;
      //  int ymax = height - 50;
        int anchomin = (int) (width * 0.1f);
        int anchomax = (int) (width * 0.5f);
        int altomin = (int) (height * 0.1f);
        int altomax = (int) (height * 0.5f);
        int radiomin = width > height ? (int) (width * .1f) : (int) (height * .1f);
        int radiomax = width > height ? (int) (width * .25f) : (int) (height * .25f);
        jugando=true;
        vidas= (int) (NUMFIGURAS*0.1);
        crearPelotas();

    }

    private void crearPelotas() {
        for (int i = 0; i < NUMFIGURAS; i++) {
            int x = Aleatorio.sgte(50, (int) (width - 50));
            int y = Aleatorio.sgte(50, (int) (height - 50));
            int al = Aleatorio.sgte(300, 700);
            int rad = al / 3;
            int color = Color.rgb(Aleatorio.sgte(1, 256), Aleatorio.sgte(1, 256), Aleatorio.sgte(1, 256));
            if (pelotas.size()==NUMFIGURAS-1){
                Pelota pel =new Pelota(x, y, rad, 9000 / rad, (float) Math.PI / 4, Color.WHITE, this);
                pel.setUltimo(true);
                pelotas.add(pel);
            }else
                pelotas.add(new Pelota(x, y, rad, 9000 / rad, (float) Math.PI / 4, color, this));

        }

    }

    static float FPS = 60;
    static float NPF = 1000000000F / FPS;

    public void run() {
        fin = false;
        long t0 = System.nanoTime(), t1, lapso;
        float nanos = 0;
        while (!fin) {
            t1 = System.nanoTime();
            lapso = t1 - t0;
            t0 = t1;
            nanos += lapso;
            if (nanos >= NPF) {
                nanos -= NPF;
                siguiente(NPF);
                pintar();
            }
        }
    }

    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }

    private void siguiente(float lapso) {
        pelotas.forEach(p -> p.mover(lapso));
        if (pelotaSeleccionada != null) {
            pelotas.remove(pelotaSeleccionada);
            pelotaSeleccionada = null;
        }
    }
    private boolean perder=false;
    private void pintar(Canvas canvas) {

        if (jugando) {
            paint.setAntiAlias(true);
            canvas.drawColor(Color.BLACK);
            canvas.save();
            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            canvas.drawText("Vidas: " + vidas, width / 2, 70, paint);
            canvas.drawText("Restantes "+pelotas.size(),40,70,paint);
            paint.setStyle(Paint.Style.FILL);
            pelotas.forEach(p -> p.paint(canvas));
            canvas.restore();
        }else if (instrucciones){
            paint.setAntiAlias(true);
            canvas.drawColor(Color.BLACK);
            canvas.save();
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setStrokeWidth(4);
            String ins = "Toca la bola que se desplaza por encima de todas";
            String ins2 = "para eliminarla. Cada vez que falles se añadirá";
            String ins3 = "una bola más al juego.";
            String ins4 = "Perderás si el número de bolas añadidas supera";
            String ins5 = "el 10% de las iniciales. Elimínalas rapido.";
            String ins6 = "TOCA LA PANTALLA PARA RETORNAR";
            canvas.drawText(ins, rectMedio.centerX() - paint.measureText(textMedio)-350 , rectMedio.centerY() + paint.getTextSize() / 3, paint);
            canvas.drawText(ins2, rectMedio.centerX() - paint.measureText(textMedio) -310, (rectMedio.centerY() + paint.getTextSize() / 3)+50, paint);
            canvas.drawText(ins3, rectMedio.centerX() + paint.measureText(textMedio) -350, (rectMedio.centerY() + paint.getTextSize() / 3)+100, paint);
            canvas.drawText(ins4, rectMedio.centerX() - paint.measureText(textMedio) -330, (rectMedio.centerY() + paint.getTextSize() / 3)+150, paint);
            canvas.drawText(ins5, rectMedio.centerX() - paint.measureText(textMedio) -260, (rectMedio.centerY() + paint.getTextSize() / 3)+200, paint);
            canvas.drawText(ins6, rectMedio.centerX() - paint.measureText(textMedio) -250, (rectMedio.centerY() + paint.getTextSize() / 3)+500, paint);

            canvas.restore();
        } else if (perder) {
            paint.setAntiAlias(true);
            canvas.drawColor(Color.RED);
            canvas.save();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(15);
            String texto= "Perdiste";
            float x=0;
            float y= getHeight()/2;
            canvas.drawText(texto,x,y,paint);
            if (x<getWidth()){
                x+=10;
                pintar();
            }else {
                perder=false;
                jugando=true;
            }
        } else {
            paint.setAntiAlias(true);
            canvas.drawColor(Color.WHITE);
            canvas.save();
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectFacil, 50, 50, paint);
            canvas.drawRoundRect(rectMedio, 50, 50, paint);
            canvas.drawRoundRect(rectDificil, 50, 50, paint);
            canvas.drawRoundRect(rectInstrucciones, 50, 50, paint);
            paint.setTextSize(180);
            paint.setStrokeWidth(20);
            canvas.drawText("PELOTAS",160,200,paint);
            canvas.drawText("MENEONAS",60,400,paint);
            paint.setStrokeWidth(7);
            // Dibujar el texto dentro de los cuadros
            paint.setTextSize(80);
            canvas.drawText(textFacil, rectFacil.centerX() - paint.measureText(textFacil) / 2, rectFacil.centerY() + paint.getTextSize() / 3, paint);
            canvas.drawText(textMedio, rectMedio.centerX() - paint.measureText(textMedio) / 2, rectMedio.centerY() + paint.getTextSize() / 3, paint);
            canvas.drawText(textDificil, rectDificil.centerX() - paint.measureText(textDificil) / 2, rectDificil.centerY() + paint.getTextSize() / 3, paint);
            canvas.drawText(textInstrucciones, rectInstrucciones.centerX() - paint.measureText(textInstrucciones) / 2, rectInstrucciones.centerY() + paint.getTextSize() / 3, paint);

            canvas.restore();
        }
    }

    private void pintar() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            synchronized (holder) {
                pintar(canvas);
            }
        } catch (Exception e) {
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }

    private float x1;
    private float y1;
    private Pelota pelotaSeleccionada;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (jugando){
                for (int i = pelotas.size() - 1; i >= 0; i--) {
                    Pelota pelota = pelotas.get(i);
                    if (pelota.estaEnArea(x, y)) {
                        if (pelota.isUltimo()){
                            eliminarPelota(i);
                        }else perderVida();
                        break;
                    }
                }
            } if (instrucciones) {
                    instrucciones=false;
                    pintar();
                    return true;
                }
             if(!instrucciones && !jugando){

                if (rectFacil.contains(x, y)) {
                    NUMFIGURAS=10;
                    iniciar();
                } else if (rectMedio.contains(x, y)) {
                    NUMFIGURAS=25;
                    iniciar();
                } else if (rectDificil.contains(x, y)) {
                    NUMFIGURAS=50;
                    iniciar();
                } else if (rectInstrucciones.contains(x, y)) {
                    pintar();
                    instrucciones=true;
                    return true;

                }
            }
        }
        return true;

    }
    public void perderVida() {
        vidas--;
        int x = Aleatorio.sgte(50, (int) (width - 50));
        int y = Aleatorio.sgte(50, (int) (height - 50));
        int al = Aleatorio.sgte(300, 700);
        int rad = al / 3;
        int color = Color.rgb(Aleatorio.sgte(1, 256), Aleatorio.sgte(1, 256), Aleatorio.sgte(1, 256));
        Pelota pel = new Pelota(x, y, rad, 9000 / rad, (float) Math.PI / 4, color, this);
        pel.setUltimo(true);
        for (Pelota pelota:pelotas) {
            pelota.setUltimo(false);
        }
        pelotas.add(pel);

        if (vidas == 0) {
            reiniciar();
        }
    }

    private void reiniciar() {
        pelotas.clear();
        crearPelotas();

        vidas= (int) (NUMFIGURAS*0.1);

    }
    private void eliminarPelota(int index) {
        pelotas.remove(index);
        pelotas.get(pelotas.size()-1).setUltimo(true);
    }

}
