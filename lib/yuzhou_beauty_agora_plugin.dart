
import 'package:yuzhou_beauty_agora_plugin/simple_beauty_type.dart';

import 'yuzhou_beauty_agora_plugin_platform_interface.dart';

class YuzhouBeautyAgoraPlugin {
  static Future<String?> getPlatformVersion() {
    return YuzhouBeautyAgoraPluginPlatform.instance.getPlatformVersion();
  }

  static Future<void> turnOnBeauty(int engineHandle) {
    return YuzhouBeautyAgoraPluginPlatform.instance.turnOnBeauty(engineHandle);
  }

  static Future<void> turnOffBeauty(int engineHandle) {
    return YuzhouBeautyAgoraPluginPlatform.instance.turnOffBeauty(engineHandle);
  }

  static Future<void> setSimpleBeautyValue(SimpleBeautyType type, double value) {
    return YuzhouBeautyAgoraPluginPlatform.instance.setSimpleBeautyValue(type, value);
  }

  static Future<void> setMakeup(String path, double style_value, double lut_value) {
    return YuzhouBeautyAgoraPluginPlatform.instance.setMakeup(path, style_value, lut_value);

  }

  static Future<void> setSticker(String path) {
    return YuzhouBeautyAgoraPluginPlatform.instance.setSticker(path);
  }

  static Future<void> setLicense(String license) {
    return YuzhouBeautyAgoraPluginPlatform.instance.setLicense(license);
  }




}
