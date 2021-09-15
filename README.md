# flutter_notification_plugin

Flutter Foregound Notification Plugin for long running operations like continuous alarm functionality.

## Getting Started

1. Add the plugin in the pubspec file
```dart
dependencies:
  flutter_notification_plugin:          
    git:
      url: https://github.com/ketansignity/flutter_alarm_notification_plugin
```

2. To start the foreground notification alarm 

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
3. To stop the foregound notification

```dart
  await FlutterNotificationPlugin.stopForegroundService();
```

4. Add sound file in the android folder
> app > src > main > res > raw > order_recieved.mp3

5. Add permission in the android manifest file

```xml
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```
6. Add sdk configuration below the application tag in manifest file

```xml
    <uses-sdk
        android:minSdkVersion="23"
        tools:overrideLibrary="com.valueappz.flutter_notification_plugin" />
```
7. Add service in the manifest file as well

```xml
    <service android:name="com.valueappz.flutter_notification_plugin.FlutterForegroundService" />
```
