package cn.edu.pku.luolin.miniweather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.luolin.app.MyApplication;
import cn.edu.pku.luolin.bean.City;
import cn.edu.pku.luolin.bean.PostWeather;
import cn.edu.pku.luolin.bean.TodayWeather;
import cn.edu.pku.luolin.service.MyIntentService;
import cn.edu.pku.luolin.util.NetUtil;
import cn.edu.pku.luolin.util.ImageUtil;
import cn.edu.pku.luolin.util.XmlUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
/**
 * Created by luolin on 2016/9/20.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 10001;
    private static final int NO_TODAY_WEATHER = 10002;

    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private ImageView mLocationBtn;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv, currentTempTv;
    private ImageView weatherImg, pmImg;

    private ProgressBar mUpdateProgress;

    private MyApplication myApplication;

    private LayoutInflater inflater;

    private PostWeatherAdapter mPostWeatherAdapter;

    private ViewPager mWeatherViewPager;

    private List<View> mPostWeatherViews;

    private int[] dots_ids = {R.id.post_iv1, R.id.post_iv2};

    private ImageView[] dots;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                case NO_TODAY_WEATHER:
                    Toast.makeText(getBaseContext(), "查无该城市或地区天气信息", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    IntentFilter intentFilter;

    public LocationClient mLocationClient = null;
    public BDLocationListener mLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            Toast.makeText(getBaseContext(), bdLocation.getDistrict(), Toast.LENGTH_SHORT).show();
            String cityName = bdLocation.getDistrict();

            if (cityName != null && !cityName.equals("")) {
                String cityCode = "";
                myApplication = MyApplication.getInstance();
                List<City> mCityList = myApplication.getCityList();

                for (City city : mCityList) {
                    if (cityName.contains(city.getCity())) {
                        cityCode = city.getNumber();
                        break;
                    }
                }

                if (!cityCode.equals("")) {
                    queryWeatherCode(cityCode);

                    // 写入SharedPreferences中
                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("main_city_code", cityCode);
                    editor.putString("main_city_name", cityName);
                    editor.commit();
                }

            }
            mLocationClient.stop();
            Log.i("BaiduLocationApiDem", bdLocation.getCity() + " " + bdLocation.getLocType());
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        //创建intent过滤器
        intentFilter = new IntentFilter();
        intentFilter.addAction("CURRENT_WEATHER_UPDATE_ACTION");

        //将其注册到intent接收器上
        registerReceiver(intentReceiver, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(intentReceiver);
        stopService(new Intent(getBaseContext(), MyIntentService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String guideStatus = sharedPreferences.getString("guide_status", "NULL");

        if (guideStatus.equals("NULL")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("guide_status", "ALREADY_GUIDE");
            editor.commit();
        }

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mLocationBtn = (ImageView) findViewById(R.id.title_location);
        mLocationBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("miniWeather", "网络OK");
//            Toast.makeText(MainActivity.this, "网络OK", Toast.LENGTH_LONG).show();
        } else {
            Log.d("miniWeather", "网络挂了");
//            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        mUpdateProgress = (ProgressBar) findViewById(R.id.title_update_progress);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);

        initView();
        updatePostWeatherView(null);
        initLocation();

        String cityCode = sharedPreferences.getString("main_city_code", "101010100");
        if (cityCode != null && !cityCode.equals(""))
            queryWeatherCode(cityCode);

        startService(new Intent(getBaseContext(), MyIntentService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        currentTempTv = (TextView) findViewById(R.id.current_temperature);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        currentTempTv.setText("N/A");
        weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
    }

    void updatePostWeatherView(List<PostWeather> postWeathers) {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mWeatherViewPager = (ViewPager) findViewById(R.id.weather_viewpager);

        mPostWeatherViews = new ArrayList<View>();

        for (int i = 0; i < 2; i++) {
            LinearLayout mPostWeatherContainer = (LinearLayout) inflater.inflate(R.layout.post_weather_container, null);
            for (int j = 0; j < 3; j++) {
                View postWeatherView = inflater.inflate(R.layout.post_weather, null);
                if (postWeathers != null) {
                    int index = i * 3 + j;
                    TextView mPostWeekDay = (TextView) postWeatherView.findViewById(R.id.post_week_day);
                    ImageView mPostWeekImg = (ImageView) postWeatherView.findViewById(R.id.post_week_img);
                    TextView mPostTemperature = (TextView) postWeatherView.findViewById(R.id.post_temperature);
                    TextView mPostClimate = (TextView) postWeatherView.findViewById(R.id.post_climate);
                    TextView mPostWind = (TextView) postWeatherView.findViewById(R.id.post_wind);

                    mPostWeekDay.setText(postWeathers.get(index).getDate());
                    int imageResource = ImageUtil.GetImageByType(postWeathers.get(index).getType());
                    if (imageResource != -1) mPostWeekImg.setImageResource(imageResource);
                    mPostTemperature.setText(postWeathers.get(index).getLow() + "~" + postWeathers.get(index).getHigh());
                    mPostClimate.setText(postWeathers.get(index).getType());
                    mPostWind.setText("风力:" + postWeathers.get(index).getFengli());
                }
                mPostWeatherContainer.addView(postWeatherView);
            }

            mPostWeatherViews.add(mPostWeatherContainer);
        }
        mPostWeatherAdapter = new PostWeatherAdapter(mPostWeatherViews, this);

        mWeatherViewPager.setAdapter(mPostWeatherAdapter);

        dots = new ImageView[dots_ids.length];

        for (int i = 0; i < dots_ids.length; i++) {
            dots[i] = (ImageView) findViewById(dots_ids[i]);
        }

        mWeatherViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for (int i = 0; i < dots_ids.length; i++) {
                    if (i == position) {
                        dots[i].setImageResource(R.drawable.page_indicator_focused);
                    } else {
                        dots[i].setImageResource(R.drawable.page_indicator_unfocused);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        currentTempTv.setText(todayWeather.getWendu() + "°C");

        if (todayWeather.getPm25() != null) {
            pmDataTv.setText(todayWeather.getPm25());
            pmQualityTv.setText(todayWeather.getQuality());

            int pm25 = Integer.parseInt(todayWeather.getPm25());

            int imageResource = ImageUtil.GetImageByPM25(pm25);

            if (imageResource != -1)
                pmImg.setImageResource(imageResource);

        } else {
            pmDataTv.setText("N/A");
            pmQualityTv.setText("N/A");
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }

        int imageResource = ImageUtil.GetImageByType(todayWeather.getType());

        if (imageResource != -1)
            weatherImg.setImageResource(imageResource);


        mUpdateBtn.setVisibility(View.VISIBLE);
        mUpdateProgress.setVisibility(View.INVISIBLE);

        updatePostWeatherView(todayWeather.getPostWeathers());

        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

    }

    private TodayWeather queryTodayWeather(final String cityCode, HttpURLConnection conn) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        TodayWeather todayWeather = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                response.append(str);
                Log.d("miniWeather", str);
            }
            String responseStr = response.toString();
            Log.d("miniWeather", responseStr);
            todayWeather = XmlUtil.ParseXML(responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return todayWeather;
    }

    /**
     * @param cityCode
     */
    private void queryWeatherCode(final String cityCode) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                TodayWeather todayWeather = queryTodayWeather(cityCode, conn);
                TodayWeather urbanTodayWeather = queryTodayWeather(cityCode.substring(0, 5) + "0100", conn);
                if (todayWeather.getCity() != null) {

                    // 更新所选城市编号存入sharedPreferences中
                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("main_city_code", cityCode);
                    editor.putString("main_city_name", todayWeather.getCity());
                    editor.commit();

                    Log.d("miniWeather", todayWeather.toString());
                    todayWeather.setPm25(urbanTodayWeather.getPm25());
                    todayWeather.setQuality(urbanTodayWeather.getQuality());
                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeather;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = NO_TODAY_WEATHER;
                    msg.obj = null;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }
        if (view.getId() == R.id.title_update_btn) {
            mUpdateBtn.setVisibility(View.INVISIBLE);
            mUpdateProgress.setVisibility(View.VISIBLE);

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("miniWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("miniWeather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("miniWeather", "网络挂了");
            }
        }
        if (view.getId() == R.id.title_location) {
            mLocationClient.start();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String from = intent.getStringExtra("FROM");
            switch (from) {
                case "SELECT_CITY_ITEM": {
                    String newCityCode = intent.getStringExtra("cityCode");
                    Log.i("MiniWeather", "newCityCode: " + newCityCode);
                    if (newCityCode != null && !newCityCode.equals("")) {
                        Log.d("miniWeather", "选择的城市代码为" + newCityCode);

                        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                            Log.d("miniWeather", "网络OK");
                            queryWeatherCode(newCityCode);
                        } else {
                            Log.d("miniWeather", "网络挂了");
                        }
                    }

                    break;
                }
                case "SELECT_CITY_BACK": {
                    break;
                }
                default: break;
            }

        }
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("CURRENT_WEATHER_UPDATE_ACTION")) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code", "101010100");

                if (NetUtil.getNetworkState(context) != NetUtil.NETWORK_NONE) {
                    Log.d("miniWeather", "网络OK");
                    queryWeatherCode(cityCode);
                } else {
                    Log.d("miniWeather", "网络挂了");
                }
            }
        }
    };

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//使用GPS
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(false);
        mLocationClient.setLocOption(option);
    }
}
