package com.example.yf.httpurlcon;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends Activity {
    TextView show;

    //---------------------傳上去資料庫的值-----------------------------------------------*/
    String Major = "0";
    String UUID = "386dd978-8d2e-452d-97e9-f495fbdb4e3b";
    //---------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = (TextView) findViewById(R.id.pat);

        //-----------不要問你會怕，當面見到再跟你解釋這兩行
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //-----------
    }

    public void clac(View V) {
        try {
        //-----------設一個空字串接資料庫回傳的資料--
            String total = "";
        //-------------------------------------

            //-----------上傳給資料庫的，這串等同於"major=0&uuid=386dd978-8d2e-452d-97e9-f495fbdb4e3b"
            // 其中major and uuid 是你資料庫欄位名稱， 後面的則是你要傳上去的值
            String urlParameters = "major=" + URLEncoder.encode(Major, "UTF-8") + "&uuid=" + URLEncoder.encode(UUID, "UTF-8");
            URL url;


            HttpURLConnection connection = null;

            try {
                url = new URL("http://120.114.104.60/test/get_map.php");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setDoInput(true);
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                Log.w("mydebug111", urlParameters);

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    total = total + line;
                    show.setText(total);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
