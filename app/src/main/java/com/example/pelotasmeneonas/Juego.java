package com.example.pelotasmeneonas;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private Bitmap bitm;
   private pelotaMenu pelotamenu = new pelotaMenu(100,100,10,800,(float) Math.PI/4,Color.GREEN,this);;
    public Juego(Context c) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        fin = false;
        vAngular = Aleatorio.sgtef((float) -Math.PI, (float) Math.PI);
        angulo = Aleatorio.sgtef((float) -Math.PI, (float) Math.PI);
        px = Aleatorio.sgtef(0, width);
        vidas=(int) (NUMFIGURAS*0.1);
        bitm=BitmapFactory.decodeResource(c.getResources(),R.drawable.pelotamenu);

    }
    public void rjuego(){
        jugando=false;
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

        rectFacil = new RectF(width/2f-200, height/1.5f-700, width/2f+200, height/1.5f-500);
        rectMedio = new RectF(width/2f-200, height/1.5f-400, width/2f+200, height/1.5f-200);
        rectDificil = new RectF(width/2f-200, height/1.5f-100, width/2f+200, height/1.5f+100);
        rectInstrucciones = new RectF(width/2f-350, height/1.5f+200, width/2f+350, height/1.5f+400);
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
                Pelota pel =new Pelota(x, y, rad, 20000 / rad, (float) Math.PI / 4, Color.WHITE, this);
                pel.setUltimo(true);
                pelotas.add(pel);
            }else
                pelotas.add(new Pelota(x, y, rad, 20000 / rad, (float) Math.PI / 4, color, this));

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
        pelotamenu.mover(lapso);
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
            paint.setTextSize(height*width/40000);
            paint.setStrokeWidth(8);
            String vid = "Vidas: "+vidas;
            String rest = "Restantes "+pelotas.size();
            canvas.drawText(vid, width-paint.measureText(vid)-20 , 100, paint);
            canvas.drawText(rest, 0,100,paint);
            paint.setStyle(Paint.Style.FILL);
            pelotas.forEach(p -> p.paint(canvas));
            canvas.restore();
        }else if (instrucciones){
            paint.setAntiAlias(true);
            canvas.drawColor(Color.BLACK);
            canvas.save();
            paint.setColor(Color.WHITE);
            paint.setTextSize(height*width/46000f);
            paint.setStrokeWidth(2);
            String ins = "Toca la bola que se desplaza por encima de todas";
            String ins2 = "para eliminarla. Cada vez que falles se añadirá";
            String ins3 = "una bola más al juego.";
            String ins4 = "Perderás si el número de bolas añadidas supera";
            String ins5 = "el 10% de las iniciales. Elimínalas rapido.";
            String ins6 = "TOCA LA PANTALLA PARA RETORNAR";
            canvas.drawText(ins, rectMedio.centerX() - paint.measureText(ins)/2, rectMedio.centerY() + paint.getTextSize() / 3, paint);
            canvas.drawText(ins2, rectMedio.centerX() - paint.measureText(ins2)/2 , (rectMedio.centerY() + paint.getTextSize() / 3)+50, paint);
            canvas.drawText(ins3, rectMedio.centerX() - paint.measureText(ins3)/2 , (rectMedio.centerY() + paint.getTextSize() / 3)+100, paint);
            canvas.drawText(ins4, rectMedio.centerX() - paint.measureText(ins4)/2, (rectMedio.centerY() + paint.getTextSize() / 3)+200, paint);
            canvas.drawText(ins5, rectMedio.centerX() - paint.measureText(ins5)/2, (rectMedio.centerY() + paint.getTextSize() / 3)+250, paint);
            canvas.drawText(ins6, rectMedio.centerX() - paint.measureText(ins6)/2, (rectMedio.centerY() + paint.getTextSize() / 3)+500, paint);

            canvas.restore();
        } else if (perder) {
            paint.setAntiAlias(true);
            canvas.drawColor(Color.RED);
            canvas.save();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(15);
            String texto= "Perdiste";
            paint.setTextSize(50);
            canvas.drawText(texto, width/2-paint.measureText(texto)/2,height/2,paint);
        } else {
            paint.setAntiAlias(true);
            canvas.drawColor(Color.WHITE);
            canvas.save();
            pelotamenu.paint(canvas,bitm);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectFacil, 50, 50, paint);
            canvas.drawRoundRect(rectMedio, 50, 50, paint);
            canvas.drawRoundRect(rectDificil, 50, 50, paint);
            canvas.drawRoundRect(rectInstrucciones, 50, 50, paint);
            paint.setTextSize(180);
            paint.setStrokeWidth(20);
            canvas.drawText("PELOTAS",width/2f-350,height/3f-450,paint);
            canvas.drawText("MENEONAS",width/2f-480,height/3f-250,paint);
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
            if (perder){
                perder=false;
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
        jugando=false;
        perder=true;


    }

    private void eliminarPelota(int index) {
        pelotas.remove(index);
        pelotas.get(pelotas.size()-1).setUltimo(true);
    }

}
