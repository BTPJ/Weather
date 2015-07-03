package com.text.weather.activity;

import com.example.weather.R;
import com.text.weather.util.HttpUtil;
import com.text.weather.util.HttpUtil.HttpCallbackListener;
import com.text.weather.util.Tools;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private TextView textView_show_weather_title, textView_publish_time,
			textView_time, textView_weather_details, textView_temperature;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_weather);
		initView();
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			textView_publish_time.setText("同步中");
			textView_time.setVisibility(View.INVISIBLE);
			textView_weather_details.setVisibility(View.INVISIBLE);
			textView_temperature.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		textView_show_weather_title = (TextView) findViewById(R.id.textView_show_weather_title);
		textView_publish_time = (TextView) findViewById(R.id.textView_publish_time);
		textView_time = (TextView) findViewById(R.id.textView_time);
		textView_weather_details = (TextView) findViewById(R.id.textView_weather_details);
		textView_temperature = (TextView) findViewById(R.id.textView_temperature);
	}

	/**
	 * 查询县级代号所对应的天气
	 * 
	 * @param countyCode
	 *            县级代号
	 */
	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String urlAddress = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(urlAddress, "countyCode");
	}

	/**
	 * 查询天气代号所对应的天气
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		String urlAddress = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(urlAddress, "weatherCode");
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或天气信息
	 * 
	 * @param urlAddress
	 *            地址
	 * @param type
	 *            类型
	 */
	private void queryFromServer(String urlAddress, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(urlAddress, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Log.d("LTP", response);
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					Tools.weatherParse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}

					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						textView_publish_time.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharePreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		textView_show_weather_title.setText(mSharedPreferences.getString(
				"city_name", ""));
		textView_publish_time.setText("今天"
				+ mSharedPreferences.getString("publish_time", "") + "发布");
		textView_time.setText(mSharedPreferences.getString("current_date", ""));
		textView_weather_details.setText(mSharedPreferences.getString(
				"weather_details", ""));
		textView_temperature.setText(mSharedPreferences.getString("temp2", "")
				+ "～" + mSharedPreferences.getString("temp1", ""));
		textView_time.setVisibility(View.VISIBLE);
		textView_weather_details.setVisibility(View.VISIBLE);
		textView_temperature.setVisibility(View.VISIBLE);

	}

}
