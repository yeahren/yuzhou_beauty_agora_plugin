#import <Flutter/Flutter.h>
#import "CEBeautyRender.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface YuzhouBeautyAgoraPlugin : NSObject<FlutterPlugin>
@property (nonatomic, strong) CEBeautyRender* beautyRender;
@property (nonatomic, assign) BOOL turnOn ;
@property (nonatomic, strong) NSString* license;
@end
