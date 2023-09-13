//
//  AgoraVideoFrameObserver.h
//  react-native-agora-rawdata
//
//  Created by LXH on 2020/11/10.
//

#import <Foundation/Foundation.h>
#import <AgoraRtcKit/AgoraRtcKit.h>

#import "CEBeautyRender.h"

NS_ASSUME_NONNULL_BEGIN

@protocol PluginAgoraVideoFrameDelegate <NSObject>
@required
//- (BOOL)onCaptureVideoFrame:(VideoFrame* _Nonnull)videoFrame sourceType:(AgoraVideoSourceType)sourceType;

@optional

- (AgoraVideoFrameType)getVideoFormatPreference;
- (BOOL)getRotationApplied;
- (BOOL)getMirrorApplied;
- (BOOL)getSmoothRenderingEnabled;
- (uint32_t)getObservedFramePosition;
- (BOOL)isMultipleChannelFrameWanted;

@end

@interface AgoraVideoFrameObserver : NSObject
@property(nonatomic, assign) NSUInteger engineHandle;
@property(nonatomic, weak) id<PluginAgoraVideoFrameDelegate> _Nullable delegate;

- (instancetype)initWithEngineHandle:(NSUInteger)engineHandle render:(CEBeautyRender*)render;

- (void)registerVideoFrameObserver;

- (void)unregisterVideoFrameObserver;

- (void)setTurnOn: (BOOL)value;
@end

NS_ASSUME_NONNULL_END
