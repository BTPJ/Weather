package com.text.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取网络数据的工具类
 * 
 * @author LTP
 *
 */
public class HttpUtil {

	/**
	 * 发送网络请求
	 * 
	 * @param urlAddress
	 * @param listner
	 */
	public static void sendHttpRequest(final String urlAddress,
			final HttpCallbackListener listner) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(urlAddress);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listner != null) {
						listner.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listner != null) {
						listner.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();

	}

	/**
	 * 回调接口
	 * 
	 * @author LTP
	 *
	 */
	public interface HttpCallbackListener {
		void onFinish(String response);

		void onError(Exception e);
	}
}
