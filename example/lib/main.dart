import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_notification_plugin/flutter_notification_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Foreground Service Example'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              ElevatedButton(
                child: Text("START"),
                onPressed: () {
                  startForegroundService();
                },
              ),
              ElevatedButton(
                child: Text("STOP"),
                onPressed: () async {
                  await FlutterNotificationPlugin.stopForegroundService();
                },
              ),
              ElevatedButton(
                child: Text("Force Crash"),
                onPressed: () {
                  exit(1);
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}

void startForegroundService() async {
  await FlutterNotificationPlugin.setServiceMethodInterval(seconds: 5);
  // await FlutterNotificationPlugin.setServiceMethod(globalForegroundService);
  await FlutterNotificationPlugin.startForegroundService(
      holdWakeLock: false,
      onStarted: () {
        print("Foreground on Started");
      },
      onStopped: () {
        print("Foreground on Stopped");
      },
      title: "Flutter Foreground Service",
      content: "This is Content",
      iconName: "ic_stat_hot_tub",
      stopAction: true,
      stopIcon: "ic_stat_hot_tub",
      stopText: "Dismiss",
      sound: "order_recieved");
}

void globalForegroundService() {
  debugPrint("current datetime is ${DateTime.now()}");
}
