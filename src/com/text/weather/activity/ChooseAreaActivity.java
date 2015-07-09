package com.text.weather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.weather.R;
import com.text.weather.bean.City;
import com.text.weather.bean.County;
import com.text.weather.bean.Province;
import com.text.weather.db.WeatherDao;
import com.text.weather.util.HttpUtil;
import com.text.weather.util.Tools;
import com.text.weather.util.HttpUtil.HttpCallbackListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity implements OnItemClickListener {
	private TextView textView_title;
	private ListView listView_show_area;
	private ArrayAdapter<String> adapter;
	private List<String> areaList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private WeatherDao mWeatherDao;
	private int currentLevel;
	private ProgressDialog progressDialog;
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;
	private Province selectedProvice;
	private City selectedCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (mSharedPreferences.getBoolean("city_selected", false)
				&& !(getIntent().getBooleanExtra("switch_area", false))) {
			startActivity(new Intent(this, WeatherActivity.class));
			finish();
		}
		setContentView(R.layout.activity_choose_area);
		initView();
		mWeatherDao = WeatherDao.getInstance(this);
		queryProvinces();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		// TODO Auto-generated method stub
		textView_title = (TextView) findViewById(R.id.textView_title);
		listView_show_area = (ListView) findViewById(R.id.listView_show_area);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, areaList);
		listView_show_area.setAdapter(adapter);
		listView_show_area.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO ListView的点击事件
		switch (currentLevel) {
		case LEVEL_PROVINCE:
			selectedProvice = provinceList.get(position);
			queryCities();
			break;
		case LEVEL_CITY:
			selectedCity = cityList.get(position);
			queryCounties();
			break;
		case LEVEL_COUNTY:
			Intent intent = new Intent(this, WeatherActivity.class);
			String countyCode = countyList.get(position).getCountyCode();
			intent.putExtra("county_code", countyCode);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 查询加载省级数据
	 */
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = mWeatherDao.loadProvinces();
		if (provinceList.size() > 0) {
			areaList.clear();
			for (Province province : provinceList) {
				areaList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView_show_area.setSelection(0);
			textView_title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 
	 * 查询加载市级数据
	 */
	private void queryCities() {
		cityList = mWeatherDao.loadCities(selectedProvice.getId());
		if (cityList.size() > 0) {
			areaList.clear();
			for (City city : cityList) {
				areaList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView_show_area.setSelection(0);
			textView_title.setText(selectedProvice.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvice.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询加载县级数据
	 */
	private void queryCounties() {
		countyList = mWeatherDao.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			areaList.clear();
			for (County county : countyList) {
				areaList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView_show_area.setSelection(0);
			textView_title.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * 根据所传入的所在地的代号，和所在地的类型来从服务器获取所在地的数据
	 * 
	 * @param code
	 *            所在地的代号
	 * @param type
	 *            所在地的类型（省、市、县）
	 */
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String urlAddress;
		if (!TextUtils.isEmpty(code)) {
			urlAddress = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			urlAddress = "http://www.weather.com.cn/data/list3/city.xml";

		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(urlAddress, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Log.d("LTP", response);
				boolean result = false;
				if ("province".equals(type)) {
					result = Tools.splitProvinceResponse(mWeatherDao, response);
				} else if ("city".equals(type)) {
					result = Tools.splitCitiesResponse(mWeatherDao, response,
							selectedProvice.getId());
				} else if ("county".equals(type)) {
					result = Tools.splitCountiesResponse(mWeatherDao, response,
							selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
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
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败,请检查网络",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示加载进度对话框
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载，请稍候");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭加载进度对话框
	 */
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO 重写返回键的方法
		switch (currentLevel) {
		case LEVEL_COUNTY:
			queryCities();
			break;
		case LEVEL_CITY:
			queryProvinces();
			break;
		case LEVEL_PROVINCE:
			finish();
			break;

		default:
			break;
		}
	}
}
