import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.FlightControllerState;
import dji.sdk.flightcontroller.virtualstick.FlightControlData;
import dji.sdk.flightcontroller.virtualstick.VirtualStickFlightControlMode;
import dji.sdk.flightcontroller.virtualstick.VirtualStickRollPitchControlMode;
import dji.sdk.flightcontroller.virtualstick.VirtualStickVerticalControlMode;
import dji.sdk.flightcontroller.virtualstick.VirtualStickYawControlMode;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {

    private HubConnection hubConnection;
    private TextView signalRTextView;
    private FlightController flightController;
    private VirtualStickFlightControlMode virtualStickFlightControlMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signalRTextView = findViewById(R.id.signalRTextView);

        // Initialize SignalR connection
        hubConnection = HubConnectionBuilder.create("https://your-signalr-server-url.com/hubs/drone")
                .build();
        hubConnection.on("Command", this::handleSignalRCommand);
        hubConnection.start();

        // Set up the video stream
        VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(videoDataListener);

        // Handle virtual stick inputs
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                Aircraft aircraft = (Aircraft) product;
                flightController = aircraft.getFlightController();
                flightController.setVirtualStickModeEnabled(true, new DJIError());
                flightController.setRollPitchControlMode(VirtualStickRollPitchControlMode.VELOCITY);
                flightController.setYawControlMode(VirtualStickYawControlMode.ANGULAR_VELOCITY);
                flightController.setVerticalControlMode(VirtualStickVerticalControlMode.VELOCITY);
                virtualStickFlightControlMode = new VirtualStickFlightControlMode(VirtualStickRollPitchControlMode.VELOCITY, VirtualStickYawControlMode.ANGULAR_VELOCITY, VirtualStickVerticalControlMode.VELOCITY);

            }}}}
