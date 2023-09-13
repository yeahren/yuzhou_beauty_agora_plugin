import 'dart:developer';
import 'dart:io' show Platform;

import 'package:agora_rtc_engine/agora_rtc_engine.dart';
import 'package:yuzhou_beauty_agora_plugin/simple_beauty_type.dart';
import 'package:yuzhou_beauty_agora_plugin/yuzhou_beauty_agora_plugin.dart';
import 'config/agora.config.dart' as config;
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';



void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late RtcEngine engine;
  bool startPreview = false, isJoined = false;
  bool turnOnBeauty = false;
  int curMarkupCount = 0;
  int curStickCount = 0;
  List<int> remoteUid = [];

  static late String license;


  static const makeup_root_path_android = "/data/user/0/com.sc.jojo/files/cosmos/makeup_style";
  static const makeup_root_path_ios = "makeup.bundle/makeup_style";
  static const makeup_values = [
    "yutu", "yuanqi", "xiaoxiangzhuang", "wugu", "shenmi",
    "qiuri", "putaobing", "mitao", "hunxue", "baixue"
  ];

  static const sticker_root_path_android = "/data/user/0/com.sc.jojo/files/cosmos/sticker";
  static const sticker_root_path_ios = "sticker.bundle";
  static const sticker_values = [
    "aircraft",
    "dtqh",
    "fgj",
    "gg",
    "hg",
    "hh",
    "jsyj",
    "lmyj",
    "rqq",
    "sm"
  ];

  @override
  void initState() {
    super.initState();
    this._initEngine();

    //test only
    YuzhouBeautyAgoraPlugin.init();
    YuzhouBeautyAgoraPlugin.dispose();


    YuzhouBeautyAgoraPlugin.init();

  }

  @override
  void dispose() {
    super.dispose();
    YuzhouBeautyAgoraPlugin.dispose();
    this._deinitEngine();
  }

  _initEngine() async {
    if (defaultTargetPlatform == TargetPlatform.android) {
      await [Permission.microphone, Permission.camera].request();
    }

    engine = createAgoraRtcEngine();
    await engine.initialize(RtcEngineContext(
        appId: config.appId,
        channelProfile: ChannelProfileType.channelProfileLiveBroadcasting));

    engine.registerEventHandler(RtcEngineEventHandler(
        onJoinChannelSuccess: (RtcConnection connection, int elapsed) {
      log('onJoinChannelSuccess connection: ${connection.toJson()} elapsed: $elapsed');
      setState(() {
        isJoined = true;
      });
    }, onUserJoined: (RtcConnection connection, int rUid, int elapsed) {
      log('onUserJoined connection: ${connection.toJson()} remoteUid: $rUid elapsed: $elapsed');
      setState(() {
        remoteUid.add(rUid);
      });
    }, onUserOffline:
            (RtcConnection connection, int rUid, UserOfflineReasonType reason) {
      log('onUserOffline connection: ${connection.toJson()} remoteUid: $rUid reason: $reason');
      setState(() {
        remoteUid.remove(rUid);
      });
    }));
    await engine.disableAudio();
    await engine.enableVideo();
    var captureConfig = CameraCapturerConfiguration(
        cameraDirection: CameraDirection.cameraFront,
        format: VideoFormat(width: 240, height: 640, fps: 15));
    await engine.setCameraCapturerConfiguration(captureConfig);

    await engine.startPreview();
    setState(() {
      startPreview = true;
    });

    await engine.joinChannel(
        token: config.token,
        channelId: config.channelId,
        uid: config.uid,
        options: ChannelMediaOptions(
            clientRoleType: ClientRoleType.clientRoleBroadcaster));
    await engine.setRecordingAudioFrameParameters(
        sampleRate: 48000,
        channel: 2,
        mode: RawAudioFrameOpModeType.rawAudioFrameOpModeReadOnly,
        samplesPerCall: 1024);
    //await YuzhouBeautyAgoraPlugin.unregisterVideoFrameObserver(handle);
  }

  _deinitEngine() async {
    await engine.release();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Stack(
          children: [
            if (startPreview)
              AgoraVideoView(
                controller: VideoViewController(
                  rtcEngine: engine,
                  canvas: VideoCanvas(uid: 0),
                ),
              ),
            Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    TextButton(
                        onPressed: !turnOnBeauty
                            ? () async {
                          if(Platform.isAndroid) {
                            license = "";
                          }
                          else if(Platform.isIOS) {
                            license = "";
                          }

                          await YuzhouBeautyAgoraPlugin.setLicense(license);

                          var handle = await engine.getNativeHandle();
                          await YuzhouBeautyAgoraPlugin.turnOnBeauty(handle);

                          //test only
                          YuzhouBeautyAgoraPlugin.dispose();
                          YuzhouBeautyAgoraPlugin.init();

                          // re-try
                          handle = await engine.getNativeHandle();
                          await YuzhouBeautyAgoraPlugin.turnOnBeauty(handle);

                          setState(() {
                            turnOnBeauty = true;
                          });
                        }
                            : () async {
                          var handle = await engine.getNativeHandle();
                          await YuzhouBeautyAgoraPlugin.turnOffBeauty(handle);

                          setState(() {
                            turnOnBeauty = false;
                          });
                        },
                        child: Text(turnOnBeauty ? 'Beauty Off' : 'Beauty On')),
                    TextButton(
                        onPressed: () {
                          YuzhouBeautyAgoraPlugin.setSimpleBeautyValue(SimpleBeautyType.FACE_WIDTH, 0.5);
                          YuzhouBeautyAgoraPlugin.setSimpleBeautyValue(SimpleBeautyType.BIG_EYE, 1.0);
                        },
                        child: Text("美颜")
                    ),
                    TextButton(
                        onPressed: () {
                          var value = makeup_values[curMarkupCount++ % makeup_values.length];

                          var trueRootPath = "";

                          if(Platform.isAndroid) {
                            trueRootPath = makeup_root_path_android;
                          }
                          else if(Platform.isIOS) {
                            trueRootPath = makeup_root_path_ios;
                          }

                          YuzhouBeautyAgoraPlugin.setMakeup(
                              "$trueRootPath/$value",
                              0.9,
                              0.4
                          );

                          setState(() {
                            curMarkupCount;
                          });
                        },
                        child: Text("风格装")
                    ),
                    TextButton(
                        onPressed: () {

                          var value = sticker_values[curStickCount++ % sticker_values.length];

                          var trueRootPath = "";

                          if(Platform.isAndroid) {
                            trueRootPath = sticker_root_path_android;
                          }
                          else if(Platform.isIOS) {
                            trueRootPath = sticker_root_path_ios;
                          }


                          YuzhouBeautyAgoraPlugin.setSticker(
                              "$trueRootPath/$value"
                          );

                          setState(() {
                            curStickCount;
                          });
                        },
                        child: Text("贴纸")
                    ),
                  ],
                ),
                Text("markup: " + (curMarkupCount == 0 ? "--" : makeup_values[(curMarkupCount - 1 ) % makeup_values.length]) +
                    "\n" + "sticker: " + (curStickCount == 0 ? "--" : sticker_values[(curStickCount - 1 ) % sticker_values.length])),
              ],
            )
            


          ],
        ),
      ),
    );
  }
}
