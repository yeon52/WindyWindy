package com.example.weathertest;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LinearLayout layout_meteo;
    LinearLayout layout_naver;
    LinearLayout layout_daum;
    TextView tv_address;
    TextView tv_meteo_temp;
    TextView tv_naver_temp;
    TextView tv_daum_temp;
    TextView tv_meteo_time;
    TextView tv_naver_time;
    TextView tv_daum_time;
    TextView tv_meteo_weather;
    TextView tv_naver_weather;
    TextView tv_daum_weather;
    ImageView iv_meteo_weather;
    ImageView iv_naver_weather;
    ImageView iv_daum_weather;

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout_meteo = findViewById(R.id.layout_meteo);
        layout_naver = findViewById(R.id.layout_naver);
        layout_daum = findViewById(R.id.layout_daum);
        tv_address = findViewById(R.id.tv_address);
        tv_meteo_temp= findViewById(R.id.tv_meteo_temp);
        tv_naver_temp= findViewById(R.id.tv_naver_temp);
        tv_daum_temp= findViewById(R.id.tv_daum_temp);
        tv_meteo_time= findViewById(R.id.tv_meteo_time);
        tv_naver_time= findViewById(R.id.tv_naver_time);
        tv_daum_time= findViewById(R.id.tv_daum_time);
        tv_meteo_weather = findViewById(R.id.tv_meteo_weather);
        tv_naver_weather = findViewById(R.id.tv_naver_weather);
        tv_daum_weather = findViewById(R.id.tv_daum_weather);
        iv_meteo_weather= findViewById(R.id.iv_meteo_weather);
        iv_naver_weather= findViewById(R.id.iv_naver_weather);
        iv_daum_weather= findViewById(R.id.iv_daum_weather);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(MainActivity.this);

        final double latitude = gpsTracker.getLatitude();
        final double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        String[] addresses = address.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(addresses)); //주솟값 앞 뒤 하나씩 삭제
        list.remove(addresses.length-1);
        list.remove(0);

        addresses = list.toArray(new String[list.size()]);//삭제를 위해 list로 변경한걸 다시 array로 변경
        final String location = String.join(" ",addresses);
        tv_address.setText("현재위치 :" + location);

        Handler handler_meteo = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bun = msg.getData();
                String[] temp = bun.getStringArray("temptoday");
                String[] timetoday = bun.getStringArray("timetoday");
                String[] weather = bun.getStringArray("weathertoday");
                tv_meteo_time.setText(timetoday[0]);
                tv_meteo_temp.setText(temp[0]);
                tv_meteo_weather.setText(weather[0]);
                int n = Integer.parseInt(timetoday[0].replace("시",""));
                switch (weather[0]) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_meteo_weather.setImageResource(R.drawable.sun);
                        else
                            iv_meteo_weather.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_meteo_weather.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_meteo_weather.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_meteo_weather.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_meteo_weather.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_meteo_weather.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "비":
                        iv_meteo_weather.setImageResource(R.drawable.rainy);
                        break;
                    case "눈":
                        iv_meteo_weather.setImageResource(R.drawable.snowy);
                        break;
                }
                if(weather[0].contains("비") && weather[0].contains("눈")) //비 또는 눈일때
                    iv_meteo_weather.setImageResource(R.drawable.rainy_or_snowy);
            }
        };
        MeteorologyData meteo = new MeteorologyData(handler_meteo, location, latitude, longitude);
        meteo.start();

        Handler handler_naver = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bun = msg.getData();
                String[] temp = bun.getStringArray("temptoday");
                String[] timetoday = bun.getStringArray("timetoday");
                String[] weather = bun.getStringArray("weathertoday");

                int time_now = Integer.parseInt(timetoday[0].replace("시",""));
                if(time_now ==0)
                    time_now=23;
                else
                    time_now -=1;
                tv_naver_time.setText(time_now+"시");
                tv_naver_temp.setText(temp[0]);
                tv_naver_weather.setText(weather[0]);
                int n = Integer.parseInt(timetoday[0].replace("시",""));
                switch (weather[0]) {
                    case "맑음":
                        if(n >= 6 && n<18)
                            iv_naver_weather.setImageResource(R.drawable.sun);
                        else
                            iv_naver_weather.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_naver_weather.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_naver_weather.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_naver_weather.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_naver_weather.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_naver_weather.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_naver_weather.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather[0].contains("비") && weather[0].contains("눈")) //비 또는 눈일때
                    iv_naver_weather.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather[0].contains("비"))
                    iv_naver_weather.setImageResource(R.drawable.rainy);
                else if(weather[0].contains("눈"))
                    iv_naver_weather.setImageResource(R.drawable.snowy);
            }
        };
        NaverData naver = new NaverData(handler_naver, location);
        naver.start();

        Handler handler_daum = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bun = msg.getData();
                String time_now = bun.getString("timenow");
                String weather_now = bun.getString("wheathernow");
                String temp_now = bun.getString("tempnow");
                tv_daum_time.setText(time_now);
                tv_daum_temp.setText(temp_now);
                tv_daum_weather.setText(weather_now);
                int n = Integer.parseInt(time_now.replace("시",""));
                switch (weather_now) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_daum_weather.setImageResource(R.drawable.sun);
                        else
                            iv_daum_weather.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_daum_weather.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_daum_weather.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_daum_weather.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_daum_weather.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_daum_weather.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_daum_weather.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather_now.contains("비") && weather_now.contains("눈")) //비 또는 눈일때
                    iv_daum_weather.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather_now.contains("비"))
                    iv_daum_weather.setImageResource(R.drawable.rainy);
                else if(weather_now.contains("눈"))
                    iv_daum_weather.setImageResource(R.drawable.snowy);
            }
        };
        DaumData daum = new DaumData(handler_daum, location);
        daum.start();

        layout_naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //네이버, 다음, 기상청 순으로 0,1,2 같이 전송
                Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                intent.putExtra("menu",0);
                intent.putExtra("address",location); //주소 전송
                startActivity(intent);
            }
        });

        layout_daum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //네이버, 다음, 기상청 순으로 0,1,2 같이 전송
                Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                intent.putExtra("menu",1);
                intent.putExtra("address",location); //주소 전송
                startActivity(intent);
            }
        });

        layout_meteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //네이버, 다음, 기상청 순으로 0,1,2 같이 전송
                Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                intent.putExtra("menu",2);
                intent.putExtra("address",location); //주소 전송
                intent.putExtra("latitude", latitude); //경도, 위도 전송 (기상청은 경도위도로 날씨 가져옴)
                intent.putExtra("longitude",longitude);
                startActivity(intent);
            }
        });



    }
    //퍼미션 요청의 결과 리턴
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults){
        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
                //위치 값을 가져옴
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission(){
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
    }

    public String getCurrentAddress(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    //GPS활성화 메소드
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}