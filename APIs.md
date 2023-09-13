*  初始化美颜插件
   *  YuzhouBeautyAgoraPlugin.init()

*  销毁美颜插件
   *  YuzhouBeautyAgoraPlugin.dispose()

*  打开美颜
   *  Future<void> YuzhouBeautyAgoraPlugin.turnOnBeauty(int engineHandle)

```dart
var handle = await engine.getNativeHandle();
await YuzhouBeautyAgoraPlugin.turnOnBeauty(handle);
```

* 关闭美颜
  * Future<void> YuzhouBeautyAgoraPlugin.turnOffBeauty(int engineHandle)


```dart
var handle = await engine.getNativeHandle();
await YuzhouBeautyAgoraPlugin.turnOffBeauty(handle);
```