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

	// �����������״̬���팦��
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
	 * ��������Ƿ�����
	 * 
	 * @return
	 */
	private boolean checkNetworkState() {
		boolean flag = false;
		// �õ�����������Ϣ
		manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// ȥ�����ж������Ƿ�����
		NetworkInfo info = manager.getActiveNetworkInfo();
		TelephonyManager mTelephony = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		if (info != null) {
			flag = info.isAvailable();
			System.out.println("-->�W�j�ѽ��B��");
		}
		if (!flag) {
			// ����û��������ȥ��������
			System.out.println("-->�W�jû���B��");
			System.out.println("-->������������...");
			setNetwork();
		} else {
			// �����Ѿ�������ȥ��ʼ�Լ���ҵ�����ж���WIFI����GPRS��
			System.out.println("-->�����ж����绷��...");
			// isNetworkAvailable();
			isWifiGprs(info, mTelephony, manager);
		}
		return flag;
	}

	/**
	 * ����δ����ʱ���������÷���
	 */
	private void setNetwork() {
		Toast.makeText(this, "���粻����������������", Toast.LENGTH_SHORT).show();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("������ʾ��Ϣ");
		builder.setMessage("���粻���ã������������磡");
		builder.setPositiveButton("����", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				/**
				 * �ж��ֻ�ϵͳ�İ汾�����API����10 ����3.0+ ��Ϊ3.0���ϵİ汾�����ú�3.0���µ����ò�һ�������õķ�����ͬ
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
		builder.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create();
		builder.show();
	}

	/**
	 * �����Ѿ�����Ȼ��ȥ�ж���wifi���ӻ���GPRS���ӣ������Լ���ҵ��
	 * */
	private boolean isWifiGprs(NetworkInfo info, TelephonyManager mTelephony,
			ConnectivityManager manager) {
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			System.out.println("��ǰ����ΪWIFI����,����״̬Ϊ��" + info.isConnected());
			Toast.makeText(this, "wifi is open! wifi", Toast.LENGTH_SHORT)
					.show();
			loadAdmob();
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE
				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !mTelephony.isNetworkRoaming()) {
			System.out.println("��ǰ����ΪGPRS����,����״̬Ϊ��" + info.isConnected());
			Toast.makeText(this, "�㵱ǰ����ΪGPRS�������ʹ��", Toast.LENGTH_SHORT).show();
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
	 * �ж�WIFI���ƶ����������Ƿ����ӣ��÷����ɵ���ʹ�ã�
	 * */
	public static boolean checkNetworkConnection(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable() || mobile.isAvailable()) // getState()�����ǲ�ѯ�Ƿ���������������
			return true;
		else
			return false;
	}

	/**
	 * �����Ѿ����ӣ�Ȼ��ȥ�ж���wifi���ӻ���GPRS���� ����һЩ�Լ����߼����ã��ڶ��ַ�����
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
			// �ж�Ϊwifi״̬�²ż��ع�棬�����GPRS�ֻ������򲻼��أ�
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
				Toast.makeText(this, "wifi is open! wifi", Toast.LENGTH_SHORT)
						.show();
				loadAdmob();
			}
		}
	}

	/**
	 * ��wifi״̬�� ����admob��� ����������ĳЩ���֪����ԭ�������ʱ������
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

	// WIFI���������⣨��֤���ͺ�ĳ������ӣ�
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