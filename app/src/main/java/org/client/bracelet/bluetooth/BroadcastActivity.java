package org.client.bracelet.bluetooth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.client.bracelet.R;

public class BroadcastActivity extends Activity {
    /** Called when the activity is first created. */

	TextView myTextView;
	Button sendButton;
	MyReceiver receiver;
	IBinder serviceBinder;
	MyService mService;
	Intent intent;
	int value = 0;

	static final int CMD_STOP_SERVICE = 0x01;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_SYSTEM_EXIT =0x03;
    static final int CMD_SHOW_TOAST =0x04;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myTextView = (TextView)findViewById(R.id.myTextView);
        myTextView.setText("Season");  
        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new SendButtonClickListener());
                  
        intent = new Intent(BroadcastActivity.this,MyService.class);
        startService(intent);
    }
    
  
    public class SendButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			byte command = 45;
			int value = 0x1;
            sendCmd(command,value);
		} 	
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(receiver!=null){
			BroadcastActivity.this.unregisterReceiver(receiver);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.lxx");
		BroadcastActivity.this.registerReceiver(receiver,filter);
	}

    public void showToast(String str){
    	Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();	
    }


	public class MyReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.lxx")){
			Bundle bundle = intent.getExtras();
			int cmd = bundle.getInt("cmd");
			
			if(cmd == CMD_SHOW_TOAST){
				String str = bundle.getString("str");
			    showToast(str);
			}
			
			else if(cmd == CMD_SYSTEM_EXIT){
				System.exit(0);
			}
			
		}
	 }   
   }

	public void sendCmd(byte command, int value){
		Intent intent = new Intent();
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd", CMD_SEND_DATA);
        intent.putExtra("command", command);
        intent.putExtra("value", value);
        sendBroadcast(intent);
	}
}