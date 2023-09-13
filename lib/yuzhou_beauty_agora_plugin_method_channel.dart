import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'yuzhou_beauty_agora_plugin_platform_interface.dart';
import 'simple_beauty_type.dart';

/// An implementation of [YuzhouBeautyAgoraPluginPlatform] that uses method channels.
class MethodChannelYuzhouBeautyAgoraPlugin extends YuzhouBeautyAgoraPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('yuzhou_beauty_agora_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> turnOnBeauty(int engineHandle) {
    return methodChannel.invokeMethod('turnOnBeauty', engineHandle);
  }

  @override
  Future<void> turnOffBeauty(int engineHandle) {
    return methodChannel.invokeMethod('turnOffBeauty', engineHandle);
  }

  @override
  Future<void> setSimpleBeautyValue(SimpleBeautyType type, double value) {
    return methodChannel.invokeMethod('setSimpleBeautyValue', {
      "type": type.toString().split(".").last,
      "value": value,
    });
  }

  @override
  Future<void> setMakeup(String path, double style_value, double lut_value) {
    return methodChannel.invokeMethod('setMakeup', {
      "path": path,
      "style_value": style_value,
      "lut_value": lut_value,
    });

  }

  @override
  Future<void> setSticker(String path) {
    return methodChannel.invokeMethod('setSticker', {"path": path});
  }

  @override
  Future<void> setLicense(String license) {
    return methodChannel.invokeMethod('setLicense', license);
  }

  @override
  void init() async {
    return methodChannel.invokeMethod('init');
  }

  @override
  void dispose() async {
    return methodChannel.invokeMethod('dispose');
  }
}
