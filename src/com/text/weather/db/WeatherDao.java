package com.text.weather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.text.weather.bean.City;
import com.text.weather.bean.County;
import com.text.weather.bean.Province;

/**
 * 数据库帮助类
 * 
 * @author LTP
 *
 */
public class WeatherDao {

	/** 数据库名 */
	public static final String DB_NAME = "weather";
	/** 数据库版本 */
	public static final int VERSION = 1;
	/** WeatherDao的单例对象 */
	private static WeatherDao mWeatherDao;
	private SQLiteDatabase db;

	/**
	 * 构造函数私有化(实现单例)
	 * 
	 * @param context
	 */
	private WeatherDao(Context context) {
		WeatherOpenHelper mWeatherOpenHelper = new WeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = mWeatherOpenHelper.getWritableDatabase();
	}

	/**
	 * 获取WeatherDao的单例对象
	 * 
	 * @param context
	 * @return WeatherDao的单例对象
	 */
	public synchronized static WeatherDao getInstance(Context context) {
		if (mWeatherDao == null) {
			mWeatherDao = new WeatherDao(context);
		}
		return mWeatherDao;
	}

	/**
	 * 将Province实例存进数据库
	 * 
	 * @param province
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * 从数据库读取全国的省份信息
	 * 
	 * @return 全国省份的列表
	 */
	public List<Province> loadProvinces() {
		List<Province> provincelList = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				provincelList.add(province);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return provincelList;
	}

	/**
	 * 将City实例存进数据库
	 * 
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库读取省下的所有城市信息
	 * 
	 * @param provinceId
	 * @return
	 */
	public List<City> loadCities(int provinceId) {
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				cityList.add(city);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return cityList;
	}

	/**
	 * 把County实例存进数据库
	 * 
	 * @param county
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}

	/**
	 * 从数据库读取市下的所有县信息
	 * 
	 * @param cityId
	 * @return
	 */
	public List<County> loadCounties(int cityId) {
		List<County> countyList = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
				countyList.add(county);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return countyList;
	}
}
