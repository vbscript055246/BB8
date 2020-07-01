package test.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class BluetoothSelectDevice extends AppCompatActivity {

    LinearLayout ll;
    ViewGroup.LayoutParams lp;
    Button PairedBtn;

    private OnClickListener PBC = new OnClickListener(){
        public void onClick(View v){
            Button Btn = (Button)v;
            String str = Btn.getText().toString();
            Intent intent = new Intent();
            intent.putExtra("device", str);
            setResult(RESULT_OK, intent);
            finish();
            Log.i("[BluetoothSelectDevice]","Select Check");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_select_device);

        loadBundle();
    }

    private void loadBundle(){
        lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll = findViewById(R.id.DeviceList);
        ll.setOrientation(LinearLayout.VERTICAL);
        Bundle bundle = this.getIntent().getExtras();
        ArrayList<String> strTemp = bundle.getStringArrayList("test");
        for (String str: strTemp){
            PairedBtn = new Button(BluetoothSelectDevice.this);
            PairedBtn.setText(str);
            PairedBtn.setOnClickListener(PBC);
            ll.addView(PairedBtn, lp);
        }
    }
}
