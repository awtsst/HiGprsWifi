package com.awtsst.higprswifi;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends Activity {

	// 检测网络连接状态管理對象
	private ConnectivityManager manager;

	private LinearLayout ll;
	private AdView adsView;
	private State gprs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	private boolean checkNetworkState() {
		boolean flag = false;
		// 得到网络连接信息
		manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		NetworkInfo info = manager.getActiveNetworkInfo();
		TelephonyManager mTelephony = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		if (info != null) {
			flag = info.isAvailable();
			System.out.println("-->網絡已經連接");
		}
		if (!flag) {
			// 网络没有连接则去设置网络
			System.out.println("-->網絡没有連接");
			System.out.println("-->正在设置网络...");
			setNetwork();
		} else {
			// 网络已经连接则去开始自己的业务（先判断是WIFI还是GPRS）
			System.out.println("-->正在判断网络环境...");
			// isNetworkAvailable();
			isWifiGprs(info, mTelephony, manager);
		}
		return flag;
	}

	/**
	 * 网络未连接时，调用设置方法
	 */
	private void setNetwork() {
		Toast.makeText(this, "网络不可用请先设置网络", Toast.LENGTH_SHORT).show();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("网络提示信息");
		builder.setMessage("网络不可用，请先设置网络！");
		builder.setPositiveButton("设置", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				/**
				 * 判断手机系统的版本！如果API大于10 就是3.0+ 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
				 */
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create();
		builder.show();
	}

	/**
	 * 网络已经连接然后去判断是wifi连接还是GPRS连接，设置自己的业务
	 * */
	private boolean isWifiGprs(NetworkInfo info, TelephonyManager mTelephony,
			ConnectivityManager manager) {
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			System.out.println("当前连接为WIFI连接,连接状态为：" + info.isConnected());
			Toast.makeText(this, "wifi is open! wifi", Toast.LENGTH_SHORT)
					.show();
			loadAdmob();
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE
				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !mTelephony.isNetworkRoaming()) {
			System.out.println("当前连接为GPRS连接,连接状态为：" + info.isConnected());
			Toast.makeText(this, "你当前环境为GPRS，请谨慎使用", Toast.LENGTH_SHORT).show();
			return info.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * make true current connect service is wifi
	 * 
	 * @param mContext
	 * @return
	 */
	private static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断WIFI和移动数据流量是否连接（该方法可单独使用）
	 * */
	public static boolean checkNetworkConnection(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable() || mobile.isAvailable()) // getState()方法是查询是否连接了数据网络
			return true;
		else
			return false;
	}

	/**
	 * 网络已经连接，然后去判断是wifi连接还是GPRS连接 设置一些自己的逻辑调用（第二种方法）
	 */
	private void isNetworkAvailable() {
		gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (gprs == null) {
			Toast.makeText(this, "wifi is open! gprs = null",
					Toast.LENGTH_SHORT).show();
		} else {
			State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
				Toast.makeText(this, "wifi is open! gprs", Toast.LENGTH_SHORT)
						.show();
			}
			// 判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
				Toast.makeText(this, "wifi is open! wifi", Toast.LENGTH_SHORT)
						.show();
				loadAdmob();
			}
		}
	}

	/**
	 * 在wifi状态下 加载admob广告 ；现在由于某些大家知道的原因，这个暂时不能用
	 */
	private void loadAdmob() {
		ll = (LinearLayout) findViewById(R.id.load_ads);
		ll.removeAllViews();
		adsView = new AdView(this, AdSize.BANNER, "a15194a1ac9505d");
		ll.addView(adsView);

		adsView.loadAd(new AdRequest());
	}

	@Override
	protected void onResume() {
		checkNetworkState();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// WIFI长连接问题（保证推送后的持续链接）
	private boolean IsOpenMethod(String methodName) {
		Class cmClass = manager.getClass();
		Class[] argClasses = null;
		Object[] argObject = null;
		Boolean isOpen = false;
		try {
			Method method = cmClass.getMethod(methodName, argClasses);
			isOpen = (Boolean) method.invoke(manager, argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOpen;
	}

}