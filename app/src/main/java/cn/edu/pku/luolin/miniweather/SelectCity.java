package cn.edu.pku.luolin.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private List<City> mQueryCityList;
    private List<City> mCurrentCityList;
    private EditText mEditText;
    private TextView mCurrentCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mCurrentCity = (TextView) findViewById(R.id.title_name);
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("main_city_name", "北京");
        mCurrentCity.setText("当前城市：" + cityName);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mCityListView = (ListView) findViewById(R.id.city_list);
        myApplication = MyApplication.getInstance();
        mEditText = (EditText) findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);

        mCityList = myApplication.getCityList();
        this.setCityList();
    }

    private void setCityList() {
        ArrayAdapter<City> mAdapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, mCityList);
        mCurrentCityList = mCityList;
        mCityListView.setAdapter(mAdapter);
        mCityListView.setOnItemClickListener(mCityClickedHandler);
        mCityListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                mEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void setCityList(CharSequence charSequence, String status) {
        mQueryCityList = new ArrayList<City>();
        for (int i = 0; i < mCityList.size(); i++) {
            City city = mCityList.get(i);
            if (status.equals("CITY_NAME")) {
                if (city.getCity().indexOf(charSequence.toString()) != -1) {
                    mQueryCityList.add(city);
                }
            } else if (status.equals("CITY_PINYIN")) {
                if (city.getAllFirstPY().toUpperCase().indexOf(charSequence.toString().toUpperCase()) != -1) {
                    mQueryCityList.add(city);
                } else if (city.getAllPY().toUpperCase().indexOf(charSequence.toString().toUpperCase()) != -1) {
                    mQueryCityList.add(city);
                }
            }
        }
        ArrayAdapter<City> mAdapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, mQueryCityList);
        mCurrentCityList = mQueryCityList;
        mCityListView.setAdapter(mAdapter);
        mCityListView.setOnItemClickListener(mCityClickedHandler);
    }

    private AdapterView.OnItemClickListener mCityClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent();
            intent.putExtra("FROM", "SELECT_CITY_ITEM");
            intent.putExtra("cityCode", mCurrentCityList.get(i).getNumber());
            setResult(RESULT_OK, intent);
            finish();

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
//                i.putExtra("cityCode", "101160101");
                i.putExtra("FROM", "SELECT_CITY_BACK");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.length() == 0) {
                setCityList();
            } else if (!isEnglish(charSequence.toString())){
                setCityList(charSequence, "CITY_NAME");
            } else {
                setCityList(charSequence, "CITY_PINYIN");
            }
        }

        private boolean isEnglish(String str) {
            Pattern pattern = Pattern.compile("^[a-zA-z]*");
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


}
