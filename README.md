<h1 align="center">Drone AI vision control app</h1>
<h2 align="center">based on DJI Mobile SDK V4 sample app</h2>

## :book: Introduction
Before you start reading this README, I strongly recommend reading the one from the [original repo](https://github.com/dji-sdk/Mobile-SDK-Android/).
This app is dedicated to work with Drone AI vision system from [this repo][drone_platform].

## :hammer: Tech setup
This demo is prepared to use with **DJI** drones which supports MobileSDK **4.16.4** (V5 drones are not supported - like Mini 3 series). In my case it is DJI Mini 2.
The orifinal application was extended by adding new view called "DronePlarformView.java".
 This application has three main tasks: 
- send RTMP stream (and live preview on the phone)
- receive SignalR messages 
- and execute commands to drone
**For more details look on "Diagram_Drone_Complex.png"**

## :iphone: Usage
Refere to original repo to lern how to get to Views list. Drone Platform view will be the first one.

Please ensure that Nginx RTMP & ControlBroker service is started as described in [this repo][drone_platform]
Put your ngrok 
## :memo: License
**AGPL-3.0 License**


[drone_platform]: https://github.com/pazdzioch87/Drone-AI-Vision-Control
