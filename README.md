# flutter_notification_plugin

Flutter Foregound Notification Plugin for long running operations like continuous alarm functionality.

## Getting Started

Add the plugin in the pubspec file
```
  flutter_notification_plugin:          
    git:
      url: https://github.com/ketansignity/flutter_alarm_notification_plugin
```

To start the foreground notification alarm 

```dart
  void startForegroundService() async {
    await FlutterNotificationPlugin.setServiceMethodInterval(seconds: 5);
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
        iconName: "ic_notification",
        stopAction: true,
        stopIcon: "ic_notification",
        stopText: "Dismiss",
        sound: 'order_recieved');
  }
```
To stop the foregound notification

```dart
  await FlutterNotificationPlugin.stopForegroundService();
```

Add sound file in the android > app > src > main > res > raw > order_recieved.mp3