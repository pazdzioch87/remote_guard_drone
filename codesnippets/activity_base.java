import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {

    private HubConnection hubConnection;
    private TextView signalRTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signalRTextView = findViewById(R.id.signalRTextView);

        // Initialize SignalR connection
        hubConnection = HubConnectionBuilder.create("https://your-signalr-server-url.com/hubs/drone").build();
        hubConnection.start();

        // Set up the video stream
        VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(videoDataListener);

        // Handle virtual stick inputs
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                Aircraft aircraft = (Aircraft) product;
                aircraft.getFlightController().setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                aircraft.getFlightController().setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                aircraft.getFlightController().setVerticalControlMode(VerticalControlMode.VELOCITY);
                aircraft.getFlightController().setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
            }
        }
    }

    private VideoFeeder.VideoDataListener videoDataListener = new VideoFeeder.VideoDataListener() {
        @Override
        public void onReceive(byte[] bytes, int i) {
            // Handle the video data here
        }
    };

    // Handle incoming messages from the SignalR server
    private void handleSignalRMessage(String message) {
        signalRTextView.setText(message);
        // Use the DJI SDK to control the drone based on the message received
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the SignalR connection
        hubConnection.stop();
    }
}
