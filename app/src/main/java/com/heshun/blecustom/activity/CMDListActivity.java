package com.heshun.blecustom.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.heshun.blecustom.R;
import com.heshun.blecustom.adapter.CMDListAdapter;
import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.base.BleMessage;
import com.heshun.blecustom.base.Head;
import com.heshun.blecustom.entity.CMDItem;
import com.heshun.blecustom.entity.ChargeMode;
import com.heshun.blecustom.entity.requestBodyEntity.ChargeNowRequest;
import com.heshun.blecustom.entity.requestBodyEntity.GunIdRequest;
import com.heshun.blecustom.entity.requestBodyEntity.SetChargeModeRequest;
import com.heshun.blecustom.entity.requestBodyEntity.SetVolumeRequest;
import com.heshun.blecustom.entity.requestBodyEntity.SysInfoRequest;
import com.heshun.blecustom.entity.requestBodyEntity.TimeSyncRequest;
import com.heshun.blecustom.tools.ToolsUtils;
import com.heshun.blecustom.wheel.DividerItemDecoration;
import com.heshun.blecustom.wheel.StrericWheelAdapter;
import com.heshun.blecustom.wheel.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author：Jics
 * 2017/6/5 16:20
 */
public class CMDListActivity extends Activity implements CMDListAdapter.CmdItemClickListener, View.OnClickListener {
	private String TAG = "cmd_request";
	public static final int TIME_OUT = 5;//超时时间
	public static final int MAX_INTERVAL = 1000;//两个包最大间隔时间
	private long lastTime = 0;
	private RecyclerView recyclerView;
	private AlertDialog.Builder loadingDialog;
	private AlertDialog dialog;
	private Timer timer;
	private int timeOut = TIME_OUT;
	private byte currentCMD = (byte) 0xFF;
	private Button btn_dis_connect;
	private Button btn_connect;
	byte[] responseTemp = new byte[0];

	// ----------- 蓝牙部分 -----------
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mCharacteristic;//全局可读写的特征
	// 蓝牙服务的ServiceConnection
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			//通过binder获取到BluetoothLeService的实体
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "初始化蓝牙连接失败");
				finish();
			}
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	// ------------ END -------------


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmd_list);
		initView();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		//获取bundle里的数据
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		//绑定并启动蓝牙服务
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

		btn_dis_connect = (Button) findViewById(R.id.btn_dis_connect);
		btn_connect = (Button) findViewById(R.id.btn_connect);

		recyclerView = (RecyclerView) findViewById(R.id.list_contener);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
		CMDListAdapter cmdListAdapter = new CMDListAdapter(this, makeCMDList());
		cmdListAdapter.setCmdItemClickListener(this);
		recyclerView.setAdapter(cmdListAdapter);
		loadingDialog = new AlertDialog.Builder(this);
	}

	/**
	 * 生成列表
	 *
	 * @return
	 */
	private List<CMDItem> makeCMDList() {
		List<CMDItem> cmdList = new ArrayList<>();
		cmdList.add(new CMDItem(false, "时间同步", Head.CMD_TIME_SYNCHRONIZATION));
		cmdList.add(new CMDItem(false, "查询系统信息", Head.CMD_QUERY_SYSTEM_INFORMATION));
		cmdList.add(new CMDItem(true, "设置音量", Head.CMD_SET_VOLUME));
		cmdList.add(new CMDItem(true, "设置充电模式", Head.CMD_SET_CHARGE_MODE));
		cmdList.add(new CMDItem(true, "立即充电", Head.CMD_CHARGE_NOW));
		cmdList.add(new CMDItem(true, "查询状态", Head.CMD_QUERY_STATE));
		cmdList.add(new CMDItem(true, "结束充电", Head.CMD_END_CHARGE));
		cmdList.add(new CMDItem(true, "查询充电历史记录", Head.CMD_QUERY_CHARGING_HISTORY));
		cmdList.add(new CMDItem(true, "查询累计电量", Head.CMD_QUERY_CUMULATIVE_CHARGE));
		cmdList.add(new CMDItem(true, "启动远程升级", Head.CMD_START_REMOTE_UPGRADE));
		return cmdList;
	}

	@Override
	public void onCmdClick(boolean isJump, byte cmd) {
		if (mConnected) {
			//发送（head+body）命令
			makeCMD(cmd);
		} else {
			Toast.makeText(this, "蓝牙连接已断开", Toast.LENGTH_SHORT).show();
		}


	/*	if (isJump) {
			Intent intent = new Intent(this, CMDJumpHereActivity.class);
			intent.putExtra("CMDCode", cmd);
			startActivity(intent);
		} else {

		}*/
	}

	/**
	 * 信息出口
	 *
	 * @param msg
	 */
	private void sendMsg(byte cmd, final byte[] msg) {
		currentCMD = cmd;//赋值当前命令
		Log.e(TAG, "请求的数据: " + Arrays.toString(msg));
		loadingDialog.setTitle("等待响应数据");
		loadingDialog.setCancelable(false);
		loadingDialog.setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null));

		dialog = loadingDialog.create();

		dialog.show();
		timer = new Timer();

		// 拆分成 20B 的包发送
		new Thread(new Runnable() {
			@Override
			public void run() {
				int packSize = 20;
				int length = msg.length;
				int packCount = length % packSize == 0 ? length / packSize : length / packSize + 1;
				for (int i = 1; i <= packCount; i++) {
					if (i == packCount) {
						byte[] bytes = new byte[length % packSize == 0 ? packSize : length % packSize];
						System.arraycopy(msg, (i - 1) * packSize, bytes, 0, bytes.length);
						mCharacteristic.setValue(bytes);
					} else {
						byte[] bytes = new byte[packSize];
						System.arraycopy(msg, (i - 1) * packSize, bytes, 0, packSize);
						mCharacteristic.setValue(bytes);
					}
					if (!mBluetoothLeService.writeCharacteristic(mCharacteristic)) {
						i--;
					}
				}
				//发送成功后开始计时
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (timeOut-- <= 0) {
							timer.cancel();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									dialog.dismiss();
									Toast.makeText(CMDListActivity.this, "响应超时", Toast.LENGTH_SHORT).show();
								}
							});
							currentCMD = (byte) 0xFF;//还原当前命令
							timeOut = TIME_OUT;
						}
					}
				}, 1000, 1000);
			}
		}).start();

	}

	/**
	 * 回调数据处理部分
	 * 数据包大小限制20byte,分包接收两条数据间隔1秒视为数据接收完毕
	 * 超时或者接收数据完整后将请求命令设为失效 (byte)0xFF
	 * @param recive
	 */
	private void receiveData(byte[] recive) {
		if (currentCMD != (byte) 0xFF) {//请求命令还在生效（1 超过五秒失效 2 数据接受完整失效）
			if (lastTime != 0) {
				if (System.currentTimeMillis() - lastTime <= MAX_INTERVAL) {//未接收完毕
					responseTemp = ToolsUtils.concatAll(responseTemp, recive);
					lastTime = System.currentTimeMillis();
				}else {//接收完毕
					//在此处理数据
					BaseResponseBody responseBody=new BleMessage().decodeMessage(recive);
					if (responseBody != null) {
						String result = responseBody.toString();
						Intent intent = new Intent(this, CMDJumpHereActivity.class);
						intent.putExtra("CMDCode", currentCMD);
						intent.putExtra("result",result);
						startActivity(intent);
						currentCMD=(byte)0xFF;
						lastTime=0;
						if (dialog.isShowing()) {
							dialog.dismiss();//接收完成取消弹窗
						}
					}else{
						Toast.makeText(CMDListActivity.this, "响应体不完整", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				responseTemp = ToolsUtils.concatAll(responseTemp, recive);
				lastTime = System.currentTimeMillis();
			}
		}else{//该条命令已失效
			Toast.makeText(CMDListActivity.this, "请求命令已失效，请重新发送！", Toast.LENGTH_SHORT).show();
			responseTemp=new byte[0];
			currentCMD=(byte)0xFF;
			lastTime=0;
		}

	}

	//------------ 以下是蓝牙通讯部分 ----------
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				//连接成功逻辑部分
				btn_connect.setVisibility(View.VISIBLE);
				btn_dis_connect.setVisibility(View.GONE);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				btn_connect.setVisibility(View.GONE);
				btn_dis_connect.setVisibility(View.VISIBLE);
				//断开连接逻辑部分
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// 获取所有服务通道部分
				displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				byte[] recive = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				//响应数据处理部分
				receiveData(recive);
			}
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
			if (uuid.equals("0000fff0-0000-1000-8000-00805f9b34fb")) {
				Log.e("----gattService层-----", "displayGattServices: " + uuid);

				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
				ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					charas.add(gattCharacteristic);
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals("0000fff3-0000-1000-8000-00805f9b34fb")) {
						final int charaProp = gattCharacteristic.getProperties();
						if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
							mCharacteristic = gattCharacteristic;
							//此特征值变化的时候会启动广播通知
							mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
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


	//------------- 以下是数据生成部分 ---------

	/**
	 * 手机参数编码请求内容
	 *
	 * @param cmd
	 */
	private void makeCMD(byte cmd) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();

		switch (cmd) {
			case Head.CMD_TIME_SYNCHRONIZATION:
				//无需参数 直接loading界面
				Head syncHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_TIME_SYNCHRONIZATION, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				BleMessage syncMsg = new BleMessage();
				sendMsg(cmd, syncMsg.encodeMessage(syncHead, new TimeSyncRequest()));
				break;
			case Head.CMD_QUERY_SYSTEM_INFORMATION:
				//无需参数 直接loading界面
				Head sysInfoHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_QUERY_SYSTEM_INFORMATION, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				BleMessage sysInfoMsg = new BleMessage();
				sendMsg(cmd, sysInfoMsg.encodeMessage(sysInfoHead, new SysInfoRequest()));
				break;
			case Head.CMD_SET_VOLUME://音量设置--
				Head volHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_SET_VOLUME, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				SetVolumeRequest setVolumeRequest = new SetVolumeRequest();
				showSetVolDialog(cmd, builder, inflater, volHead, setVolumeRequest);
				break;
			case Head.CMD_SET_CHARGE_MODE://充电模式
				Head modeHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_SET_CHARGE_MODE, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				SetChargeModeRequest setChargeModeRequest = new SetChargeModeRequest();
				showSetChargeModeDialog(cmd, builder, inflater, modeHead, setChargeModeRequest);
				break;
			case Head.CMD_CHARGE_NOW://立即充电
				Head nowHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_CHARGE_NOW, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				ChargeNowRequest chargeNowRequest = new ChargeNowRequest();
				showChargeNowDialog(cmd, builder, inflater, nowHead, chargeNowRequest);
				break;
			case Head.CMD_QUERY_STATE://查询状态 --
				Head stateHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_QUERY_STATE, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				GunIdRequest stateRequest = new GunIdRequest();//枪号
				showSetGunIdDialog(cmd, "查询状态", builder, inflater, stateHead, stateRequest);
				break;
			case Head.CMD_END_CHARGE: //结束充电--
				Head endHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_END_CHARGE, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				GunIdRequest endRequest = new GunIdRequest();//枪号
				showSetGunIdDialog(cmd, "结束充电", builder, inflater, endHead, endRequest);
				break;
			case Head.CMD_QUERY_CHARGING_HISTORY: //历史记录--
				//查询充电历史记录
				Head historyHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_QUERY_CHARGING_HISTORY, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				//body部分需要参数
				GunIdRequest chargeHistoryRequest = new GunIdRequest();//枪号

				showSetGunIdDialog(cmd, "历史记录", builder, inflater, historyHead, chargeHistoryRequest);
				break;
			case Head.CMD_QUERY_CUMULATIVE_CHARGE://累计电量--
				Head cumulativeHead = new Head(Head.CMD_TYPE_REQUEST, Head.CMD_QUERY_CUMULATIVE_CHARGE, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
				GunIdRequest cumulativeRequest = new GunIdRequest();//枪号
				showSetGunIdDialog(cmd, "累计电量", builder, inflater, cumulativeHead, cumulativeRequest);
				break;
			case Head.CMD_START_REMOTE_UPGRADE:
				break;
		}
	}

	/**
	 * 设置枪编号
	 *
	 * @param cmd
	 * @param name
	 * @param builder
	 * @param inflater
	 * @param head
	 * @param gunIdRequest
	 */
	private void showSetGunIdDialog(final byte cmd, String name, final AlertDialog.Builder builder, LayoutInflater inflater, final Head head, final GunIdRequest gunIdRequest) {
		final View view = inflater.inflate(R.layout.dialog_gun_num, null);
		final EditText editText = (EditText) view.findViewById(R.id.et_gun_id);
		builder.setTitle(name);
		builder.setView(view);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			int gunId = 0;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (editText.getText() != null) {
					gunId = Integer.parseInt(editText.getText().toString());

					BleMessage bleMessage = new BleMessage();
					gunIdRequest.setId(gunId);
					sendMsg(cmd, bleMessage.encodeMessage(head, gunIdRequest));

					builder.create().dismiss();
				} else {
					Toast.makeText(CMDListActivity.this, "输入枪编号", Toast.LENGTH_SHORT).show();
				}
				builder.create().dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
			}
		});
		builder.create().show();
	}


	/**
	 * 音量设置 参数设置弹窗
	 *
	 * @param cmd
	 * @param builder
	 * @param inflater
	 * @param volHead
	 * @param setVolumeRequest
	 */
	private void showSetVolDialog(final byte cmd, final AlertDialog.Builder builder, LayoutInflater inflater,
								  final Head volHead, final SetVolumeRequest setVolumeRequest) {
		View setVolume = inflater.inflate(R.layout.dialog_set_volume, null);
		builder.setView(setVolume);
		builder.setTitle("音量设置");
		//seekbar部分
		SeekBar seekBar = (SeekBar) setVolume.findViewById(R.id.id_seekbar);
		final TextView tv_volume = (TextView) setVolume.findViewById(R.id.tv_volume);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tv_volume.setText(progress + "");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		// 确认按钮
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int vol = Integer.parseInt(tv_volume.getText().toString());
				BleMessage bleMessage = new BleMessage();
				setVolumeRequest.setVolume(vol);
				sendMsg(cmd, bleMessage.encodeMessage(volHead, setVolumeRequest));

				builder.create().dismiss();
			}
		});
		//取消按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 充电模式 参数设置弹窗
	 *
	 * @param cmd
	 * @param builder
	 * @param inflater
	 * @param modeHead
	 * @param setChargeModeRequest
	 */
	private void showSetChargeModeDialog(final byte cmd, final AlertDialog.Builder builder, LayoutInflater inflater,
										 final Head modeHead, final SetChargeModeRequest setChargeModeRequest) {
		final View view = inflater.inflate(R.layout.dialog_set_charge_mode, null);

		builder.setView(view);
		builder.setTitle("设置充电模式");

		final LinearLayout wheel_start_time = (LinearLayout) view.findViewById(R.id.wheel_start_time);
		final LinearLayout wheel_end_time = (LinearLayout) view.findViewById(R.id.wheel_end_time);
		final WheelView startHourWheel, startMinuteWheel, endHourWheel, endMinuteWheel;

		startHourWheel = (WheelView) view.findViewById(R.id.start_hourwheel);
		startMinuteWheel = (WheelView) view.findViewById(R.id.start_minutewheel);
		endHourWheel = (WheelView) view.findViewById(R.id.end_hourwheel);
		endMinuteWheel = (WheelView) view.findViewById(R.id.end_minutewheel);

		Calendar calendar = Calendar.getInstance();
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		int curMinute = calendar.get(Calendar.MINUTE);

		String[] hourContent, minuteContent;
		hourContent = new String[24];
		for (int i = 0; i < 24; i++) {
			hourContent[i] = String.valueOf(i);
			if (hourContent[i].length() < 2) {
				hourContent[i] = "0" + hourContent[i];
			}
		}

		minuteContent = new String[60];
		for (int i = 0; i < 60; i++) {
			minuteContent[i] = String.valueOf(i);
			if (minuteContent[i].length() < 2) {
				minuteContent[i] = "0" + minuteContent[i];
			}
		}

		startHourWheel.setAdapter(new StrericWheelAdapter(hourContent));
		startHourWheel.setCurrentItem(curHour);
		startHourWheel.setCyclic(true);
		startHourWheel.setInterpolator(new AnticipateOvershootInterpolator());

		startMinuteWheel.setAdapter(new StrericWheelAdapter(hourContent));
		startMinuteWheel.setCurrentItem(curMinute);
		startMinuteWheel.setCyclic(true);
		startMinuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

		endHourWheel.setAdapter(new StrericWheelAdapter(hourContent));
		endHourWheel.setCurrentItem(curHour);
		endHourWheel.setCyclic(true);
		endHourWheel.setInterpolator(new AnticipateOvershootInterpolator());

		endMinuteWheel.setAdapter(new StrericWheelAdapter(hourContent));
		endMinuteWheel.setCurrentItem(curMinute);
		endMinuteWheel.setCyclic(true);
		endMinuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

		final ChargeMode chargeMode = new ChargeMode();
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);


		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radioButton = (RadioButton) view.findViewById(group.getCheckedRadioButtonId());
				switch (radioButton.getText().toString()) {
					//第一字节保存小时，第二字节保存分钟
					//0表示不限时间
					case "经济模式":
						wheel_start_time.setVisibility(View.VISIBLE);
						wheel_end_time.setVisibility(View.VISIBLE);
						chargeMode.setMode(SetChargeModeRequest.MODE_BARGAIN);

						break;
					case "定时模式":
						wheel_start_time.setVisibility(View.VISIBLE);
						wheel_end_time.setVisibility(View.GONE);
						chargeMode.setMode(SetChargeModeRequest.MODE_TIMING);

						break;
					default://立即模式
						wheel_start_time.setVisibility(View.GONE);
						wheel_end_time.setVisibility(View.GONE);
						chargeMode.setMode(SetChargeModeRequest.MODE_IMMEDIATELY);
				}
			}
		});

		//确认按钮
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			int startHour, endHour, startMinute, endMinute;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (chargeMode.getMode()) {

					case SetChargeModeRequest.MODE_BARGAIN:
						startHour = Integer.parseInt(startHourWheel.getCurrentItemValue());
						startMinute = Integer.parseInt(startMinuteWheel.getCurrentItemValue());
						endHour = Integer.parseInt(endHourWheel.getCurrentItemValue());
						endMinute = Integer.parseInt(endMinuteWheel.getCurrentItemValue());
						break;
					case SetChargeModeRequest.MODE_TIMING:
						startHour = Integer.parseInt(startHourWheel.getCurrentItemValue());
						startMinute = Integer.parseInt(startMinuteWheel.getCurrentItemValue());
						endHour = 0;
						endMinute = 0;
						break;
					case SetChargeModeRequest.MODE_IMMEDIATELY:
						startHour = 0;
						startMinute = 0;
						endHour = 0;
						endMinute = 0;
						break;
				}


				//Todo 发送业务
				BleMessage bleMessage = new BleMessage();
				setChargeModeRequest.setChargeMode(chargeMode.getMode());
				setChargeModeRequest.setStartH(startHour);
				setChargeModeRequest.setStartM(startMinute);
				setChargeModeRequest.setEndH(endHour);
				setChargeModeRequest.setEndM(endMinute);
				sendMsg(cmd, bleMessage.encodeMessage(modeHead, setChargeModeRequest));
				builder.create().dismiss();
			}
		});

		//取消按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				builder.create().dismiss();
			}
		});
		builder.create().show();

	}

	/**
	 * 立即充电 参数设置弹窗
	 *
	 * @param cmd
	 * @param builder
	 * @param inflater
	 * @param nowHead
	 * @param chargeNowRequest
	 */
	private void showChargeNowDialog(final byte cmd, final AlertDialog.Builder builder, LayoutInflater inflater,
									 final Head nowHead, final ChargeNowRequest chargeNowRequest) {
		final View view = inflater.inflate(R.layout.dialog_charge_now, null);
		builder.setView(view);
		builder.setTitle("立即充电");

		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_charge_now);
		final EditText editText = (EditText) view.findViewById(R.id.et_gun_id);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			int authorizeType;
			int id;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				RadioButton radioButton = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());

				switch (radioButton.getText().toString()) {
					case "长期授权":
						authorizeType = 0x01;
						break;
					default://本次授权
						authorizeType = 0x00;
				}
				if (editText.getText() != null) {
					id = Integer.parseInt(editText.getText().toString());
					BleMessage bleMessage = new BleMessage();
					chargeNowRequest.setAuthorizeType(authorizeType);
					chargeNowRequest.setId(id);
					sendMsg(cmd, bleMessage.encodeMessage(nowHead, chargeNowRequest));
					builder.create().dismiss();
				} else {
					Toast.makeText(CMDListActivity.this, "输入枪编号", Toast.LENGTH_SHORT).show();
				}
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				builder.create().dismiss();
			}
		});
		builder.create().show();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_dis_connect:
				btn_dis_connect.setVisibility(View.GONE);
				btn_connect.setVisibility(View.VISIBLE);
				mBluetoothLeService.connect(mDeviceAddress);
				break;
			case R.id.btn_connect:
				btn_dis_connect.setVisibility(View.VISIBLE);
				btn_connect.setVisibility(View.GONE);
				mBluetoothLeService.disconnect();
				break;
		}
	}
}
