package cn.edu.pku.luolin.util;

import cn.edu.pku.luolin.miniweather.R;

/**
 * Created by luolin on 2016/12/3.
 */
public class ImageUtil {

    public static int GetImageByType(String type) {
        int imageResource = -1;

        switch (type) {
            case "暴雪":
                imageResource =  R.drawable.biz_plugin_weather_baoxue;
                break;
            case "暴雨":
                imageResource = R.drawable.biz_plugin_weather_baoyu;
                break;
            case "大暴雨":
                imageResource = R.drawable.biz_plugin_weather_dabaoyu;
                break;
            case "大雪":
                imageResource = R.drawable.biz_plugin_weather_daxue;
                break;
            case "大雨":
                imageResource = R.drawable.biz_plugin_weather_dayu;
                break;
            case "多云":
                imageResource = R.drawable.biz_plugin_weather_duoyun;
                break;
            case "雷阵雨":
                imageResource = R.drawable.biz_plugin_weather_leizhenyu;
                break;
            case "雷阵雨冰雹":
                imageResource = R.drawable.biz_plugin_weather_leizhenyubingbao;
                break;
            case "晴":
                imageResource = R.drawable.biz_plugin_weather_qing;
                break;
            case "沙尘暴":
                imageResource = R.drawable.biz_plugin_weather_shachenbao;
                break;
            case "特大暴雨":
                imageResource = R.drawable.biz_plugin_weather_tedabaoyu;
                break;
            case "雾":
                imageResource = R.drawable.biz_plugin_weather_wu;
                break;
            case "小雪":
                imageResource = R.drawable.biz_plugin_weather_xiaoxue;
                break;
            case "小雨":
                imageResource = R.drawable.biz_plugin_weather_xiaoyu;
                break;
            case "阴":
                imageResource = R.drawable.biz_plugin_weather_yin;
                break;
            case "雨夹雪":
                imageResource = R.drawable.biz_plugin_weather_yujiaxue;
                break;
            case "阵雪":
                imageResource = R.drawable.biz_plugin_weather_zhenxue;
                break;
            case "阵雨":
                imageResource = R.drawable.biz_plugin_weather_zhenyu;
                break;
            case "中雪":
                imageResource = R.drawable.biz_plugin_weather_zhongxue;
                break;
            case "中雨":
                imageResource = R.drawable.biz_plugin_weather_zhongyu;
                break;
        }
        return imageResource;
    }

    public static int GetImageByPM25(int pm25) {
        int imageResource = -1;

        if (pm25 <= 50) {
            imageResource = R.drawable.biz_plugin_weather_0_50;
        } else if (pm25 <= 100) {
            imageResource = R.drawable.biz_plugin_weather_51_100;
        } else if (pm25 <= 150) {
            imageResource = R.drawable.biz_plugin_weather_101_150;
        } else if (pm25 <= 200) {
            imageResource = R.drawable.biz_plugin_weather_151_200;
        } else if (pm25 <= 300) {
            imageResource = R.drawable.biz_plugin_weather_201_300;
        } else {
            imageResource = R.drawable.biz_plugin_weather_greater_300;
        }

        return imageResource;
    }
}
