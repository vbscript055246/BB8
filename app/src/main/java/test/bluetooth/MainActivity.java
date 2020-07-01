package test.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;

    ArrayList<String> strTemp = new ArrayList<String>();

    ImageView mBlueIv;
    Button mOnBtn, mConnectBtn;
    TextView mShowText;
    JoystickView joystic;

    BluetoothAdapter mBlueAdapter;
    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;
    String MODULE_MAC = "98:D3:34:90:6F:A1";
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    Handler mHandler;
    ConnectedThread btt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystic = findViewById(R.id.joyStick);

        mBlueIv       = findViewById(R.id.bluetoothIv);
        mOnBtn        = findViewById(R.id.onBtn);
        mConnectBtn   = findViewById(R.id.connectBtn);
        mShowText     = findViewById(R.id.showText);

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBlueAdapter == null)   showToast("Bluetooth isn't available");
        else showToast("Bluetooth is available");

        if (btt != null) mBlueIv.setImageResource(R.drawable.bluetooth_open);// mBlueAdapter.isEnabled()
        else mBlueIv.setImageResource(R.drawable.bluetooth_close);

        joystic.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(btt == null){
                    mShowText.setText("角度: " + angle + ", 強度: " + strength);
                    return;
                }
                String msg = "";
                String S = (strength > 75) ? "B" : (strength > 40) ? "S" : "0";
                String vectorV = (angle >= 22.5 && angle <= 157.5)? "1":(angle >= 202.5 && angle <= 337.5)? "2": "N";
                String vectorH = ((angle > 0 && angle <= 67.5)||(angle <=360 && angle >= 292.5))? "3":(angle >= 112.5 && angle <= 247.5)? "4": "M";
                msg = vectorV + vectorH + S;
                String tmp = mShowText.getText().toString();
                if(!tmp.equals(msg)){
                    btt.write(vectorV.getBytes());
                    btt.write(vectorH.getBytes());
                    btt.write(S.getBytes());
                    mShowText.setText(msg);
                }
            }
        }, 4);

        //on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isEnabled()){
                    showToast("Turning On Bluetooth...");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else {
                    showToast("Bluetooth is already on");
                }
            }
        });

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()){
                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    for (BluetoothDevice device: devices){
                        strTemp.add(device.getName() + "," + device.getAddress());
                    }
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, BluetoothSelectDevice.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("test", strTemp);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,1 );
                }
                else {
                    mBlueIv.setImageResource(R.drawable.bluetooth_close);
                    showToast("Turn on bluetooth to get paired devices");
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    //bluetooth is on
                    //mBlueIv.setImageResource(R.drawable.bluetooth_open);
                    showToast("Bluetooth is on");
                }
                else {
                    //user denied to turn bluetooth on
                    //mBlueIv.setImageResource(R.drawable.bluetooth_close);
                    showToast("could't on bluetooth");
                }
                break;
            case 1:
                strTemp.clear();
                switch (resultCode) {
                    case RESULT_OK:
                        String[] tokens = data.getStringExtra("device").split(",");
                        MODULE_MAC = tokens[1];
                        Log.i("[Android]","Select Device Name: " + tokens[0]);
                        Log.i("[Android]","Select Device MAC: " + MODULE_MAC);
                        initiateBluetoothProcess();
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void initiateBluetoothProcess(){
        Log.i("[BLUETOOTH]","start init BlueTooth");
        mmDevice = mBlueAdapter.getRemoteDevice(MODULE_MAC);
        try {
            if (mmSocket != null && mmSocket.isConnected()){
                mmSocket.close();
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            Log.i("[BLUETOOTH]","Connected to: "+ mmSocket.getRemoteDevice().getName());
            mmSocket.connect();
        }catch(IOException e){
            try{
                mmSocket.close();
                Log.e("[BLUETOOTH]",e.toString());
                showToast("Connected to: "+ mmSocket.getRemoteDevice().getName() + " Failed");
                mBlueIv.setImageResource(R.drawable.bluetooth_close);
            }catch(IOException c){
                Log.e("[BLUETOOTH]",c.toString());
                return;
            }
            return;
        }
        Log.i("[BLUETOOTH]", "Creating handler");
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
                    String txt = (String)msg.obj;
                    showToast("rec: " + txt);
                }
            }
        };
        Log.i("[BLUETOOTH]", "Creating and running Thread");
        btt = new ConnectedThread(mmSocket,mHandler);
        btt.start();
        mBlueIv.setImageResource(R.drawable.bluetooth_open);
    }
}


