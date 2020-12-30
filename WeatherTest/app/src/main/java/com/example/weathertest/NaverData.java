package com.example.weathertest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class NaverData extends Thread {
    Bundle bun = new Bundle();
    Handler handler;
    String[] todayTime = new String[8]; //오늘 시간간격(1시간씩 8타임/바람,습도는 7타임)
    String[] tomorrowTime = new String[8]; //내일 시간간격(3시간씩 8타임/바람, 습도는 7타임)
    String[] aftertomorrowTime = new String[8]; //모레 시간간격
    String[] tomorrow_aver = new String[4]; //내일, 모레 오전, 오후 온도
    String[] afterto_aver = new String[4];

    //오늘 시간별
    String[] temp_today = new String[8];
    String[] weather_today = new String[8];
    String[] prob_rain_today = new String[7]; //강수확률
    String[] wind_speed_today = new String[7]; //풍속
    String[] humidity_today = new String[7]; //습도
    String[] wind_dir_today = new String[7]; //바람

    //내일 시간별
    String[] temp_tomorrow = new String[8];
    String[] weather_tomorrow = new String[8];
    String[] prob_rain_tomorrow = new String[7]; //강수확률
    String[] wind_speed_tomorrow = new String[7]; //풍속
    String[] wind_dir_tomorrow = new String[7]; //풍향

    //모레 시간별
    String[] temp_aftertomorrow = new String[8];
    String[] weather_aftertomorrow = new String[8];
    String[] prob_rain_aftertomorrow = new String[7]; //강수확률
    String[] wind_speed_aftertomorrow = new String[7]; //풍속
    String[] wind_dir_aftertomorrow = new String[7]; //풍향
    String dust_now;
    String address;
    NaverData(Handler handler, String address){
        this.handler = handler;
        this.address = address;
    }
    public void run() {
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query="+address+" 날씨";
        Document doc = null; //HTML로 부터 데이터 가져오기
        {
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Elements elem = doc.select("dd.weather_item._dotWrapper"); //온도, 강수확률, 풍속
        String[] Inform1 = elem.text().split(" "); //정보 전부 하나하나 나누기

        Elements elem2 = doc.select("dd.item_condition"); //날씨, 강수량, 풍향
        String[] Inform2 = elem2.text().split(" ");

        List<String> inform2_tmp = new ArrayList<>(Arrays.asList(Inform2));
        int index = inform2_tmp.indexOf("또는");
        while(index !=-1){
            inform2_tmp.set(index-1,inform2_tmp.get(index-1) + " " + inform2_tmp.get(index)+ " " + inform2_tmp.get(index+1));
            inform2_tmp.remove(index);
            inform2_tmp.remove(index);
            Inform2 = inform2_tmp.toArray(new String[inform2_tmp.size()]);
            index = inform2_tmp.indexOf("또는");
        }
        Elements elem3 = doc.select("dd.item_time"); //하루 시간대(한시간 간격)
        String[] Time = elem3.text().split(" ");

        Elements elem4 = doc.select("div.main_info.morning_box");
        String[] future = elem4.text().split(" ");

        Elements elem5 = doc.select("dl.indicator");
        String[] dust = elem5.text().split(" ");
        dust_now = dust[1]; //미세먼지

        index = -1;
        List<String> future_tmp = new ArrayList<>(Arrays.asList(future));
        for(int i=0;i<future_tmp.size();i++){
            if(future_tmp.get(i).endsWith("고")){ //흐리고, 구름많고 등 찾기
                index = i;
                break;
            }
        }
        int index2, index3;
        while(index !=-1){
            index2 = future_tmp.indexOf("한때");
            index3 = future_tmp.indexOf("가끔");
            if(index2 != -1 || index3!=-1)
            {
                future_tmp.set(index,future_tmp.get(index)+" "+future_tmp.get(index+1)+" "+future_tmp.get(index+2));
                future_tmp.remove(index+1);
                future_tmp.remove(index+1);
            }
            else{
                future_tmp.set(index,future_tmp.get(index)+" "+future_tmp.get(index+1));
                future_tmp.remove(index+1);
            }
            future = future_tmp.toArray(new String[future_tmp.size()]);
            index = -1;
            for(int i=0;i<future_tmp.size();i++){
                if(future_tmp.get(i).endsWith("고")) { //흐리고, 구름많고 등 찾기
                    index = i;
                    break;
                }
            }
        }

        List<String> list = new ArrayList<>(Arrays.asList(Time)); //내일로 넘어가면 "내일" 문자열이 저장되어서 그거 삭제, 나중에 따로 설정해줌
        list.removeAll(Collections.singleton("내일"));
        Time = list.toArray(new String[list.size()]);//삭제를 위해 list로 변경한걸 다시 array로 변경
        for (int i = 0; i < 8; i++) {
            if(Time[i].equals("00시"))
                todayTime[i] = Time[i].replace("00","0"); //오늘시간
            else if(Time[i].startsWith("0"))
                todayTime[i] = Time[i].replace("0",""); //오늘시간
            else
                todayTime[i] = Time[i]; //오늘시간

            if(Time[i+30].equals("00시"))
                tomorrowTime[i] = Time[i + 30].replace("00","0"); //내일시간
            else if (Time[i+30].startsWith("0"))
                tomorrowTime[i] = Time[i + 30].replace("0",""); //내일시간
            else
                tomorrowTime[i] = Time[i+30]; //내일시간
        }
        ////////////오늘날씨//////////////
        //온도, 날씨
        for(int i=0;i<8;i++){
            temp_today[i] = Inform1[i].replace("도","C");
            weather_today[i] = Inform2[i];
        }
        //강수,바람,습도
        for(int i=0;i<7;i++){
            prob_rain_today[i] = Inform1[i+8];
            wind_speed_today[i] = Inform1[i+15];
            wind_dir_today[i] = Inform2[i+16];
            humidity_today[i] = Inform1[i+22];
        }
        /////////////내일날씨///////////

        //오전오후 온도, 날씨
        tomorrow_aver[0] = future[1].replace("도씨","");
        tomorrow_aver[1] = future[2];
        tomorrow_aver[2] = future[6].replace("도씨","");
        tomorrow_aver[3] = future[7];
        //온도, 날씨
        for(int i=0;i<8;i++){
            temp_tomorrow[i] = Inform1[i+29].replace("도","C");
            weather_tomorrow[i] = Inform2[i+30];
        }

        //강수,바람,습도
        for(int i=0;i<7;i++){
              prob_rain_tomorrow[i] = Inform1[i+37];
//            wind_speed_tomorrow[i] = Inform1[i+44];
//            wind_dir_tomorrow[i] = Inform2[i+46];
//            humidity_tomorrow[i] = Inform1[i+51];
        }

        //모레날씨
        //데이터가 없는경우가 있었어서 없으면 아직 모른다고 표기하기!
        //오전오후 온도,날씨
        afterto_aver[0] = future[11].replace("도씨","");
        afterto_aver[1] = future[12];
        afterto_aver[2] = future[16].replace("도씨","");
        afterto_aver[3] = future[17];
        int empty;
        if(Inform1.length !=87){
            //데이터 없음 표기
            empty = 0;
        }
        else{
            empty = 1;
            //온도, 날씨
            for(int i=0;i<8;i++){
                temp_aftertomorrow[i] = Inform1[i+58].replace("도","C");;
                weather_aftertomorrow[i] = Inform2[i+60];
                if(Time[i+60].startsWith("0"))
                    aftertomorrowTime[i] = Time[i+60].replace("0","");
                else
                    aftertomorrowTime[i] = Time[i+60];
            }
            //강수
            for(int i=0;i<7;i++){
                prob_rain_aftertomorrow[i] = Inform1[i+66];
//                wind_speed_aftertomorrow[i] = Inform1[i+73];
//                wind_dir_aftertomorrow[i] = Inform2[i+76];
//                humidity_aftertomorrow[i] = Inform1[i+80];
            }
        }

        //전달
        bun.putStringArray("temptoday",temp_today);
        bun.putStringArray("weathertoday",weather_today);
        bun.putStringArray("windspeedtoday",wind_speed_today);
        bun.putStringArray("winddirtoday",wind_dir_today);
        bun.putStringArray("probraintoday",prob_rain_today);
        bun.putStringArray("humiditytoday",humidity_today);
        bun.putStringArray("timetoday",todayTime);
        bun.putStringArray("timetomorrow",tomorrowTime);
        bun.putStringArray("temptomorrow",temp_tomorrow);
        bun.putStringArray("weathertomorrow",weather_tomorrow);
        bun.putStringArray("probraintomorrow",prob_rain_tomorrow);
        bun.putStringArray("tomorrowaver",tomorrow_aver);
        bun.putStringArray("aftertoaver",afterto_aver);
        bun.putStringArray("tempafterto",temp_aftertomorrow);
        bun.putStringArray("weatherafterto",weather_aftertomorrow);
        bun.putStringArray("probrainafterto",prob_rain_aftertomorrow);
        bun.putString("dusttoday",dust_now);
        bun.putInt("isempty",empty);
        Message msg = handler.obtainMessage();
        msg.setData(bun);
        handler.sendMessage(msg);
    }
}


