package cn.edu.pku.luolin.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.edu.pku.luolin.app.MyApplication;
import cn.edu.pku.luolin.bean.City;

/**
 * Created by luolin on 2016/10/11.
 */
public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView mCityListView;
    private MyApplication myApplication;
    private List<City> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mCityListView = (ListView) findViewById(R.id.city_list);
        myApplication = MyApplication.getInstance();

        this.setCityList();
    }

    private void setCityList() {
        mCityList =  myApplication.getCityList();
        ArrayAdapter<City> mAdapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, mCityList);
        mCityListView.setAdapter(mAdapter);
        mCityListView.setOnItemClickListener(mCityClickedHandler);
    }

    private AdapterView.OnItemClickListener mCityClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //将选择的城市编号存入sharedPreferences中
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("main_city_code", mCityList.get(i).getNumber());
            editor.commit();

            Intent intent = new Intent();
            intent.putExtra("cityCode", mCityList.get(i).getNumber());
            setResult(RESULT_OK, intent);
            finish();
//            Toast.makeText(SelectCity.this, mCityList.get(i).getNumber(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }


}
