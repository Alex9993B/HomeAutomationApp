package uk.ac.napier.homeautomationapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "Main Activity";
    BluetoothAdapter mBlutetoothAdapter;
    Button btnEnableDisable_Discoverable;

    BluetoothConnectionService mBluetoothConnection;

    Button btnStartConnection;
    Button btnSend;

    EditText etSend;
    BluetoothArduino bTArduino;
    String deviceName;
    public static TextView txvResult;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewdevices;

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        Toast toast = Toast.makeText(context, "Bluetooth OFF", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        Toast toast2 = Toast.makeText(context, "Turning off...", Toast.LENGTH_SHORT);
                        toast2.show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE STATE_ON");
                        Toast toast3 = Toast.makeText(context, "Bluetooth turned ON", Toast.LENGTH_SHORT);
                        toast3.show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        Toast toast4 = Toast.makeText(context, "Turning on...", Toast.LENGTH_SHORT);
                        toast4.show();
                        break;
                }

            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.equals(action, BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        Toast toast5 = Toast.makeText(context, "Discoverability enabled", Toast.LENGTH_SHORT);
                        toast5.show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        Toast toast6 = Toast.makeText(context, "Discoverability Disabled. Able to receive connections", Toast.LENGTH_SHORT);
                        toast6.show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Unable to receive connections.");
                        Toast toast7 = Toast.makeText(context, "Discoverability Disabled. Unable to receive connections", Toast.LENGTH_SHORT);
                        toast7.show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        Toast toast8 = Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT);
                        toast8.show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        Toast toast9 = Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);
                        toast9.show();
                        break;
                }

            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");


            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                lvNewdevices = (ListView) findViewById(R.id.lvNewDevices);
                mBTDevices.add(device);

                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewdevices.setAdapter(mDeviceListAdapter);

            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND BONDED.");
                    mBTDevice = mDevice;
                    Toast toast11 = Toast.makeText(context, "Paired with the device", Toast.LENGTH_SHORT);
                    toast11.show();

                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND BONDING.");
                    Toast toast12 = Toast.makeText(context, "Pairing...", Toast.LENGTH_SHORT);
                    toast12.show();

                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND NONE.");
                    Toast toast13 = Toast.makeText(context, "Unpaired", Toast.LENGTH_SHORT);
                    toast13.show();

                }

            }


        }
    };
    public Context getThisActivityContext(){
        return this.getApplicationContext();
    }
    public void setMessageForToast(String message){
        Toast.makeText(this.getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = findViewById(R.id.btnONOFF);
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btn_discoverable_on_off);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);
        lvNewdevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();


        btnStartConnection = (Button) findViewById(R.id.btnStartConnection);



        mBlutetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvNewdevices.setOnItemClickListener(MainActivity.this);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "OnClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
                bTArduino = BluetoothArduino.getInstance(deviceName);
                bTArduino.setContext(getApplicationContext());
            }
        });




        txvResult = findViewById(R.id.txvResult);

    }



    public void startConnection(){
        try {
            startBTConnection(mBTDevice, MY_UUID_INSECURE);
            deviceName = mBTDevice.getName();
        } catch (NullPointerException exception){
            Toast.makeText(this,"Please, select a bluetooth device to connect to first", Toast.LENGTH_SHORT).show();
        }
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");
    try {
        mBluetoothConnection.startClient(device, uuid);
    } catch (Exception e){
        Log.e("ASBluetoothConnection",e.getMessage());
    }
    }


    public void enableDisableBT(){
        try {
            if (mBlutetoothAdapter == null) {
                Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            }
            if (!mBlutetoothAdapter.isEnabled()) {
                Log.d(TAG, "enabledDisableBT: enabling BT.");
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mBroadcastReceiver1, BTIntent);
            }
            if (mBlutetoothAdapter.isEnabled()) {
                Log.d(TAG, "enableDisableBT: disabling BT.");
                mBlutetoothAdapter.disable();

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mBroadcastReceiver1, BTIntent);

            }
        } catch (NullPointerException exception){
            Toast.makeText(this, "There is no Bluetooth antenna", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException exception){
            Toast.makeText(this, "There has been a problem", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnEnableDisable_Discoverable(View view) {

        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds. ");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        IntentFilter intentFilter = new IntentFilter(mBlutetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);


    }

    /**
     * This is to recognise if the device supports speech input recognition.
      * @param view
     */
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);

        } else {
            Toast.makeText(this,"Your device doesn't support speech input", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * This class displays the command of speech recognition
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {


                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));

                    StringBuilder command = new StringBuilder();


                    for (String s : result)
                    {
                        command.append(s);
                        command.append("\t");
                    }



                    String command2 = "" + command;
                    System.out.println(command2);
                    /**
                     * This part sends the message TO the arduino
                     */

                    try {
                        bTArduino.Connect();
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                   try {
                       bTArduino.sendMessage(txvResult.getText().toString());
                       String messageFromConsole= bTArduino.getLastMessage();
                       System.out.println("Message sent");
                       Toast.makeText(getApplicationContext(), messageFromConsole, Toast.LENGTH_LONG).show();
                   } catch (NullPointerException exception){
                       Toast.makeText(this, "Please, connect it to an Arduino first", Toast.LENGTH_SHORT).show();
                   }


                }
                break;
        }
    }

    /**
     * This class gets the last message from the arduino console
     * @param deviceName
     */
    private void getMessageFromArduino(String deviceName){
       bTArduino = BluetoothArduino.getInstance(deviceName);

        try {
            bTArduino.Connect();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        txvResult.setText(bTArduino.getLastMessage());

}

    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        try {
            if (mBlutetoothAdapter.isDiscovering()) {
                mBlutetoothAdapter.cancelDiscovery();
                Log.d(TAG, "btnDiscover: Canceling discovery.");

                checkBTPermissions();

                mBlutetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

            }
            if (!mBlutetoothAdapter.isDiscovering()) {
                checkBTPermissions();
                mBlutetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
        } catch (IllegalStateException exception){
            Toast.makeText(this, "You didn't activate the bluetooth antenna!\n Please turn it on.", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException exception){
            Toast.makeText(this, "You didn't activate the bluetooth antenna!\n Please turn it on.", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkBTPermissions(){

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);{

                }

            } else {

                Log.d(TAG, "checkBTPermissions: No need to check for Permissions. SDK version < LOLLIPOP.");

            }
        }




    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mBlutetoothAdapter.cancelDiscovery();
        Log.d(TAG, "onItemClick: You clicked on a device.");
        deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG,"onItemClick: deviceAddress = " + deviceAddress);

        getMessageFromArduino(deviceName);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }
}



