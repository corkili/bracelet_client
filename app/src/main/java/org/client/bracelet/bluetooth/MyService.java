package org.client.bracelet.bluetooth;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;


public class MyService extends Service{

    public boolean threadFlag = true;
    MyThread myThread;
    CommandReceiver cmdReceiver;

    static final int CMD_STOP_SERVICE = 0x01;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_SYSTEM_EXIT =0x03;
    static final int CMD_SHOW_TOAST =0x04;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream  inStream = null;
    public  boolean bluetoothFlag  = true;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "98:D3:32:30:B9:D5";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        cmdReceiver = new CommandReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.cmd");
        registerReceiver(cmdReceiver, filter);
        doJob();
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(cmdReceiver);
        threadFlag = false;
        boolean retry = true;
        while(retry){
            try{
                myThread.join();
                retry = false;
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            connectDevice();
            while(threadFlag){
                try {
                    byte[] buffer = new byte[1024];
                    int length = inStream.read(buffer);
                    if (length != 0) {
                        showToast("收到来自蓝牙的消息:" + new String(buffer, 0, length)
                                + "(" + length + ")");
                    }
                } catch (IOException e) {
                    showToast("读取信息错误");
                } catch (NullPointerException e) {

                }
            }
        }
    }

    public void doJob(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showToast("蓝牙设备不可用，请打开蓝牙！");
            bluetoothFlag  = false;
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            showToast("请打开蓝牙并重新运行程序！");
            bluetoothFlag  = false;
            stopService();
            showToast("请打开蓝牙并重新运行程序！");
            return;
        }
        showToast("搜索到蓝牙设备!");
        threadFlag = true;
        myThread = new MyThread();
        myThread.start();
    }

    public  void connectDevice(){
        showToast("正在尝试连接蓝牙设备，请稍后···");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            showToast("套接字创建失败");
            bluetoothFlag = false;
        }
        showToast("成功连接蓝牙设备");
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket.connect();
            showToast("连接成功建立，可以开始操控了!");
            bluetoothFlag = true;
        } catch (IOException e) {
            try {
                btSocket.close();
                bluetoothFlag = false;
            } catch (IOException e2) {
                showToast("连接没有建立，无法关闭套接字");
            }
        }

        if(bluetoothFlag){
            try {
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

//    public void sendCmd(byte cmd, int value) {
//        if(!bluetoothFlag){
//            return;
//        }
//
//        byte[] msgBuffer = new byte[5];
//        msgBuffer[0] = cmd;
//        msgBuffer[1] = (byte)(value >> 0  & 0xff);
//        msgBuffer[2] = (byte)(value >> 8  & 0xff);
//        msgBuffer[3] = (byte)(value >> 16 & 0xff);
//        msgBuffer[4] = (byte)(value >> 24 & 0xff);
//
//        try {
//            outStream.write(msgBuffer, 0, 5);
//            outStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void sendStringMsg(String s) {
        byte[] buffer = s.getBytes();
        try {
            outStream.write(buffer);
            outStream.flush();
        } catch (IOException e) {
            showToast("消息发送出错！");
        } catch (NullPointerException e) {

        }
        showToast("发送的消息为：" + s);
    }

    public int readByte(){
        int ret = -1;
        if(!bluetoothFlag){
            return ret;
        }
        try {
            ret = inStream.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void stopService(){
        threadFlag = false;
        stopSelf();
    }

    public void showToast(String str){
        Intent intent = new Intent();
        intent.putExtra("cmd", CMD_SHOW_TOAST);
        intent.putExtra("str", str);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
    }

    private class CommandReceiver extends BroadcastReceiver{
        int count = 0;
        String test[] = {"hello", "1", "2", "3", "4", "5", "879655#20", "888@308"};
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.intent.action.cmd")){
                int cmd = intent.getIntExtra("cmd", -1);
                if(cmd == CMD_STOP_SERVICE){
                    stopService();
                }
                if(cmd == CMD_SEND_DATA)
                {
                    byte command = intent.getByteExtra("command", (byte) 0);
                    int value = intent.getIntExtra("value", 1);
//                    sendCmd(command,value);
                    sendStringMsg(test[count % test.length]);
                    count++;
                }

            }
        }
    }

}
