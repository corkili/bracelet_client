package org.client.bracelet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import org.client.bracelet.R;
import org.client.bracelet.bluetooth.BluetoothSerialClient;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.HeartState;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.entity.SleepState;
import org.client.bracelet.entity.SportState;
import org.client.bracelet.entity.State;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class SportFragment extends Fragment {

    private ApplicationManager manager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private AwesomeTextView stepsTV, kilometreTV, sleepTV;
    private BootstrapButton refreshBtn, bandBraceletBtn, heartRateBtn;
    private BtnOnClick btnOnClick;
    private JSONObject getSportResult, getSleepResult, postSportResult, postSleepResult,
            postHeartResult, notifyFriendResult;

    private SweetAlertDialog mLoadingDialog, pDialog;
    private AlertDialog mDeviceListDialog;
    private LinkedList<BluetoothDevice> mBluetoothDevices;
    private ArrayAdapter<String> mDeviceArrayAdapter;
    private BluetoothSerialClient mClient;

    private String sendMessage;
    private boolean isProcessData;
    private Date getStateStartTime, getStateEndTime;

    private String sportStateJsonString, sleepStateJsonString, heartStateJsonString;
    private int heartTimes;

    private static SportFragment singleton;

    public static SportFragment getInstance() {
        if (singleton == null) {
            singleton = new SportFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnOnClick = new BtnOnClick();
        mBluetoothDevices = new LinkedList<>();
        manager = ApplicationManager.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        mClient = BluetoothSerialClient.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        getStateStartTime = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        getStateEndTime = new Date(calendar.getTimeInMillis() - 1);

        if(mClient == null) {
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity().getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误提示")
                    .setContentText("请开启蓝牙后，重启应用");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    System.exit(0);
                }
            });
            dialog.show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (!ApplicationManager.getInstance().isLogin() && mClient != null) {
            v = inflater.inflate(R.layout.fragment_sport, null);
            stepsTV = ViewFindUtils.find(v, R.id.steps);
            kilometreTV = ViewFindUtils.find(v, R.id.kilometre);
            sleepTV = ViewFindUtils.find(v, R.id.sleep);
            refreshBtn = ViewFindUtils.find(v, R.id.btn_refresh);
            bandBraceletBtn = ViewFindUtils.find(v, R.id.btn_band_bracelet);
            heartRateBtn = ViewFindUtils.find(v, R.id.btn_heart_rate);

            refreshBtn.setOnClickListener(btnOnClick);
            bandBraceletBtn.setOnClickListener(btnOnClick);
            heartRateBtn.setOnClickListener(btnOnClick);

            if (ApplicationManager.getInstance().hasBandBracelet()) {
                bandBraceletBtn.setText("解除绑定");
            } else {
                bandBraceletBtn.setText("绑定设备");
            }
            initDeviceListDialog();
            heartRateBtn.setEnabled(mClient.isConnection());
            updateET();
            initLoading();
            enableBluetooth();
        } else {
            v = inflater.inflate(R.layout.fragment_nologin, null);
            BootstrapButton toLoginBtn = ViewFindUtils.find(v, R.id.btn_to_login);
            toLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
        return v;
    }

    private void updateET() {
        stepsTV.setText(manager.getSteps() + "步");
        kilometreTV.setText(manager.getKilometres() + "公里");
        sleepTV.setText(manager.getSleepTime() + "小时");
    }

    private class BtnOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == refreshBtn.getId()) {
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("正在同步数据");
                pDialog.setCancelable(false);
                pDialog.show();
                final Thread refreshThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        isProcessData = false;
                        sendMessage = "4";      // 校准时间
                        do {
                            sendStringData(sendMessage);
                        } while (!isProcessData);

                        isProcessData = false;
                        sendMessage = "2";
                        do {
                            sendStringData(sendMessage);
                        } while (!isProcessData);
                        Message msg1 = new Message();
                        msg1.what = 2;
                        bluetoothHandler.sendMessage(msg1);

                        isProcessData = false;
                        sendMessage = "3";
                        do {
                            sendStringData(sendMessage);
                        } while (!isProcessData);
                        Message msg2 = new Message();
                        msg2.what = 3;
                        bluetoothHandler.sendMessage(msg2);
                    }
                };
                refreshThread.start();
            } else if (v.getId() == bandBraceletBtn.getId()) {
                if (!mClient.isConnection()) {
                    String address = sharedPreferences.getString("address", null);
                    if (address != null) {
                        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                        connect(device);
                    } else {
                        mDeviceListDialog.show();
                    }
                } else {
                    mBTHandler.close();
                    // 解除绑定
                }
            } else if (v.getId() == heartRateBtn.getId()) {
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("正在测试心率");
                pDialog.setCancelable(false);
                pDialog.show();
                isProcessData = false;
                final Thread heartThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        sendMessage = "1";
                        do {
                            sendStringData(sendMessage);
                            try {
                                sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (!isProcessData);
                        Message msg = new Message();
                        msg.what = 1;
                        System.out.println("heartThread send message...");
                        bluetoothHandler.sendMessage(msg);
                    }
                };
                heartThread.start();
            }
        }
    }

    private void initDeviceListDialog() {
        mDeviceArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.item_device);
        ListView listView = new ListView(getActivity().getApplicationContext());
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择设备");
        builder.setView(listView);
        builder.setPositiveButton("扫描", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                scanDevices();
            }
        });
        mDeviceListDialog = builder.create();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
    }

    private void addDeviceToArrayAdapter(BluetoothDevice device) {
        if(mBluetoothDevices.contains(device)) {
            mBluetoothDevices.remove(device);
            mDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
        }
        mBluetoothDevices.add(device);
        mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress() );
        mDeviceArrayAdapter.notifyDataSetChanged();
    }

    private void enableBluetooth() {
        BluetoothSerialClient btSet =  mClient;
        btSet.enableBluetooth(getActivity(), new BluetoothSerialClient.OnBluetoothEnabledListener() {
            @Override
            public void onBluetoothEnabled(boolean success) {
                if(success) {
                    getPairedDevices();
                }
            }
        });
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  mClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
    }

    private void scanDevices() {
        BluetoothSerialClient btSet = mClient;
        btSet.scanDevices(getActivity().getApplicationContext(), new BluetoothSerialClient.OnScanListener() {
            String message ="";
            @Override
            public void onStart() {
                Log.d("Test", "Scan Start.");
                mLoadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                mLoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mLoadingDialog.setTitleText("Scanning....");
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.show();
                message = "Scanning....";
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        BluetoothSerialClient btSet = mClient;
                        btSet.cancelScan(getActivity().getApplicationContext());
                    }
                });
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) {
                addDeviceToArrayAdapter(bluetoothDevice);
                message += "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress();
                mLoadingDialog.setTitleText(message);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Scan finish.");
                message = "";
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                mDeviceListDialog.show();
            }
        });
    }

    private void connect(BluetoothDevice device) {
        mLoadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        mLoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setTitleText("Connecting....");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        BluetoothSerialClient btSet =  mClient;
        btSet.connect(getActivity().getApplicationContext(), device, mBTHandler);
    }

    private BluetoothSerialClient.BluetoothStreamingHandler mBTHandler = new BluetoothSerialClient.BluetoothStreamingHandler() {
        ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);

        @Override
        public void onError(Exception e) {
            mLoadingDialog.cancel();
            bandBraceletBtn.setText(R.string.bandBracelet);
            manager.hasBandBracelet(false);
            heartRateBtn.setEnabled(false);
            System.out.println("Messgae : Connection error - " +  e.toString());
            Toast.makeText(getActivity(), "Messgae : Connection error - " +  e.toString(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisconnected() {
            bandBraceletBtn.setText(R.string.bandBracelet);
            heartRateBtn.setEnabled(false);
            manager.hasBandBracelet(false);
            mLoadingDialog.cancel();
            System.out.println("Messgae : Disconnected.");
            Toast.makeText(getActivity(), "Messgae : Disconnected.",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onData(byte[] buffer, int length) {
            if(length == 0) return;
            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer, 0, length);
            if(buffer[length - 1] == '\0') {
                // TODO 处理来自蓝牙的数据
                final String data = new String(mmByteBuffer.array(), 0, mmByteBuffer.position());
                System.out.println(mClient.getConnectedDevice().getName() + " : " + data);
                mmByteBuffer.clear();
                Toast.makeText(getActivity(), mClient.getConnectedDevice().getName() + " : " +
                        data,Toast.LENGTH_LONG).show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        processData(data);
                    }
                }.start();
            }
        }

        @Override
        public void onConnected() {
            System.out.println(mClient.getConnectedDevice().getName());
            Toast.makeText(getActivity(), "Messgae : Connected. " + mClient.getConnectedDevice().getName(),Toast.LENGTH_LONG).show();
            mLoadingDialog.cancel();
            bandBraceletBtn.setText(R.string.bandedBracelet);
            heartRateBtn.setEnabled(true);
            manager.hasBandBracelet(true);
            editor = sharedPreferences.edit();
            editor.putString("bracelet", mClient.getConnectedDevice().getAddress());
            editor.apply();
        }
    };

    public void sendStringData(String data) {
        data += '\0';
        byte[] buffer = data.getBytes();
        // TODO 向蓝牙设备发送数据
        if(!mBTHandler.write(buffer)) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("尚未连接蓝牙设备");
        }
    }

    private void processData(String data) {
        Date now = new Date();
        int index = data.length() - 1;
        for (int i = data.length() - 1; i >= 0; i--) {
            if (Character.isDigit(data.charAt(i))) {
                index = i + 1;
                break;
            }
        }
        data = data.substring(0, index);
        System.out.println("processData: " + data + " and send message is " + sendMessage);
        if ("1".equals(sendMessage)) {      // 心率
            int value;
            try {
                value = Integer.parseInt(data);
            } catch (NumberFormatException e) {
                value = -1;
            }
            if (value != -1) {
                isProcessData = true;
                heartTimes = value;
                HeartState state = new HeartState();
                state.setTimes(value);
                state.setStatus("heart");
                state.setStartTime(new Date(now.getTime() - 1000));
                state.setEndTime(now);
                postState(state);
            } else {
                isProcessData = false;
                sendMessage = "1";
                sendStringData(sendMessage);
            }
        } else if ("2".equals(sendMessage)) {   // 计步
            String[] temp = data.split("@");
            if (temp.length == 2) {
                int steps;
                double kilometres;
                try {
                    steps = Integer.parseInt(temp[0]);
                    kilometres = Long.parseLong(temp[1]) / 100000;
                } catch (NumberFormatException e) {
                    steps = -1;
                    kilometres = -1d;
                }
                if (steps != -1 && kilometres != -1d) {
                    manager.setSteps(manager.getSteps() + steps);
                    manager.setKilometres(manager.getKilometres() + kilometres);
                    SportState state = new SportState();
                    long lust = sharedPreferences.getLong("LastUpdateStateTime", getStateStartTime.getTime());
                    manager.setLastUpdateStateTime(new Date(lust));
                    state.setStartTime(manager.getLastUpdateStateTime());
                    state.setEndTime(now);
                    state.setStatus("sport");
                    state.setSportType("walk");
                    state.setSteps((long)steps);
                    state.setKilometre(kilometres);
                    manager.setLastUpdateStateTime(now);
                    editor = sharedPreferences.edit();
                    editor.putLong("LastUpdateStateTime", now.getTime());
                    editor.apply();
                    isProcessData = true;
                    postState(state);
                    sendMessage = "5";
                    for (int i = 0; i < 3; i++) {
                        sendStringData(sendMessage);
                    }
                } else {
                    isProcessData = false;
                    sendMessage = "2";
                    sendStringData(sendMessage);
                }
            } else {
                isProcessData = false;
                sendMessage = "2";
                sendStringData(sendMessage);
            }
        } else if ("3".equals(sendMessage)) {   // 睡眠
            String[] temp = data.split("#");
            if (temp.length == 2) {
                long start, end;
                try {
                    start = Long.parseLong(temp[0]);
                    end = Long.parseLong(temp[1]);
                } catch (NumberFormatException e) {
                    start = -1;
                    end = -1;
                }
                if (start != -1 && end != -1) {
                    isProcessData = true;
                    if (start > 0 && end > 0) {
                        Date startTime = new Date(manager.getTimePair().second + (start - manager.getTimePair().first));
                        Date endTime = new Date(manager.getTimePair().second + (end - manager.getTimePair().first));
                        SleepState state = new SleepState();
                        state.setStatus("sleep");
                        state.setStartTime(startTime);
                        state.setEndTime(endTime);
                        state.setSleepType("睡眠");
                        manager.setLastSleepTimes(end - start);
                        postState(state);
                    }
                    sendMessage = "6";
                    for (int i = 0; i < 3; i++) {
                        sendStringData(sendMessage);
                    }
                } else {
                    isProcessData = false;
                    sendMessage = "3";
                    sendStringData(sendMessage);
                }
            } else {
                isProcessData = false;
                sendMessage = "3";
                sendStringData(sendMessage);
            }
        } else if ("4".equals(sendMessage)) {
            long value;
            try {
                value = Long.parseLong(data);
            } catch (NumberFormatException e) {
                value = -1;
            }
            if (value != -1) {
                isProcessData = true;
                manager.setTimePair(new Pair<>(value, now.getTime()));
            } else {
                isProcessData = false;
                sendMessage = "4";
                sendStringData(sendMessage);
            }
        } else {
            int value;
            try {
                value = Integer.parseInt(data);
            } catch (NumberFormatException e) {
                value = -1;
            }
            if (value == 8) {
                notifyFriendsTrouble();
                sendMessage = "7";
                for (int i = 0; i < 3; i++) {
                    sendStringData(sendMessage);
                }
            }
        }
    }

    private <T extends State> void postState(T state) {
        if (state instanceof SportState) {
            sportStateJsonString = state.toString();
            new Thread(postSportRequest).start();
        } else if (state instanceof  SleepState) {
            sleepStateJsonString = state.toString();
            new Thread(postSleepRequest).start();
        } else if (state instanceof HeartState) {
            heartStateJsonString = state.toString();
            new Thread(postHeartRequest).start();
        }
    }

    private void notifyFriendsTrouble() {
        new Thread(notifyFriendRequest).start();
    }

    private void initLoading() {
        new Thread(getSportRequest).start();
        new Thread(getSleepRequest).start();
        long lust = sharedPreferences.getLong("LastUpdateStateTime", -1);
        if (lust == -1) {
            lust = new Date().getTime();
            editor = sharedPreferences.edit();
            editor.putLong("LastUpdateStateTime", lust);
            editor.apply();
        }
        manager.setLastUpdateStateTime(new Date(lust));
    }

    Handler bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            System.out.println("bluetooth handle message: " + msg.what);
            switch (msg.what) {
                case 1: {
                    pDialog.dismissWithAnimation();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("当前心率值为")
                            .setContentText(String.valueOf(heartTimes))
                            .show();
                    break;
                }
                case 2: {
                    stepsTV.setText(manager.getSteps() + "步");
                    kilometreTV.setText(manager.getKilometres() + "公里");
                    break;
                }
                case 3: {
                    pDialog.dismissWithAnimation();
                    sleepTV.setText(manager.getLastSleepTimes() / (1000d * 60d * 60d) + "小时");
                    break;
                }
            }
        }
    };

    Runnable notifyFriendRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            String content = "我不慎跌倒了，可能有危险，如果可以的话，请联系我，谢谢！";
            notifyFriendResult = Webservice.sendMessages(content, manager.getUser().getId());
            int resCode;
            try {
                if (notifyFriendResult != null) {
                    resCode = notifyFriendResult.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            notifyFriendsHandler.sendMessage(msg);
        }
    };

    Handler notifyFriendsHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if (msg.what != MessageCode.MSG_REQUEST_SUCCESSFUL) {
                notifyFriendResult = null;
                new Thread(notifyFriendRequest).start();
            } else {
                try {
                    manager.setUser(new User(notifyFriendResult.getJSONObject("user").toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable postSportRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            postSportResult = Webservice.addStates(sportStateJsonString);
            int resCode;
            try {
                if (postSportResult != null) {
                    resCode = postSportResult.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            postSportHandler.sendMessage(msg);
        }
    };

    Handler postSportHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if (msg.what != MessageCode.MSG_REQUEST_SUCCESSFUL) {
                postSportResult = null;
                new Thread(postSportRequest).start();
            }
        }
    };

    Runnable postHeartRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            postHeartResult = Webservice.addStates(heartStateJsonString);
            int resCode;
            try {
                if (postHeartResult != null) {
                    resCode = postHeartResult.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            postHeartHandler.sendMessage(msg);
        }
    };

    Handler postHeartHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if (msg.what != MessageCode.MSG_REQUEST_SUCCESSFUL) {
                postHeartResult = null;
                new Thread(postHeartRequest).start();
            }
        }
    };

    Runnable postSleepRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            postSleepResult = Webservice.addStates(sleepStateJsonString);
            int resCode;
            try {
                if (postSleepResult != null) {
                    resCode = postSleepResult.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            postSleepHandler.sendMessage(msg);
        }
    };

    Handler postSleepHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if (msg.what != MessageCode.MSG_REQUEST_SUCCESSFUL) {
                postSleepResult = null;
                new Thread(postSleepRequest).start();
            }
        }
    };


    Runnable getSleepRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            getSleepResult = Webservice.getStates("sleep", getStateStartTime.getTime(), getStateEndTime.getTime());
            int resCode;
            try {
                if (getSleepResult != null) {
                    resCode = getSleepResult.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            getSleepHandler.sendMessage(msg);
        }
    };

    Handler getSleepHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if (msg.what != MessageCode.MSG_REQUEST_SUCCESSFUL) {
                getSleepResult = null;
                new Thread(getSleepRequest).start();
            } else {
                try {
                    double hours = 0;
                    JSONArray stateArray = getSleepResult.getJSONArray("states");
                    for (int i = 0; i < stateArray.length(); i++) {
                        SportState state = new SportState(stateArray.getJSONObject(i).toString(), manager.getUser());
                        hours += (state.getEndTime().getTime() - state.getStartTime().getTime()) / (1000d * 60d * 60d);
                    }
                    manager.setSleepTime(hours);
                    updateET();
                } catch (JSONException e) {

                }
                getSleepResult = null;
            }
        }
    };

    Runnable getSportRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            getSportResult = Webservice.getStates("sport", getStateStartTime.getTime(), getStateEndTime.getTime());
            int resCode;
            try {
                if (getSportResult != null) {
                    resCode = getSportResult.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            getSportHandler.sendMessage(msg);
        }
    };

    Handler getSportHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if (msg.what != MessageCode.MSG_REQUEST_SUCCESSFUL) {
                getSportResult = null;
                new Thread(getSportRequest).start();
            } else {
                try {
                    int steps = 0;
                    double kilometre = 0;
                    JSONArray stateArray = getSportResult.getJSONArray("states");
                    for (int i = 0; i < stateArray.length(); i++) {
                        SportState state = new SportState(stateArray.getJSONObject(i).toString(), manager.getUser());
                        steps += state.getSteps();
                        kilometre += state.getKilometre();
                    }
                    manager.setSteps(steps);
                    manager.setKilometres(kilometre);
                    updateET();
                } catch (JSONException e) {

                }
                getSportResult = null;
            }
        }
    };
}
