package com.example.get_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MainActivity extends AppCompatActivity {
    double lat;
    double lon;
    String username;
    String checkid = "네트워크 환경이 원활하지 않습니다.";
    private Button button1;
    private static String IP_ADDRESS = "ec2-52-193-213-157.ap-northeast-1.compute.amazonaws.com";
    private static String TAG = "phptest";
    private TextView txtResult;
    private EditText mEditTextName;
    private TextView mTextViewResult;
    List<Bus_stop> stopList = new ArrayList<Bus_stop>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button1);
        mEditTextName = (EditText) findViewById(R.id.editText_main_name);
        mTextViewResult = (TextView) findViewById(R.id.textView_main_result);
        txtResult = (TextView) findViewById(R.id.txtResult);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Bus_stop defalt = new Bus_stop();
        for (int i = 0; i < 5; i++)
            stopList.add(defalt);
        checkPermission();  // 파일 저장 권한 체크 -----------------------------------------------------------------------------
        String line = null;
        String readStr = "";
        File idFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/userid.txt"); // 저장 경로
        if(!idFile.exists()){
            show2();
        }
        else{
            try {
                BufferedReader buf = new BufferedReader(new FileReader(idFile));
                while((line=buf.readLine())!=null){
                    readStr += line;
                }
                Toast.makeText(getApplicationContext(), readStr, Toast.LENGTH_SHORT).show();
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
// 파일 생성------------------------------------------------------------------------------------------------------------------------

        //show2();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                } else {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String provider = location.getProvider();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    double altitude = location.getAltitude();

                    txtResult.setText("위치정보 : " + provider + "\n" +
                            "위도 : " + longitude + "\n" +
                            "경도 : " + latitude + "\n" +
                            "고도  : " + altitude);

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);

                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/dist/index.php?lat=" + latitude + "&lng=" + longitude);
                    Toast.makeText(getApplicationContext(), Double.toString(latitude) + " "+longitude, Toast.LENGTH_LONG).show();

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show(); //여기에 딜레이 후 시작할 작업들을 입력

                    }
                }, 100);

            }
        });




    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            txtResult.setText("위치정보 : " + provider + "\n" +
                    "위도 : " + longitude + "\n" +
                    "경도 : " + latitude + "\n" +
                    "고도  : " + altitude);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray stopArray = jsonObject.getJSONArray("distance");
                Log.d(TAG, "POST response  distance- " + jsonObject.getString("distance"));

                for (int i = 0; i < stopArray.length(); i++) {
                    JSONObject stopObject = stopArray.getJSONObject(i);
                    Log.d(TAG, "POST response  stopArray- " + stopArray.getString(i));
                    Bus_stop stop = new Bus_stop();

                    stop.setKo_name(stopObject.getString("ko_name"));
                    stop.setStop_id(stopObject.getString("stop_id"));
                    stop.setLatitude(stopObject.getString("latitude"));
                    stop.setLongitude(stopObject.getString("longitude"));
                    stop.setDist(stopObject.getString("dist"));
                    Log.d(TAG, "POST response  0- " );

                    if(stopList.size() < 5){
                        stopList.add(stop);
                    }
                    else{
                        stopList.set(i,stop);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Bus_stop bus = stopList.get(0);
            mTextViewResult.setText(bus.getKo_name());
            Log.d(TAG, "POST response  0- " + stopList.size());
            Log.d(TAG, "POST response  1- " + stopList.get(1));
            Log.d(TAG, "POST response  2- " + stopList.get(2));
            Log.d(TAG, "POST response  3- " + stopList.get(3));
            Log.d(TAG, "POST response  4- " + stopList.get(4));
            //Log.d(TAG, "POST response  5- " + stopList.get(5));
            //Log.d(TAG, "POST response  6- " + stopList.get(6));
            //Log.d(TAG, "POST response  7- " + stopList.get(7));

        }


        @Override
        protected String doInBackground(String... params) {

            //String name = (String)params[1];
            //String country = (String)params[2];

            String serverURL = (String) params[0];
            //String postParameters = "name=" + name + "&country=" + country;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();


                /*
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();*/


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK || responseStatusCode == HttpURLConnection.HTTP_CREATED) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


    public class Bus_stop {
        private String ko_name = "네트워크가 원활하지 않습니다.";
        private String stop_id = "네트워크가 원활하지 않습니다.";
        private String latitude = "네트워크가 원활하지 않습니다.";
        private String longitude = "네트워크가 원활하지 않습니다.";
        private String dist = "네트워크가 원활하지 않습니다.";

        public String getKo_name() {
            return ko_name;
        }

        public String getStop_id() {
            return stop_id;
        }
        public String getLatitude() {
            return latitude;
        }
        public String getLongitude() {
            return longitude;
        }

        public String getDist() {
            return dist;
        }

        public void setKo_name(String ko_name) {
            this.ko_name = ko_name;
        }

        public void setStop_id(String stop_id) {
            this.stop_id = stop_id;
        }
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        public void setDist(String dist) {
            this.dist = dist;
        }

    }

    void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("정류장을 선택하세요");
        final CharSequence[] items = {stopList.get(0).getKo_name(), stopList.get(1).getKo_name(), stopList.get(2).getKo_name(), stopList.get(3).getKo_name(), stopList.get(4).getKo_name()};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                lat = Double.parseDouble(stopList.get(pos).getLatitude());
                lon = Double.parseDouble(stopList.get(pos).getLongitude());
                Toast.makeText(getApplicationContext(), items[pos] + Double.toString(lat) + " "+lon, Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        builder.show();
    }

    void show2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("id를 만드세요");
        final EditText name = new EditText(this);
        builder.setView(name);
        //builder.setMessage("Test for preventing dialog close");
        builder.setPositiveButton("Test",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {   username = name.getText().toString();
                check_id task = new check_id();
                task.execute("http://ec2-52-193-213-157.ap-northeast-1.compute.amazonaws.com/index.php?id=%27"+username+"%27");
                //Do stuff, possibly set wantToCloseDialog to true then...
                Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();


                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.i(TAG, "checkid = "+checkid.length());
                        if(checkid.equals("중복되지 않은 id\n")) { // 중복체크 된 아이디를 파일로 저장-----------------------------------------------------------------------
                            File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()); // 저장 경로

                            if (!saveFile.exists()) { // 폴더 없을 경우
                                saveFile.mkdir(); // 폴더 생성
                            }
                            try {
                                BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile + "/" + "userid.txt", false));
                                buf.append(username); // 파일 쓰기
                                buf.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "userid.txt가 생성됨", Toast.LENGTH_SHORT).show();//--------------------------------------------------

                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "id 생성 성공", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "중복된 id", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
            }
        });



    }
    public void checkPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }



    class check_id extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            checkid = result;
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            //String name = (String)params[1];
            //String country = (String)params[2];

            String serverURL = (String)params[0];
            //String postParameters = "name=" + name + "&country=" + country;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();


                /*
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();*/


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK || responseStatusCode == HttpURLConnection.HTTP_CREATED ){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                    sb.append("\n");
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}