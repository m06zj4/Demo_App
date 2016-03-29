package com.example.away.imageclick;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private ImageView imageView;
    private Bitmap bitmap;

    private TouchLocation TL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        int H = bitmap.getHeight();
        int W = bitmap.getWidth();

        imageView.setMaxHeight(H);
        imageView.setMaxWidth(W);

        TL = new TouchLocation(H, W);
        TL.setDotWithJson("111");

        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.w("123", "X:" + event.getX() + "  Y:" + event.getY());
                    Object oo = TL.analyseTouchLocation(event.getX(), event.getY());
                    if (oo != null) {
                        int out = (int) oo;
                        Toast.makeText(MainActivity.this, String.valueOf(out) + " is click!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
