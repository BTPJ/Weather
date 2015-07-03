package com.text.weather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.text.weather.bean.City;
import com.text.weather.bean.County;
import com.text.weather.bean.Province;
import com.text.weather.db.WeatherDao;

/**
 * 利用正则表达式来解析和处理数据
 * 
 * @author LTP
 *
 */
public class Tools {

	/**
	 * 解析服务器返回的省的数据并存储进数据库中相对应的省表中
	 * 
	 * @param mWeatherDao
	 * @param response
	 * @return
	 */
	public synchronized static boolean splitProvinceResponse(
			WeatherDao mWeatherDao, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存入Province表
					mWeatherDao.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析服务器返回的市的数据并存储进数据库中相对应的市表中
	 * 
	 * @param mWeatherDao
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public synchronized static boolean splitCitiesResponse(
			WeatherDao mWeatherDao, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			for (String c : allCities) {
				String[] array = c.split("\\|");
				City city = new City();
				city.setCityCode(array[0]);
				city.setCityName(array[1]);
				city.setProvinceId(provinceId);
				mWeatherDao.saveCity(city);
			}
			return true;
		}
		return false;
	}

	/**
	 * 解析服务器返回的县的数据并存储进数据库中相对应的县表中
	 * 
	 * @param mWeatherDao
	 * @param response
	 * @param cityId
	 * @return
	 */
	public synchronized static boolean splitCountiesResponse(
			WeatherDao mWeatherDao, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			for (String c : allCounties) {
				String[] array = c.split("\\|");
				County county = new County();
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(cityId);
				mWeatherDao.saveCounty(county);
			}
			return true;
		}
		return false;

	}

	public static void weatherParse(Context context, String response) {
		try {
			JSONObject mJsonObject = new JSONObject(response);
			JSONObject weatherInfo = mJsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String cityId = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDetails = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, cityId, temp1, temp2,
					weatherDetails, publishTime);
			;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的天气信息存储到SharedPreferences文件中
	 * 
	 * @param context
	 *            上下文
	 * @param cityName
	 *            城市名
	 * @param weatherCode
	 *            城市Id
	 * @param temp1
	 *            最高温度
	 * @param temp2
	 *            最低温度
	 * @param weatherDetails
	 *            具体天气信息
	 * @param publishTime
	 *            天气发布时间
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2,
			String weatherDetails, String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy年M月d日",
				Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_details", weatherDetails);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", mSimpleDateFormat.format(new Date()));
		editor.commit();
	}
}
