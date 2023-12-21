package com.sleepace.sdc100sdk.demo.fragment;

import java.util.HashMap;
import java.util.List;

import com.sleepace.sdc100sdk.demo.MainActivity;
import com.sleepace.sdc100sdk.demo.R;
import com.sleepace.sdc100sdk.demo.SearchBleDeviceActivity;
import com.sleepace.sdc100sdk.demo.util.ActivityUtil;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.sdc100.CallbackType;
import com.sleepace.sdk.sdc100.SDC100Helper;
import com.sleepace.sdk.sdc100.domain.ServerConfig;
import com.sleepace.sdk.sdc100.domain.WiFiConfig;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.wifidevice.WiFiDeviceSdkHelper;
import com.sleepace.sdk.wifidevice.bean.DeviceInfo;
import com.sleepace.sdk.wifidevice.bean.IdentificationBean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends BaseFragment {
	private View vDeviceName;
	private TextView tvDeviceName;
	private Button btnSetTcpAddress, btnSetWiFi, btnDeviceId, btnUpgrade, btnConnectServer, btnBindDevice, btnUnbindDevice;
	private EditText etIp, etPort, etSsid, etPwd, etServerAddress, etToken, etChannelId, etDeviceId, etVersion;
	private RadioGroup rgSide;
	private RadioButton rbLeft, rbRight;
	private WiFiDeviceSdkHelper wifiDeviceSdkHelper;
	private SDC100Helper deviceHelper;
	private ProgressDialog progressDialog;
	private ProgressDialog upgradeDialog;
	private SharedPreferences mSetting;	
	private String ip, sid;
	private int port;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_login, null);
		// LogUtil.log(TAG+" onCreateView-----------");
		wifiDeviceSdkHelper = WiFiDeviceSdkHelper.getInstance(mActivity.getApplicationContext());
		deviceHelper = SDC100Helper.getInstance(mActivity.getApplicationContext());
		mSetting = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
		findView(root);
		initListener();
		initUI();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		vDeviceName = root.findViewById(R.id.layout_devicename);
		tvDeviceName  = root.findViewById(R.id.tv_devicename);
		btnSetTcpAddress = (Button) root.findViewById(R.id.btn_set_net);
		btnSetWiFi = (Button) root.findViewById(R.id.btn_set_wifi);
		btnDeviceId = (Button) root.findViewById(R.id.btn_get_device_id);
		btnConnectServer = (Button) root.findViewById(R.id.btn_connect);
		btnUpgrade = (Button) root.findViewById(R.id.btn_upgrade_device);
		btnBindDevice = (Button) root.findViewById(R.id.btn_bind_device);
		btnUnbindDevice = (Button) root.findViewById(R.id.btn_unbind_device);
		etIp = (EditText) root.findViewById(R.id.et_ip);
		etPort = (EditText) root.findViewById(R.id.et_port);
		etSsid = (EditText) root.findViewById(R.id.et_ssid);
		etPwd = (EditText) root.findViewById(R.id.et_pwd);
		etServerAddress = (EditText) root.findViewById(R.id.et_server_address);
		etToken = (EditText) root.findViewById(R.id.et_token);
		etChannelId = (EditText) root.findViewById(R.id.et_channel_id);
		etDeviceId = root.findViewById(R.id.tv_device_id);
		etVersion = (EditText) root.findViewById(R.id.tv_firmware_version);
		rgSide = root.findViewById(R.id.rg_position);
		rbLeft = root.findViewById(R.id.rb_left);
		rbRight = root.findViewById(R.id.rb_right);
		root.findViewById(R.id.btn_device_version).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String channelId = etChannelId.getText().toString().trim();
				if(!TextUtils.isEmpty(channelId)) {
					HashMap<String, Object> args = new HashMap<String, Object>();
					args.put("channelId", channelId);
					args.put("lan", "zh-cn");
					wifiDeviceSdkHelper.getlastFirmwareVersion(args, new IResultCallback<Void>() {
						@Override
						public void onResultCallback(CallbackData<Void> cd) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		});
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		vDeviceName.setOnClickListener(this);
		btnSetTcpAddress.setOnClickListener(this);
		btnSetWiFi.setOnClickListener(this);
		btnDeviceId.setOnClickListener(this);
		btnUpgrade.setOnClickListener(this);
		btnConnectServer.setOnClickListener(this);
		btnBindDevice.setOnClickListener(this);
		btnUnbindDevice.setOnClickListener(this);
		rgSide.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.login_);
		progressDialog = new ProgressDialog(mActivity);
		progressDialog.setIcon(android.R.drawable.ic_dialog_info);
		//progressDialog.setMessage(getString(R.string.connecting_server));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		upgradeDialog = new ProgressDialog(mActivity);
		upgradeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条的形式为圆形转动的进度条
		upgradeDialog.setMessage(getString(R.string.fireware_updateing, ""));
		upgradeDialog.setCancelable(false);
		upgradeDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		deviceHelper.addBleStateCallback(stateCallback);
		String serverHost = mSetting.getString("serverHost", "http://120.24.68.136:8091");
		etServerAddress.setText(serverHost);
		String token = mSetting.getString("token", "");
		etToken.setText(token);
		String channelId = mSetting.getString("channelId", "10000");
		etChannelId.setText(channelId);
		//String deviceId = mSetting.getString("deviceId", MainActivity.deviceId);
		etDeviceId.setText("");
//		String version = mSetting.getString("version", "");
		etVersion.setText("");
		initSide();
		
		if(deviceHelper.isBleConntected()) {
			deviceHelper.serverConfigGet(callback);
		}
	}
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			if(state == CONNECTION_STATE.CONNECTED) {
				deviceHelper.serverConfigGet(callback);
			}
		}
	};
	
	private IResultCallback callback = new IResultCallback() {
		@Override
		public void onResultCallback(final CallbackData cd) {
			// TODO Auto-generated method stub
			if(ActivityUtil.isActivityAlive(mActivity) && isAdded()) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(cd.getCallbackType() == CallbackType.SERVER_CONFIG_GET) {
							if(cd.isSuccess()) {
								ServerConfig config = (ServerConfig) cd.getResult();
								etIp.setText(config.getIp());
								etPort.setText(String.valueOf(config.getPort()));
							}
							deviceHelper.wifiConfigGet(callback);
						}else if(cd.getCallbackType() == CallbackType.WIFI_CONFIG_GET) {
							if(cd.isSuccess()) {
								WiFiConfig config = (WiFiConfig) cd.getResult();
								etSsid.setText(new String(config.getSsidRaw()));
								etPwd.setText(config.getPassword());
							}
						}
					}
				});
			}
		}
	};
	
	private void initSide() {
		if(MainActivity.num == 0) {
			rbLeft.setChecked(true);
		}else {
			rbRight.setChecked(true);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		deviceHelper.removeBleStateCallback(stateCallback);
		Editor editor = mSetting.edit();
		String serverHost = etServerAddress.getText().toString().trim();
		if (!TextUtils.isEmpty(serverHost)) {
			editor.putString("serverHost", serverHost);
		}
		String token = etToken.getText().toString().trim();
		if (!TextUtils.isEmpty(token)) {
			editor.putString("token", token);
		}
		String channelId = etChannelId.getText().toString().trim();
		if (!TextUtils.isEmpty(channelId)) {
			editor.putString("channelId", channelId);
		}
		String deviceId = etDeviceId.getText().toString().trim();
		if (!TextUtils.isEmpty(deviceId)) {
			editor.putString("deviceId", deviceId);
		}
		String version = etVersion.getText().toString().trim();
		if (!TextUtils.isEmpty(version)) {
			editor.putString("version", version);
		}
		editor.commit();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == R.id.rb_left) {
				MainActivity.num = 0;
			}else {
				MainActivity.num = 1;
			}
		}
	};
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		String host = etServerAddress.getText().toString().trim();
		WiFiDeviceSdkHelper.initServerHost(host);
		if(v == vDeviceName) {
			Intent intent = new Intent(getActivity(), SearchBleDeviceActivity.class);
			startActivityForResult(intent, 100);
		}else if(v == btnSetTcpAddress) {
			String ip = etIp.getText().toString().trim();
			String sPort = etPort.getText().toString().trim();
			if(!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(sPort)) {
				short port = Short.valueOf(sPort);
				deviceHelper.serverConfigSet(ip, port, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						if(ActivityUtil.isActivityAlive(mActivity) && isAdded()) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(cd.isSuccess()) {
										Toast.makeText(mActivity, R.string.set_success, Toast.LENGTH_SHORT).show();
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					}
				});
			}
		}else if(v == btnSetWiFi) {
			String ssid = etSsid.getText().toString().trim();
			String pwd = etPwd.getText().toString().trim();
			if(!TextUtils.isEmpty(ssid)) {
				byte[] ssidRaw = ssid.getBytes();
				deviceHelper.wifiConfigSet(ssidRaw, pwd, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						if(ActivityUtil.isActivityAlive(mActivity) && isAdded()) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(cd.isSuccess()) {
										Toast.makeText(mActivity, R.string.set_success, Toast.LENGTH_SHORT).show();
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					}
				});
			}
		}else if(v == btnDeviceId) {
			deviceHelper.getDeviceInfo(new IResultCallback<com.sleepace.sdk.sdc100.domain.DeviceInfo>() {
				@Override
				public void onResultCallback(final CallbackData<com.sleepace.sdk.sdc100.domain.DeviceInfo> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity) && isAdded()) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									com.sleepace.sdk.sdc100.domain.DeviceInfo device = cd.getResult();
									etDeviceId.setText(device.getDeviceId());
								}else {
									Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			});
		}else if (v == btnConnectServer) {
			progressDialog.show();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("token", etToken.getText().toString().trim());
			args.put("channelId", etChannelId.getText().toString().trim());
			wifiDeviceSdkHelper.authorize(args, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG + " authorize cd:" + cd);
					if (cd.isSuccess()) {
						if (ActivityUtil.isActivityAlive(mActivity)) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (cd.isSuccess()) {
										Toast.makeText(mActivity, R.string.connect_server_success, Toast.LENGTH_SHORT).show();
										IdentificationBean bean = (IdentificationBean) cd.getResult();
										MainActivity.userId = bean.getUserId();
										ip = bean.getIp();
										port = bean.getPort();
										sid = bean.getSid();
										getBindedDevice();
									} else {
										progressDialog.dismiss();
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					} else {
						if (ActivityUtil.isActivityAlive(mActivity)) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									progressDialog.dismiss();
									Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				}
			});
		} else if (v == btnUpgrade) {
			String strVersion = etVersion.getText().toString().trim();
			if (TextUtils.isEmpty(MainActivity.deviceId) || TextUtils.isEmpty(strVersion)) {
				return;
			}
			
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			float deviceVersion = Float.valueOf(strVersion);
			deviceHelper.upgradeDevice(deviceVersion, MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<Integer>() {
				@Override
				public void onResultCallback(final CallbackData<Integer> cd) {
					// TODO Auto-generated method stub
					if (ActivityUtil.isActivityAlive(mActivity) && isAdded()) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (cd.isSuccess()) {
									upgradeDialog.show();
									Integer progress = cd.getResult();
									upgradeDialog.setProgress(progress);
									if (progress == 100) {
										upgradeDialog.dismiss();
										Toast.makeText(mActivity, R.string.update_completed, Toast.LENGTH_SHORT).show();
									}
								} else {
									//upgradeDialog.dismiss();
									if(cd.getStatus() == 8) {
										showTips(getString(R.string.err_device_offline));
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
					}
				}
			});
		} else if (v == btnBindDevice) {
			progressDialog.show();
			final String deviceId = etDeviceId.getText().toString().trim();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("deviceId", deviceId);
			args.put("leftRight", MainActivity.num);
			wifiDeviceSdkHelper.bindDevice(args, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG + " bindDevice cd:" + cd);
					if (ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								if (cd.isSuccess()) {
									Toast.makeText(mActivity, R.string.bind_device_success, Toast.LENGTH_SHORT).show();
									getBindedDevice();
								} else {
									Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			});
		} else if (v == btnUnbindDevice) {
			progressDialog.show();
			String deviceId = etDeviceId.getText().toString().trim();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("deviceId", deviceId);
			args.put("leftRight", MainActivity.num);
			wifiDeviceSdkHelper.unbindDevice(args, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG + " unbindDevice cd:" + cd);
					if (ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								if (cd.isSuccess()) {
									Toast.makeText(mActivity, R.string.unbind_device_success, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			});
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SdkLog.log(TAG+" onActivityResult----------req:" + requestCode+",result:" + resultCode);
		if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
			BleDevice device = (BleDevice) data.getSerializableExtra("device");
			tvDeviceName.setText(device.getDeviceName());
			progressDialog.show();
			deviceHelper.connectDevice(device.getAddress(), 10000, new IResultCallback<Void>() {
				@Override
				public void onResultCallback(final CallbackData<Void> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity) && isAdded()) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								if(cd.isSuccess()) {
//									Toast.makeText(mActivity, R.string.connect_device_success, Toast.LENGTH_SHORT).show();
								}else {
									Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			});
		}
	}

	private void getBindedDevice() {
		wifiDeviceSdkHelper.getBindedDevice(new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				SdkLog.log(TAG + " getBindedDevice cd:" + cd);
				if (ActivityUtil.isActivityAlive(mActivity)) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							progressDialog.dismiss();
							if(cd.isSuccess()) {
								List<DeviceInfo> list = (List<DeviceInfo>) cd.getResult();
								int size = list == null ? 0 : list.size();
								for(int i=0; i<size; i++) {
									DeviceInfo device = list.get(i);
									String deviceId = device.getDeviceId();
									String deviceName = device.getDeviceName();
									short deviceType = (short) device.getDeviceType();
									float deviceVersion = device.getDeviceVersion();
									String ext = device.getExt();
									DeviceType dType = DeviceType.getDeviceType(deviceType);
									if(DeviceType.isSDC100(dType)) {
										MainActivity.deviceId = deviceId;
										MainActivity.deviceType = deviceType;
										MainActivity.num = device.getLeftRight();
										
										etDeviceId.setText(deviceId);
										etVersion.setText(String.valueOf(deviceVersion));
										initSide();
										deviceHelper.login(ip, port, sid, deviceId, loginCallback);
										break;
									}else {
										
									}
								}
							}
						}
					});
				}
			}
		});
	}
	
	private IResultCallback loginCallback = new IResultCallback() {
		@Override
		public void onResultCallback(final CallbackData cd) {
			// TODO Auto-generated method stub
			if(cd.isSuccess()) {
				deviceHelper.queryDeviceOnlineState(MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<Byte>() {
					@Override
					public void onResultCallback(CallbackData<Byte> res) {
						// TODO Auto-generated method stub
						if(res.isSuccess()) {
							MainActivity.deviceOnline = res.getResult() == 1;
						}
					}
				});
			}
		}
	};
	
	

}
