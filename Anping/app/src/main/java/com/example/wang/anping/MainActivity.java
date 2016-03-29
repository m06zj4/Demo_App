package com.example.wang.anping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ImageMap imageMap;
    private boolean turn = false;
    private boolean map_OK = false;
    private HttpConnector photoHC, jsonHC;
    private ShortestPath SP;
    private String MapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        Bitmap user = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        Bitmap lock = BitmapFactory.decodeResource(getResources(), R.drawable.lock);
        Bitmap dire = BitmapFactory.decodeResource(getResources(), R.drawable.direction);

        jsonHC = new HttpConnector("http://120.114.104.31/test/get_map2.php");
//        jsonHC = new HttpConnector("http://120.114.104.60/test/get_map2.php");
//        photoHC = new HttpConnector("http://120.114.138.151/img/api");
        photoHC = new HttpConnector("http://120.114.104.31/json/api");

        imageMap = new ImageMap(imageView, user, lock, dire);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    PointF mPointF = new PointF();
                    mPointF.x = event.getX();
                    mPointF.y = event.getY();
                    new DrawMap().execute(mPointF);
                }
                return true;
            }
        });

        checkAndDownloadMap("I-4");
    }

    /*--------------------------------Shortest Path Algorithm Code--------------------------------*/

    public int[] calculateShortestPath(int source, int destination, int option) {
        if (SP == null) {
            return null;
        }

        // SP.floydWarshell.calculateDistance();
        // SP.floydWarshell.output();

        SP.dijkstra.calculateDistance(source);
        SP.dijkstra.output(option, destination);

        //return SP.TextOut;
        return SP.dijkstra.getPath();
    }

    /*-----------------------------------------Map Code-------------------------------------------*/

    public void checkAndDownloadMap(String newMapName) {
        if (newMapName == null) {
            return;
        }
        if (newMapName.equals("")) {
            return;
        }
        if (MapName != null) {
            if (MapName.equals(newMapName)) {
                return;
            }
        }

        int Attempts = 5;

        map_OK = false;

        Toast.makeText(MainActivity.this, getString(R.string.change_map) + " " + newMapName, Toast.LENGTH_SHORT).show();

        while (!map_OK) {
            try {
                new GetMapTask().execute(newMapName);
                MapName = newMapName;
                map_OK = true;
            } catch (Exception e) {
                if (Attempts == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.change_fail), Toast.LENGTH_SHORT).show();
                    break;
                }

                Attempts--;
            }
        }

    }

    private class GetMapTask extends AsyncTask<String, Void, String> {
        String JSON = null;
        Bitmap bitmap = null;
        int NodeTotal = 0;

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 1) {
                JSON = jsonHC.SendPost("Map", params[0]);
                bitmap = photoHC.GetImage("Map", params[0]);
            } else {
                return null;
            }

            if (JSON == null) {
                return null;
            }

            try {
                JSONObject jsonData = new JSONObject(JSON);
                JSONObject information = jsonData.getJSONObject("information");
                NodeTotal = information.getInt("node");

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            if (NodeTotal > 0) {
                SP = new ShortestPath(NodeTotal);
                SP.setMatrixWithJson(JSON);
            } else {
                return null;
            }

            return "ok!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Add your image to your view
            if (s != null) {
                imageMap.setMapImage(bitmap);
                imageMap.setMapDot(JSON);
            }
        }
    }

    private class DrawMap extends AsyncTask<PointF, Void, Boolean> {
        @Override
        protected Boolean doInBackground(PointF... PointFs) {
            if (PointFs.length == 1) {

                boolean draw = false;

                if (map_OK) {

                    if (turn) {
                        if (imageMap.drawLockLocation(PointFs[0].x,PointFs[0].y)) {
                            turn = !turn;
                            draw = true;
                        }
                    } else {
                        if (imageMap.drawUserLocation(PointFs[0].x,PointFs[0].y)) {
                            turn = !turn;
                            draw = true;
                        }
                    }

                    Integer source, destination;

                    source = imageMap.getNodeNumber(imageMap.User);
                    destination = imageMap.getNodeNumber(imageMap.Lock);

                    if (source != null && destination != null) {
                        calculateShortestPath(source, destination, 2);
                        imageMap.drawPath(SP.dijkstra.getPath());
                        draw = true;
                    }
                }

                return draw;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean){
                imageMap.reloadImage();
            }
        }
    }

    /*-----------------------------------------Menu Code------------------------------------------*/

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
        if (id == R.id.change_ksu) {
            checkAndDownloadMap("KSU");
            return true;
        }
        if (id == R.id.change_i4) {
            checkAndDownloadMap("I-4");
            return true;
        }
        if (id == R.id.change_example) {
            checkAndDownloadMap("Example");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
