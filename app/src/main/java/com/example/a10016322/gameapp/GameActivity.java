package com.example.a10016322.gameapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

public class GameActivity extends AppCompatActivity {
    //Code from this program has been used from "Beginning Android Games" by Mario Zechner
    //Review SurfaceView, Canvas, continue
    //for timer, use countdown timer or system timer

    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }



    //----------------------------GameSurface Below This Line--------------------------
    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener{

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap hungryOmNom, sadOmNom, candy;
        Paint paintProperty;
        Canvas canvas;
        Rect candyRect, omNomRect;

        int screenWidth;
        int screenHeight;
        int candyY = 0;
        int candyX = 0;
        int total = 0;
        int score = 0;

        public GameSurface(Context context) {
            super(context);

            holder=getHolder();

            hungryOmNom = BitmapFactory.decodeResource(getResources(),R.drawable.omnom);
            sadOmNom = BitmapFactory.decodeResource(getResources(),R.drawable.sadomnom);
            candy = BitmapFactory.decodeResource(getResources(),R.drawable.candy);

            SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            total = screenWidth/2;
            screenHeight=sizeOfScreen.y;

            paintProperty= new Paint();
            paintProperty.setColor(Color.WHITE);
            paintProperty.setTextSize(36);

        }


        @Override
        public void run() {
            while (running == true){

                if (holder.getSurface().isValid() == false)
                    continue;

                candyRect = new Rect();
                omNomRect = new Rect();
                candyRect.set(candyX, candyY, candyX+92, candyY+92);
                omNomRect.set(total, 835, total+125, 1060);
                if (candyRect.intersect(omNomRect))
                {
                    score++;
                    candyY = -50;
                    candyX = (int)(Math.random()*600)+50;
                    candyRect.set(0,0,0,0);
                }
                canvas= holder.lockCanvas();
                canvas.drawRGB(145,145,145);
                canvas.drawText("Score: "+score, 15, 30, paintProperty);
                canvas.drawBitmap(hungryOmNom,total,835,null);
                canvas.drawBitmap(candy,candyX,candyY,null);
                if (candyY >= screenHeight)
                {
                    canvas.drawBitmap(sadOmNom,total,835,null);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {}
                    if (candyY >= screenHeight+50)
                    {
                        candyY = -50;
                        candyX = (int)(Math.random()*600)+50;
                    }
                }
                holder.unlockCanvasAndPost(canvas);
                candyY+=3;
            }
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }


        @Override
        public void onSensorChanged(SensorEvent event) {
            if ((total < screenWidth - 100) && (total > 0)) {
                if ((event.values[0] > 0) && (event.values[0] < 3)) {
                    total -= 5;
                } else if ((event.values[0] > 3) && (event.values[0] < 10)) {
                    total -= 10;
                } else if ((event.values[0] > -3) && (event.values[0] < 0)) {
                    total += 5;
                } else if ((event.values[0] > -10) && (event.values[0] < -3)) {
                    total += 10;
                }
            }
            else if (total <= 0)
                total = 1;
            else if (total >= screenWidth - 100)
                total = screenWidth - 101;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }//GameSurface

}
