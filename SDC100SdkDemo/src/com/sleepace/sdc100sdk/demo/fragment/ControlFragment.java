package com.sleepace.sdc100sdk.demo.fragment;

import com.sleepace.sdc100sdk.demo.R;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sdc100.CallbackType;
import com.sleepace.sdk.sdc100.SDC100Helper;
import com.sleepace.sdk.sdc100.domain.WorkStatus;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
public class ControlFragment extends BaseFragment {
	
	private Button btnDisconnect, btnBackUp, btnBackDown, btnWaistUp, btnWaistDown, btnMovieMode, btnReadMode, btnZeroMode, btnFlatMode,
		btnM1, btnSaveM1, btnM2, btnSaveM2;
	private CheckBox cbLock, cbStopSnore, cbNightLight;
	private SDC100Helper deviceHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_control, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		deviceHelper = SDC100Helper.getInstance(getActivity().getApplicationContext());
		findView(view);
		initListener();
		initUI();
		return view;
	}
	
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		btnDisconnect = (Button) root.findViewById(R.id.btn_disconnect);
		btnBackUp = (Button) root.findViewById(R.id.btn_back_up);
		btnBackDown = (Button) root.findViewById(R.id.btn_back_down);
		btnWaistUp = (Button) root.findViewById(R.id.btn_waist_up);
		btnWaistDown = (Button) root.findViewById(R.id.btn_waist_down);
		btnMovieMode = (Button) root.findViewById(R.id.btn_mode_movie);
		btnReadMode = (Button) root.findViewById(R.id.btn_mode_read);
		btnZeroMode = (Button) root.findViewById(R.id.btn_mode_zero);
		btnFlatMode = (Button) root.findViewById(R.id.btn_mode_flat);
		btnM1 = (Button) root.findViewById(R.id.btn_m1);
		btnSaveM1 = (Button) root.findViewById(R.id.btn_save_m1);
		btnM2 = (Button) root.findViewById(R.id.btn_m2);
		btnSaveM2 = (Button) root.findViewById(R.id.btn_save_m2);
		
		cbLock = (CheckBox)root.findViewById(R.id.cb_lock);
		cbStopSnore = (CheckBox)root.findViewById(R.id.cb_smart_stop_snore);
		cbNightLight = (CheckBox)root.findViewById(R.id.cb_night_light);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		deviceHelper.addBleStateCallback(stateCallback);
		btnDisconnect.setOnClickListener(this);
		btnBackUp.setOnTouchListener(touchListener);
		btnBackDown.setOnTouchListener(touchListener);
		btnWaistUp.setOnTouchListener(touchListener);
		btnWaistDown.setOnTouchListener(touchListener);
		btnMovieMode.setOnClickListener(this);
		btnReadMode.setOnClickListener(this);
		btnZeroMode.setOnClickListener(this);
		btnFlatMode.setOnClickListener(this);
		btnM1.setOnClickListener(this);
		btnSaveM1.setOnClickListener(this);
		btnM2.setOnClickListener(this);
		btnSaveM2.setOnClickListener(this);
		
		cbLock.setOnCheckedChangeListener(onCheckedChangeListener);
		cbStopSnore.setOnCheckedChangeListener(onCheckedChangeListener);
		cbNightLight.setOnCheckedChangeListener(onCheckedChangeListener);
	}


	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.control);
		deviceHelper.workStatusGet(callback);
	}
	
	private IResultCallback callback = new IResultCallback() {
		@Override
		public void onResultCallback(final CallbackData cd) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" callback cd:" +cd);
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!isAdded()) {
						return;
					}

					if (cd.getCallbackType() == CallbackType.WORK_STATUS_GET) {
						if(cd.isSuccess()) {
							WorkStatus workStatus = (WorkStatus) cd.getResult();
							cbLock.setChecked(workStatus.getLockState() == 1);
							cbNightLight.setChecked(workStatus.getBedBottomLightState() == 1);
						}
						deviceHelper.smartStopSnoringGet(callback);
					}else if(cd.getCallbackType() == CallbackType.STOP_SNORE_GET) {
						if(cd.isSuccess()) {
							byte onoff = (Byte) cd.getResult();
							cbStopSnore.setChecked(onoff == 1);
						}
					}
				}
			});
		}
	};
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setPageEnable(deviceHelper.isBleConntected());
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		SdkLog.log(TAG+" onDestroyView----------------");
		deviceHelper.removeBleStateCallback(stateCallback);
	}
	
	private long startTouchTime = 0;
	private TaskAction longTouchTask = null;
	private View.OnTouchListener touchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startTouchTime = System.currentTimeMillis();
				longTouchTask = new TaskAction(v);
				btnDisconnect.postDelayed(longTouchTask, 1000);
				break;
			
			
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				long touchTime = System.currentTimeMillis() - startTouchTime;
				if(touchTime < 1000) {//短按
					if(longTouchTask != null) {
						btnDisconnect.removeCallbacks(longTouchTask);
					}
				}
				
				if(v == btnBackUp){
					deviceHelper.bedControl((byte)2, (byte)0, (byte)0, new IResultCallback() {
						@Override
						public void onResultCallback(CallbackData cd) {
							// TODO Auto-generated method stub
							
						}
					});
				}else if(v == btnBackDown){
					deviceHelper.bedControl((byte)1, (byte)0, (byte)0, new IResultCallback() {
						@Override
						public void onResultCallback(CallbackData cd) {
							// TODO Auto-generated method stub
							
						}
					});
				}else if(v == btnWaistUp){
					deviceHelper.bedControl((byte)2, (byte)0, (byte)1, new IResultCallback() {
						@Override
						public void onResultCallback(CallbackData cd) {
							// TODO Auto-generated method stub
							
						}
					});
				}else if(v == btnWaistDown){
					deviceHelper.bedControl((byte)1, (byte)0, (byte)1, new IResultCallback() {
						@Override
						public void onResultCallback(CallbackData cd) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				
				break;
			}
			
			return false;
		}
	};
	
	
	private class TaskAction implements Runnable {
		private View v;
		
		TaskAction(View v){
			this.v = v;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(v == btnBackUp){
				deviceHelper.bedControl((byte)2, (byte)1, (byte)0, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						
					}
				});
			}else if(v == btnBackDown){
				deviceHelper.bedControl((byte)1, (byte)1, (byte)0, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						
					}
				});
			}else if(v == btnWaistUp){
				deviceHelper.bedControl((byte)2, (byte)1, (byte)1, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						
					}
				});
			}else if(v == btnWaistDown){
				deviceHelper.bedControl((byte)1, (byte)1, (byte)1, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
	};
	
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(final IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!isAdded()) {
						return;
					}

					if (state == CONNECTION_STATE.DISCONNECT) {
						setPageEnable(false);
					} else if (state == CONNECTION_STATE.CONNECTED) {
						setPageEnable(true);
					}
				}
			});
		}
	};
	
	private void setPageEnable(boolean enable) {
		btnDisconnect.setEnabled(enable);
		btnBackUp.setEnabled(enable);
		btnBackDown.setEnabled(enable);
		btnWaistUp.setEnabled(enable);
		btnWaistDown.setEnabled(enable);
		btnMovieMode.setEnabled(enable);
		btnReadMode.setEnabled(enable);
		btnZeroMode.setEnabled(enable);
		btnFlatMode.setEnabled(enable);
		btnM1.setEnabled(enable);
		btnSaveM1.setEnabled(enable);
		btnM2.setEnabled(enable);
		btnSaveM2.setEnabled(enable);
	}
	
	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(buttonView == cbLock) {
				deviceHelper.bedControl((byte)10, (byte) (isChecked ? 1 : 0), (byte)0, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						
					}
				});
			}else if(buttonView == cbStopSnore) {
				deviceHelper.smartStopSnoringSet((byte) (isChecked ? 1 : 0), new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						SdkLog.log(TAG+" smartStopSnoringSet cd:" +cd);
					}
				});
			}else if(buttonView == cbNightLight) {
				byte onoff = (byte) (isChecked ? 1 : 0);
				deviceHelper.bedBottomLightSet(onoff, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
	};
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == btnDisconnect){
			deviceHelper.disconnectBle();
		}else if(v == btnMovieMode){
			deviceHelper.bedControl((byte)3, (byte)0, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnReadMode){
			deviceHelper.bedControl((byte)4, (byte)0, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnZeroMode){
			deviceHelper.bedControl((byte)6, (byte)0, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnFlatMode){
			deviceHelper.bedControl((byte)7, (byte)0, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnM1){
			deviceHelper.bedControl((byte)8, (byte)0, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnSaveM1){
			deviceHelper.bedControl((byte)8, (byte)1, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnM2){
			deviceHelper.bedControl((byte)9, (byte)0, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnSaveM2){
			deviceHelper.bedControl((byte)9, (byte)1, (byte)0, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	
	
	

		
}



