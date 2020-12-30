package com.example.weathertest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragToday_daum extends Fragment {
    ImageView iv_weather_now;
    TextView tv_weather_now;
    TextView tv_temp_now;
    TextView tv_wind_dir_now;
    TextView tv_wind_speed_now;
    TextView tv_prob_rain_now;
    TextView tv_humidity_now;
    TextView tv_time_now;

    ImageView iv_weather_today1;
    ImageView iv_weather_today2;
    ImageView iv_weather_today3;
    ImageView iv_weather_today4;
    ImageView iv_weather_today5;

    TextView tv_temp_today1;
    TextView tv_temp_today2;
    TextView tv_temp_today3;
    TextView tv_temp_today4;
    TextView tv_temp_today5;

    TextView tv_probrain_today1;
    TextView tv_probrain_today2;
    TextView tv_probrain_today3;
    TextView tv_probrain_today4;
    TextView tv_probrain_today5;

    TextView tv_time_today1;
    TextView tv_time_today2;
    TextView tv_time_today3;
    TextView tv_time_today4;
    TextView tv_time_today5;
    TextView tv_smog;

    String address;
    private View view;
    public static FragToday_daum newInstance(String address){
        FragToday_daum fragToday = new FragToday_daum(address);
        return fragToday;
    }
    FragToday_daum(String address){
        this.address = address;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_today, container,false);

        iv_weather_now = view.findViewById(R.id.iv_weather_now);
        tv_weather_now = view.findViewById(R.id.tv_weather_now);
        tv_time_now = view.findViewById(R.id.tv_time_now);
        tv_temp_now = view.findViewById(R.id.tv_temp_now);
        tv_wind_dir_now = view.findViewById(R.id.tv_wind_dir_now);
        tv_wind_speed_now = view.findViewById(R.id.tv_wind_speed_now);
        tv_prob_rain_now = view.findViewById(R.id.tv_prob_rain_now);
        tv_humidity_now = view.findViewById(R.id.tv_humidity_now);

        iv_weather_today1 = view.findViewById(R.id.iv_weather_today1);
        iv_weather_today2 = view.findViewById(R.id.iv_weather_today2);
        iv_weather_today3 = view.findViewById(R.id.iv_weather_today3);
        iv_weather_today4 = view.findViewById(R.id.iv_weather_today4);
        iv_weather_today5 = view.findViewById(R.id.iv_weather_today5);

        tv_temp_today1 = view.findViewById(R.id.tv_temp_today1);
        tv_temp_today2 = view.findViewById(R.id.tv_temp_today2);
        tv_temp_today3 = view.findViewById(R.id.tv_temp_today3);
        tv_temp_today4 = view.findViewById(R.id.tv_temp_today4);
        tv_temp_today5 = view.findViewById(R.id.tv_temp_today5);

        tv_probrain_today1 = view.findViewById(R.id.tv_probrain_today1);
        tv_probrain_today2 = view.findViewById(R.id.tv_probrain_today2);
        tv_probrain_today3 = view.findViewById(R.id.tv_probrain_today3);
        tv_probrain_today4 = view.findViewById(R.id.tv_probrain_today4);
        tv_probrain_today5 = view.findViewById(R.id.tv_probrain_today5);

        tv_time_today1 = view.findViewById(R.id.tv_time_today1);
        tv_time_today2 = view.findViewById(R.id.tv_time_today2);
        tv_time_today3 = view.findViewById(R.id.tv_time_today3);
        tv_time_today4 = view.findViewById(R.id.tv_time_today4);
        tv_time_today5 = view.findViewById(R.id.tv_time_today5);

        tv_smog = view.findViewById(R.id.tv_smog);
        Handler handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bun = msg.getData();
                String[] temp = bun.getStringArray("temptoday");
                String[] weather = bun.getStringArray("weathertoday");
                String[] windspeed = bun.getStringArray("windspeedtoday");
                String[] winddir = bun.getStringArray("winddirtoday");
                String[] probrain = bun.getStringArray("probraintoday");
                String[] humidity = bun.getStringArray("humiditytoday");
                String[] dust_now =  bun.getStringArray("dustnow");
                String[] time = bun.getStringArray("timeline");
                String time_now = bun.getString("timenow");
                String weather_now = bun.getString("wheathernow");
                String temp_now = bun.getString("tempnow");
                String wind_speed_now = bun.getString("windspeednow");
                String humidity_now = bun.getString("humiditynow");
                int time_now_index = 0;
                for(int i=time.length-1;i>=0;i--){
                    int now = Integer.parseInt(time_now.replace("시",""));
                    int timeline = Integer.parseInt(time[i].replace("시",""));
                    if(now>=timeline)
                        time_now_index = Integer.parseInt(time[i].replace("시",""));
                }
                tv_time_now.setText(time_now+", 현재");
                tv_weather_now.setText(weather_now);
                tv_temp_now.setText(temp_now);
                tv_prob_rain_now.setText("강수확률 : "+probrain[time_now_index]);
                tv_wind_speed_now.setText("풍속 : " + wind_speed_now);
                tv_wind_dir_now.setText("풍향 : " + winddir[time_now_index]);
                tv_humidity_now.setText("습도 : " + humidity_now);
                if(dust_now[1].equals("-"))
                    tv_smog.setText("미세먼지 : "+dust_now[0]);
                else
                    tv_smog.setText("미세먼지 : "+ dust_now[1]+", "+dust_now[0]);

                int n = Integer.parseInt(time_now.replace("시",""));
                switch (weather_now) {
                    case "맑음":
                        if(n >= 6 && n<18)
                            iv_weather_now.setImageResource(R.drawable.sun);
                        else
                            iv_weather_now.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_weather_now.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_weather_now.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_weather_now.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_weather_now.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_weather_now.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_weather_now.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather_now.contains("비") && weather_now.contains("눈")) //비 또는 눈일때
                    iv_weather_now.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather_now.contains("비"))
                    iv_weather_now.setImageResource(R.drawable.rainy);
                else if(weather_now.contains("눈"))
                    iv_weather_now.setImageResource(R.drawable.snowy);

                tv_temp_today1.setText(temp[2]);
                tv_probrain_today1.setText(probrain[2]);
                tv_time_today1.setText(time[2]);
                n = Integer.parseInt(time[2].replace("시",""));
                switch (weather[2]) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_weather_today1.setImageResource(R.drawable.sun);
                        else
                            iv_weather_today1.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_weather_today1.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_weather_today1.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_weather_today1.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_weather_today1.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_weather_today1.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_weather_today1.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather[2].contains("비") && weather[2].contains("눈")) //비 또는 눈일때
                    iv_weather_today1.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather[2].contains("비"))
                    iv_weather_today1.setImageResource(R.drawable.rainy);
                else if(weather[2].contains("눈"))
                    iv_weather_today1.setImageResource(R.drawable.snowy);

                tv_temp_today2.setText(temp[3]);
                tv_probrain_today2.setText(probrain[3]);
                tv_time_today2.setText(time[3]);
                n = Integer.parseInt(time[3].replace("시",""));
                switch (weather[3]) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_weather_today2.setImageResource(R.drawable.sun);
                        else
                            iv_weather_today2.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_weather_today2.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_weather_today2.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_weather_today2.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_weather_today2.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_weather_today2.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_weather_today2.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather[3].contains("비") && weather[3].contains("눈")) //비 또는 눈일때
                    iv_weather_today2.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather[3].contains("비"))
                    iv_weather_today2.setImageResource(R.drawable.rainy);
                else if(weather[3].contains("눈"))
                    iv_weather_today2.setImageResource(R.drawable.snowy);

                tv_temp_today3.setText(temp[4]);
                tv_probrain_today3.setText(probrain[4]);
                tv_time_today3.setText(time[4]);
                n = Integer.parseInt(time[4].replace("시",""));
                switch (weather[4]) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_weather_today3.setImageResource(R.drawable.sun);
                        else
                            iv_weather_today3.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_weather_today3.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_weather_today3.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_weather_today3.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_weather_today3.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_weather_today3.setImageResource(R.drawable.cloud_many_night);
                         break;
                    case "안개":
                        iv_weather_today3.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather[4].contains("비") && weather[4].contains("눈")) //비 또는 눈일때
                    iv_weather_today3.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather[4].contains("비"))
                    iv_weather_today3.setImageResource(R.drawable.rainy);
                else if(weather[4].contains("눈"))
                    iv_weather_today3.setImageResource(R.drawable.snowy);

                tv_temp_today4.setText(temp[5]);
                tv_probrain_today4.setText(probrain[5]);
                tv_time_today4.setText(time[5]);
                n = Integer.parseInt(time[5].replace("시",""));
                switch (weather[5]) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_weather_today4.setImageResource(R.drawable.sun);
                        else
                            iv_weather_today4.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_weather_today4.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_weather_today4.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_weather_today4.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_weather_today4.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_weather_today4.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_weather_today4.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather[5].contains("비") && weather[5].contains("눈")) //비 또는 눈일때
                    iv_weather_today4.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather[5].contains("비"))
                    iv_weather_today4.setImageResource(R.drawable.rainy);
                else if(weather[5].contains("눈"))
                    iv_weather_today4.setImageResource(R.drawable.snowy);

                tv_temp_today5.setText(temp[6]);
                tv_probrain_today5.setText(probrain[6]);
                tv_time_today5.setText(time[6]);
                n = Integer.parseInt(time[6].replace("시",""));
                switch (weather[6]) {
                    case "맑음":
                        if (n >= 6 && n<18)
                            iv_weather_today5.setImageResource(R.drawable.sun);
                        else
                            iv_weather_today5.setImageResource(R.drawable.night);
                        break;
                    case "흐림":
                        iv_weather_today5.setImageResource(R.drawable.cloudy);
                        break;
                    case "구름조금":
                        if (n >= 6 && n<18)
                            iv_weather_today5.setImageResource(R.drawable.cloud_little_day);
                        else
                            iv_weather_today5.setImageResource(R.drawable.cloud_little_night);
                        break;
                    case "구름많음":
                        if (n >= 6 && n<18)
                            iv_weather_today5.setImageResource(R.drawable.cloud_many_day);
                        else
                            iv_weather_today5.setImageResource(R.drawable.cloud_many_night);
                        break;
                    case "안개":
                        iv_weather_today5.setImageResource(R.drawable.fog);
                        break;
                }
                if(weather[6].contains("비") && weather[6].contains("눈")) //비 또는 눈일때
                    iv_weather_today5.setImageResource(R.drawable.rainy_or_snowy);
                else if(weather[6].contains("비"))
                    iv_weather_today5.setImageResource(R.drawable.rainy);
                else if(weather[6].contains("눈"))
                    iv_weather_today5.setImageResource(R.drawable.snowy);
            }
        };
        DaumData daum = new DaumData(handler, address);
        daum.start();
        return view;
    }
}


