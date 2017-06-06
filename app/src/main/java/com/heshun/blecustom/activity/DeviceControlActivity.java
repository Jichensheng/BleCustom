/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.heshun.blecustom.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.heshun.blecustom.R;
import com.heshun.blecustom.YModem.Ymodem;
import com.heshun.blecustom.adapter.EPAdapter;
import com.heshun.blecustom.entity.ElectricityParameter;
import com.heshun.blecustom.tools.FileUtils;
import com.heshun.blecustom.wheel.GetCurrentTime;
import com.heshun.blecustom.wheel.GetTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceControlActivity extends Activity implements View.OnClickListener {
	private final static String TAG = DeviceControlActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private TextView mDataField;//数据接收区域
	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mCharacteristic;//全局可读写的特征
	private String filePath = "";
	private TextView tv_filepath;
	private Ymodem ymodem;
	private List<byte[]> packageFrames;
	private boolean isFirstFrame = true;
	//recyclerview
	private RecyclerView recyclerView;
	private List<ElectricityParameter> eps;
	private EPAdapter adapter;
	private boolean fileSucc = false;//文件是否打开成功
	private String tempData = "";
	//控件
	private Button btn_get;
	private Button btn_add;
	private Button btn_sub;
	private Button btn_start;
	private Button btn_stop;
	private Button btn_send;
	private Button btn_make_time;
	private Button btn_set_time;
	private boolean dataNeedAdd = false;
	// 蓝牙服务的ServiceConnection
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			//通过binder获取到BluetoothLeService的实体
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_services_characteristics);
		initView();
	}

	private void initView() {
		ymodem = new Ymodem();
		//获取bundle里的数据
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//绑定并启动蓝牙服务
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

		//recyclerview部分
		eps = FileUtils.analysisWords2entity("JLZ=VOL:0V,CUR:0A,ELC:0Kwh,TIME:0MIN,STATE:0=JLZ");
		recyclerView = (RecyclerView) findViewById(R.id.rv_eplist);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter = new EPAdapter(this, eps);
		recyclerView.setAdapter(adapter);
		// Sets up UI references.
		mConnectionState = (TextView) findViewById(R.id.connection_state);
		mDataField = (TextView) findViewById(R.id.data_value);
		btn_get = (Button) findViewById(R.id.btn_get);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_make_time = (Button) findViewById(R.id.btn_make_time);
		btn_set_time = (Button) findViewById(R.id.btn_set_time);
		tv_filepath = (TextView) findViewById(R.id.tv_filepath);

		btn_get.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		btn_sub.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_make_time.setOnClickListener(this);
		btn_set_time.setOnClickListener(this);
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
				clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				//TODO 数据接收区域
				byte[] recive = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

				receiveData(recive);
			}
		}
	};

	/**
	 * 界面数据归零
	 */
	private void clearUI() {
		filePath="";
		mDataField.setText(R.string.no_data);
		btn_send.setText("UPDATA");

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	/**
	 * actionBar按钮
	 *
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_connect:
				mBluetoothLeService.connect(mDeviceAddress);
				return true;
			case R.id.menu_disconnect:
				mBluetoothLeService.disconnect();
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 状态连接回调
	 *
	 * @param resourceId
	 */
	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

	/**
	 * 文件选择器回调
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case FileUtils.FILE_SELECT_CODE:
				if (resultCode == RESULT_OK) {
					Uri uri = data.getData();
					filePath = FileUtils.getPath(this, uri);
					btn_send.setVisibility(View.VISIBLE);
					tv_filepath.setText(filePath);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 向外围设备发数据
	 *
	 * @param characteristic
	 * @param s
	 * @return
	 */
	private boolean writeData(BluetoothGattCharacteristic characteristic, String s) {
		byte[] writeBytes = s.getBytes();
		int charaProp = characteristic.getProperties();
		//如果该char可写
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
			characteristic.setValue(writeBytes);
			mBluetoothLeService.writeCharacteristic(characteristic);
			return true;
		} else
			return false;
	}

	/**
	 * 接收到的外围设备数据
	 *
	 * @param data
	 */
	private void receiveData(byte[] data) {


		if (data != null) {
			mDataField.setText(Arrays.toString(data) + "\n" + new String(data));
			Log.e("------receive-----", Arrays.toString(data));

			byte flag = data[0];
			byte flag_splicing = data.length > 1 ? data[1] : (byte) 0;
			String charlization = new String(data).trim();//用于判断"C"或者"JLZ="
			if (!dataNeedAdd && flag == Ymodem.NAK || flag == Ymodem.CAN || flag == Ymodem.ACK || flag == Ymodem.CHAR_C) {
				if (flag == Ymodem.ACK && flag_splicing == Ymodem.CHAR_C) {//处理[6,67]粘包问题
					flag = Ymodem.CHAR_C;
				}
				if (packageFrames != null) {//包不空
					if (charlization.length() == 1 && flag == Ymodem.CHAR_C && isFirstFrame) {//接到接收端的请求（第一帧）
						int state = ymodem.send(packageFrames, Ymodem.CHAR_C);
						deelState(state);
					} else {//接下来的帧
						int state = ymodem.send(packageFrames, flag);
						deelState(state);
					}
				} else {
					//文件为空
				}

			}
			if (charlization.startsWith("J") && !dataNeedAdd) {
				tempData += charlization;
				dataNeedAdd = true;
				if (charlization.contains("=JLZ")) {
					//给recyclerView使用的数据
					updataRecyclerView(FileUtils.analysisWords2entity(tempData));
					tempData = "";
					dataNeedAdd = false;
				}

//						Log.e("------JLZ-----", new String(temp, 0, temp.length).trim());
			}
			if (dataNeedAdd) {
				tempData += charlization;
				if (tempData.length() < 200 && charlization.contains("=JLZ")) {
					//给recyclerView使用的数据
					updataRecyclerView(FileUtils.analysisWords2entity(tempData));
					tempData = "";
					dataNeedAdd = false;
				}

//						Log.e("------JLZelse-----", new String(temp).trim());
			}

		}
	}

	/**
	 * 更新RecyclerView
	 *
	 * @param list
	 */
	private void updataRecyclerView(List<ElectricityParameter> list) {
		if (list != null) {
			this.eps.clear();
			for (ElectricityParameter ep : list) {
				this.eps.add(ep);
			}
			adapter.notifyDataSetChanged();
		}

	}

	/**
	 * 根据状态做处理
	 *
	 * @param state
	 */
	private void deelState(int state) {
		Message msg = handler.obtainMessage();
		float progress = 0f;
		switch (state) {
			case Ymodem.STATE_COMPLETE:
				isFirstFrame = true;
				msg.obj = "传输完成";
				progress = 100f;
				handler.sendMessage(msg);
				break;
			case Ymodem.STATE_FORCE_STOP:
				msg.obj = "传输被强停";
				progress = 0f;
				handler.sendMessage(msg);
				isFirstFrame = true;
				break;
			case Ymodem.STATE_MORE_RETRY:
				msg.obj = "重试次数太多被强停";
				progress = 0f;
				handler.sendMessage(msg);
				isFirstFrame = true;
				break;
			case Ymodem.STATE_NEXT:
				progress = ((float) (ymodem.getFrameNumber()-1) / (packageFrames.size())) * 100;
				//下一个包
				isFirstFrame = false;
				break;
		}
		btn_send.setText(String.format("%s %s", "UPDATA", (int) progress + "%"));
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(DeviceControlActivity.this, String.valueOf(msg.obj),
					Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}
	};

	/**
	 * 解析gattServices，把server罗列到可扩展列表上
	 *
	 * @param gattServices
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		String uuid;

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			uuid = gattService.getUuid().toString();
			//只过滤串口服务
			if (uuid.equals("0000ffe0-0000-1000-8000-00805f9b34fb")) {
				Log.e("----gattService层-----", "displayGattServices: " + uuid);

				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
				ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					charas.add(gattCharacteristic);
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
						final int charaProp = gattCharacteristic.getProperties();
						if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
							mCharacteristic = gattCharacteristic;
							//此特征值变化的时候会启动广播通知
							mBluetoothLeService.setCharacteristicNotification(
									gattCharacteristic, true);

							ymodem.setBLE(mCharacteristic, mBluetoothLeService);

							break;

						}
					}
					Log.e("---Characteristic层----", "displayGattServices: " + uuid);
				}
			} else {//end for
				continue;
			}
		}

	}

	/**
	 * 广播过滤器
	 *
	 * @return
	 */
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_get:
				FileUtils.showFileChooser(this);
				break;
			case R.id.btn_add:
				writeData(mCharacteristic, "+VOICE");
				break;
			case R.id.btn_sub:
				writeData(mCharacteristic, "-VOICE");
				break;
			case R.id.btn_start:
				writeData(mCharacteristic, "+START");
				break;
			case R.id.btn_stop:
				writeData(mCharacteristic, "+STOP");
				break;
			case R.id.btn_make_time:
				new GetTime(this) {
					@Override
					public void getTimeString(final String sb) {

						new Thread(new Runnable() {
							@Override
							public void run() {
								int i=0;
								String commendSet=sb;
								String[] array = commendSet.split(" ");

								while(i< array.length){
									if(i==1){
										mCharacteristic.setValue(" "+array[i++]);
									}else if(i==0){
										mCharacteristic.setValue("+ALARM"+array[i++]);
									}
									if(!mBluetoothLeService.writeCharacteristic(mCharacteristic)){
										i--;
									}
								}
							}
						}).start();
					}
				}.makeDailag();
				break;
			case R.id.btn_set_time:
				new Thread(new Runnable() {
					@Override
					public void run() {
						int i=0;
						String commendSet=new GetCurrentTime().getTime();
						String[] array = commendSet.split(" ");
						while(i< array.length){
							if(i==1){
								mCharacteristic.setValue(" "+array[i++]);
							}else if(i==0){
								mCharacteristic.setValue("+TIME"+array[i++]);
							}
							if(!mBluetoothLeService.writeCharacteristic(mCharacteristic)){
								i--;
							}
						}
					}
				}).start();
				break;
			case R.id.btn_send://发文件
				if (tv_filepath.getText().length() <= 0) {
					Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
				} else {
					new FileTask().execute(filePath);
				}
				break;

		}
	}

	/**
	 * 异步加载文件
	 */
	public class FileTask extends AsyncTask<String, Void, Integer> {
		static final int ERROR_FILE = 1;
		static final int SUCC = 2;


		@Override
		protected Integer doInBackground(String... strings) {

			try {
				File file = new File(strings[0]);
				InputStream inputStream = new FileInputStream(file);
				packageFrames = Ymodem.getPackage(inputStream, file.getName(), FileUtils.getFileOrFilesSize(strings[0], FileUtils.SIZETYPE_B));
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR_FILE;
			}
			return SUCC;
		}

		@Override
		protected void onPostExecute(Integer flag) {
			switch (flag) {
				case ERROR_FILE:
					//文件打开失败
					fileSucc = false;
					break;
				case SUCC:
					fileSucc = true;
					writeData(mCharacteristic, "+UPDATA");
					Ymodem.setInitiativeStart(true);//防止板子不经允许就自动发C升级
					break;
			}
		}
	}

}
