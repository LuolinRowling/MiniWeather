package cn.edu.pku.luolin.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.luolin.bean.PostWeather;
import cn.edu.pku.luolin.bean.TodayWeather;

/**
 * Created by luolin on 2016/12/3.
 */
public class XmlUtil {

    public static TodayWeather ParseXML(String xmlData) {
        TodayWeather todayWeather = null;
        List<PostWeather> postWeathers = null;
        PostWeather postWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            Log.d("miniWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("date_1")) {
                                eventType = xmlPullParser.next();
                                postWeathers = new ArrayList<PostWeather>();
                                postWeather = new PostWeather();
                                postWeather.setDate(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high_1")) {
                                eventType = xmlPullParser.next();
                                postWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("type_1")) {
                                eventType = xmlPullParser.next();
                                postWeather.setType(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fl_1")) {
                                eventType = xmlPullParser.next();
                                postWeather.setFengli(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("low_1")) {
                                eventType = xmlPullParser.next();
                                postWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("forecast")) {
                                eventType = xmlPullParser.next();
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                postWeather = new PostWeather();
                                postWeather.setDate(todayWeather.getDate());
                                postWeather.setHigh(todayWeather.getHigh());
                                postWeather.setLow(todayWeather.getLow());
                                postWeather.setFengli(todayWeather.getFengli());
                                postWeather.setType(todayWeather.getType());
                                postWeathers.add(postWeather);
                                dateCount++;
                                postWeather = new PostWeather();
                                postWeather.setDate(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount > 1){
                                eventType = xmlPullParser.next();
                                postWeather = new PostWeather();
                                postWeather.setDate(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high") && highCount > 0) {
                                eventType = xmlPullParser.next();
                                postWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("low") && lowCount > 0) {
                                eventType = xmlPullParser.next();
                                postWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("type") && (typeCount % 2 == 0)) {
                                eventType = xmlPullParser.next();
                                postWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && (typeCount % 2 == 1)) {
                                eventType = xmlPullParser.next();
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && (fengliCount % 2 == 0)) {
                                eventType = xmlPullParser.next();
                                postWeather.setFengli(xmlPullParser.getText());
                                postWeathers.add(postWeather);
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && (fengliCount % 2 == 1)) {
                                eventType = xmlPullParser.next();
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("zhishus")) {
                                todayWeather.setPostWeathers(postWeathers);
                            }
                        }

                        break;
                    //判断当前事件是否为标签元素结束
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应时间
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
}
