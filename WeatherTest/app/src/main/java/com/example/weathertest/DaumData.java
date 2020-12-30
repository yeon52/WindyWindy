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
import java.util.List;

class DaumData extends Thread {

    //내일, 모레 오전, 오후 온도 날씨
    String[] tomorrow_aver = new String[4];
    String[] afterto_aver = new String[4];
    //오늘 시간별
    String[] temp_today = new String[8];
    String[] weather_today = new String[8];
    String[] prob_rain_today = new String[8]; //강수확률
    String[] wind_speed_today = new String[8]; //풍속
    String[] humidity_today = new String[8]; //습도
    String[] wind_dir_today = new String[8]; //풍향
    //내일 시간별
    String[] temp_tomorrow = new String[8];
    String[] weather_tomorrow = new String[8];
    String[] prob_rain_tomorrow = new String[8]; //강수확률
    String[] wind_speed_tomorrow = new String[8]; //풍속
    String[] humidity_tomorrow = new String[8]; //습도
    String[] wind_dir_tomorrow = new String[8]; //풍향
    //모레 시간별
    String[] temp_aftertomorrow = new String[8];
    String[] weather_aftertomorrow = new String[8];
    String[] prob_rain_aftertomorrow = new String[8]; //강수확률
    String[] wind_speed_aftertomorrow = new String[8]; //풍속
    String[] humidity_aftertomorrow = new String[8]; //습도
    String[] wind_dir_aftertomorrow = new String[8]; //풍향

    String[] wind_today = new String[8]; //풍속 풍향 합쳐져있음
    String[] wind_tomorrow = new String[8];
    String[] wind_aftertomorrow = new String[8];

    String time_now;
    String wheather_now;
    String temp_now;
    String wind_speed_now;
    String humidity_now;
    String[] timeline = new String[8]; //시간간격은 3일다 똑같음 3시간간격

    Bundle bun = new Bundle();
    Handler handler;
    String address;

    DaumData(Handler handler, String address) {
        this.handler = handler;
        this.address = address;
    }

    public void run() {
        String url = "https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&sq=&o=&q=" + address + " 날씨";
        Document doc = null; //HTML로 부터 데이터 가져오기
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elem = doc.select("td"); //온도, 강수확률, 풍속, 풍향, 습도
        String[] Inform1 = elem.text().split(" "); //정보 전부 하나하나 나누기

        Elements elem2 = doc.select("div.cont_today"); //현재날씨
        String[] Today = elem2.text().split(" ");

        int index = -1;
        List<String> Today_tmp = new ArrayList<>(Arrays.asList(Today));
        for(int i=0;i<Today_tmp.size();i++){
            if(Today_tmp.get(i).endsWith("고")){ //흐리고, 구름많고 등 찾기
                index = i;
                break;
            }
        }
        if (index != -1) {
            Today_tmp.set(index, Today_tmp.get(index) + " " + Today_tmp.get(index + 1));
            Today_tmp.remove(index + 1);
            Today = Today_tmp.toArray(new String[Today_tmp.size()]);
        }
        index = Today_tmp.indexOf("또는");
        while (index != -1) {
            Today_tmp.set(index - 1, Today_tmp.get(index - 1) + " " + Today_tmp.get(index) + " " + Today_tmp.get(index + 1));
            Today_tmp.remove(index);
            Today_tmp.remove(index);
            Today = Today_tmp.toArray(new String[Today_tmp.size()]);
            index = Today_tmp.indexOf("또는");
        }

        index = Today_tmp.indexOf("눈또는");
        while (index != -1) {
            Today_tmp.set(index,"눈 또는");
            Today_tmp.set(index, Today_tmp.get(index) + " " + Today_tmp.get(index+1));
            Today_tmp.remove(index+1);
            Today = Today_tmp.toArray(new String[Today_tmp.size()]);
            index = Today_tmp.indexOf("눈또는");
        }

        Elements elem3 = doc.select("div.cont_tomorrow"); //내일, 모레 오전, 오후 날씨
        String[] Tomorrow = elem3.text().split(" ");

        index = -1;
        List<String> Tomorrow_tmp = new ArrayList<>(Arrays.asList(Tomorrow));
        for(int i=0;i<Tomorrow_tmp.size();i++){
            if(Tomorrow_tmp.get(i).endsWith("고")){ //흐리고, 구름많고 등 찾기
                index = i;
                break;
            }
        }
        int index2, index3;
        while (index != -1) {
            index2 = Tomorrow_tmp.indexOf("한때");
            index3 = Tomorrow_tmp.indexOf("가끔");
            if (index2 != -1 || index3 != -1) {
                Tomorrow_tmp.set(index, Tomorrow_tmp.get(index) + " " + Tomorrow_tmp.get(index + 1) + " " + Tomorrow_tmp.get(index + 2));
                Tomorrow_tmp.remove(index + 1);
                Tomorrow_tmp.remove(index + 1);
            } else {
                Tomorrow_tmp.set(index, Tomorrow_tmp.get(index) + " " + Tomorrow_tmp.get(index + 1));
                Tomorrow_tmp.remove(index + 1);
            }
            Tomorrow = Tomorrow_tmp.toArray(new String[Tomorrow_tmp.size()]);
            index = -1;
            for(int i=0;i<Tomorrow_tmp.size();i++){
                if(Tomorrow_tmp.get(i).endsWith("고")){ //흐리고, 구름많고 등 찾기
                    index = i;
                    break;
                }
            }
        }

        index = Tomorrow_tmp.indexOf("또는");
        while (index != -1) {
            Tomorrow_tmp.set(index - 1, Tomorrow_tmp.get(index - 1) + " " + Tomorrow_tmp.get(index) + " " + Tomorrow_tmp.get(index + 1));
            Tomorrow_tmp.remove(index);
            Tomorrow_tmp.remove(index);
            Tomorrow = Tomorrow_tmp.toArray(new String[Tomorrow_tmp.size()]);
            index = Tomorrow_tmp.indexOf("또는");
        }

        index = Tomorrow_tmp.indexOf("눈또는");
        while (index != -1) {
            Tomorrow_tmp.set(index,"눈 또는");
            Tomorrow_tmp.set(index, Tomorrow_tmp.get(index) + " " + Tomorrow_tmp.get(index+1));
            Tomorrow_tmp.remove(index+1);
            Tomorrow = Tomorrow_tmp.toArray(new String[Tomorrow_tmp.size()]);
            index = Tomorrow_tmp.indexOf("눈또는");
        }

        Elements elem4 = doc.select("th"); //시간
        String[] Time = elem4.text().split(" ");

        //현재 날씨
        int tmp_idx = Arrays.asList(Today).indexOf("현재풍속");
        time_now = Today[0];
        wheather_now = Today[2];
        temp_now = Today[3];
        wind_speed_now = Today[tmp_idx + 1];
        humidity_now = Today[tmp_idx + 3];
        String[] dust_now = new String[2];
        dust_now[0] = Today[tmp_idx + 5];
        if (Today.length <= 15)
            dust_now[1] = "-";
        else {
            dust_now[1] = Today[tmp_idx + 6];
            dust_now[1] = dust_now[1].replaceAll("\\(", "");
            dust_now[1] = dust_now[1].replaceAll("\\)", "");
        }

        for (int i = 0; i < 8; i++)
            timeline[i] = Time[i + 1];

        for (int i = 0; i < 8; i++) {
            weather_today[i] = Inform1[2 * i];
            temp_today[i] = Inform1[2 * i + 1];
            prob_rain_today[i] = Inform1[i + 16];
        }

        int n = 0;
        while (true) {
            if (Inform1[n].contains("풍")) {
                index = n;
                break;
            }
            n++;
        }
        for (int i = 0; i < 8; i++) {
            wind_today[i] = Inform1[i + index];
            humidity_today[i] = Inform1[i + index + 8];

            weather_tomorrow[i] = Inform1[2 * i + index + 16];
            temp_tomorrow[i] = Inform1[2 * i + index + 17];
            prob_rain_tomorrow[i] = Inform1[i + index + 32];
        }
        n = index + 40;
        while (true) {
            if (Inform1[n].contains("풍")) {
                index = n;
                break;
            }
            n++;
        }
        for (int i = 0; i < 8; i++) {
            wind_tomorrow[i] = Inform1[i + index];
            humidity_tomorrow[i] = Inform1[i + index + 8];
        }

        //풍속, 풍향 나누기
        for (int i = 0; i < wind_today.length; i++) {
            index = wind_today[i].indexOf("풍");
            wind_dir_today[i] = wind_today[i].substring(0, index);
            wind_speed_today[i] = wind_today[i].substring(index + 1);

            index = wind_tomorrow[i].indexOf("풍");
            wind_dir_tomorrow[i] = wind_tomorrow[i].substring(0, index);
            wind_speed_tomorrow[i] = wind_tomorrow[i].substring(index + 1);
        }

        int empty = 0;
        if (Inform1[104].contains("풍")) { //모레 정보가 있을경우
            empty = 1;
            for (int i = 0; i < 8; i++) {
                weather_aftertomorrow[i] = Inform1[2 * i + 80];
                temp_aftertomorrow[i] = Inform1[2 * i + 81];
                prob_rain_aftertomorrow[i] = Inform1[i + 96];
                wind_aftertomorrow[i] = Inform1[i + 104];
                humidity_aftertomorrow[i] = Inform1[i + 112];
            }
            for (int i = 0; i < 8; i++) {
                index = wind_aftertomorrow[i].indexOf("풍");
                wind_dir_aftertomorrow[i] = wind_aftertomorrow[i].substring(0, index);
                wind_speed_aftertomorrow[i] = wind_aftertomorrow[i].substring(index + 1);
                humidity_aftertomorrow[i] = humidity_aftertomorrow[i].replaceAll("\\.0", ""); //습도 소수점빼기
            }
        }

        //내일, 모레 온도, 날씨 순
        tomorrow_aver[0] = Tomorrow[2];
        tomorrow_aver[1] = Tomorrow[1];
        tomorrow_aver[2] = Tomorrow[5];
        tomorrow_aver[3] = Tomorrow[4];
        afterto_aver[0] = Tomorrow[8];
        afterto_aver[1] = Tomorrow[7];
        afterto_aver[2] = Tomorrow[11];
        afterto_aver[3] = Tomorrow[10];

        //습도 소수점 빼기
        for (int i = 0; i < 8; i++) {
            humidity_today[i] = humidity_today[i].replaceAll("\\.0", "");
            //humidity_tomorrow[i] = humidity_tomorrow[i].replaceAll("\\.0","");
        }

        //전달
        bun.putStringArray("temptoday", temp_today);
        bun.putStringArray("weathertoday", weather_today);
        bun.putStringArray("windspeedtoday", wind_speed_today);
        bun.putStringArray("winddirtoday", wind_dir_today);
        bun.putStringArray("probraintoday", prob_rain_today);
        bun.putStringArray("humiditytoday", humidity_today);
        bun.putStringArray("timeline", timeline);
        bun.putString("timenow", time_now);
        bun.putString("wheathernow", wheather_now);
        bun.putString("tempnow", temp_now);
        bun.putString("windspeednow", wind_speed_now);
        bun.putString("humiditynow", humidity_now);
        bun.putStringArray("temptomorrow", temp_tomorrow);
        bun.putStringArray("weathertomorrow", weather_tomorrow);
        bun.putStringArray("probraintomorrow", prob_rain_tomorrow);
        bun.putStringArray("tomorrowaver", tomorrow_aver);
        bun.putStringArray("aftertoaver", afterto_aver);
        bun.putStringArray("tempafterto", temp_aftertomorrow);
        bun.putStringArray("weatherafterto", weather_aftertomorrow);
        bun.putStringArray("probrainafterto", prob_rain_aftertomorrow);
        bun.putStringArray("dustnow", dust_now);
        bun.putInt("isempty", empty);
        Message msg = handler.obtainMessage();
        msg.setData(bun);
        handler.sendMessage(msg);
    }
}


