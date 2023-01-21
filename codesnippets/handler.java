    private void handleSignalRCommand(String command) {
        // Parse the command and extract the roll, pitch, yaw and vertical velocity values
        // Example: "r:0.5,p:-0.2,y:0.1,v:0.3"
        String[] values = command.split(",");
        float roll = Float.parseFloat(values[0].substring(2));
        float pitch = Float.parseFloat(values[1].substring(2));
        float yaw = Float.parseFloat(values[2].substring(2));
        float verticalVelocity = Float.parseFloat(values[3].substring(2));

        // Create a FlightControlData object with the parsed values
        FlightControlData flightControlData = new FlightControlData(roll, pitch, yaw, verticalVelocity);

        // Send the FlightControlData object to the drone to control it via virtual stick
        flightController.sendVirtualStickFlightControlData(flightControlData, new DJIError());
    }

    private VideoFeeder.VideoDataListener videoDataListener = new VideoFeeder.VideoDataListener() {
        @Override
        public void onReceive(byte[] bytes, int i) {
            // Handle the video data here
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the SignalR connection
        hubConnection.stop();
    }
}


