import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:yuzhou_beauty_agora_plugin/simple_beauty_type.dart';

import 'yuzhou_beauty_agora_plugin_method_channel.dart';

abstract class YuzhouBeautyAgoraPluginPlatform extends PlatformInterface {
  /// Constructs a YuzhouBeautyAgoraPluginPlatform.
  YuzhouBeautyAgoraPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static YuzhouBeautyAgoraPluginPlatform _instance = MethodChannelYuzhouBeautyAgoraPlugin();

  /// The default instance of [YuzhouBeautyAgoraPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelYuzhouBeautyAgoraPlugin].
  static YuzhouBeautyAgoraPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [YuzhouBeautyAgoraPluginPlatform] when
  /// they register themselves.
  static set instance(YuzhouBeautyAgoraPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> turnOnBeauty(int engineHandle) {
    throw UnimplementedError('turnOnBeauty() has not been implemented.');
  }

  Future<void> turnOffBeauty(int engineHandle) {
    throw UnimplementedError('turnOffBeauty() has not been implemented.');
  }

  Future<void> setSimpleBeautyValue(SimpleBeautyType type, double value) {
    throw UnimplementedError('setSimpleBeautyValue() has not been implemented.');
  }

  Future<void> setMakeup(String path, double style_value, double lut_value) {
    throw UnimplementedError('setMakeup() has not been implemented.');

  }

  Future<void> setSticker(String path) {
    throw UnimplementedError('setSticker() has not been implemented.');
  }

  Future<void> setLicense(String license) {
    throw UnimplementedError('setLicense() has not been implemented.');
  }


}
