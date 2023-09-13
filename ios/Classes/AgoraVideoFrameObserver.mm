//
//  AgoraVideoFrameObserver.mm
//  react-native-agora-rawdata
//
//  Created by LXH on 2020/11/10.
//

#import "AgoraVideoFrameObserver.h"

#import <AgoraRtcKit/IAgoraMediaEngine.h>
#import <AgoraRtcKit/IAgoraRtcEngine.h>

#import "CEBeautyRender.h"

namespace agora {
class VideoFrameObserver : public media::IVideoFrameObserver {
public:
    VideoFrameObserver(long long engineHandle, void *observer, void *render)
    : engineHandle(engineHandle), observer(observer), render(render), turnOn(false) {
        auto rtcEngine = reinterpret_cast<rtc::IRtcEngine *>(engineHandle);
        if (rtcEngine) {
            util::AutoPtr<media::IMediaEngine> mediaEngine;
            mediaEngine.queryInterface(rtcEngine, agora::rtc::AGORA_IID_MEDIA_ENGINE);
            if (mediaEngine) {
                mediaEngine->registerVideoFrameObserver(this);
            }
        }
    }
    
    virtual ~VideoFrameObserver() {
        auto rtcEngine = reinterpret_cast<rtc::IRtcEngine *>(engineHandle);
        if (rtcEngine) {
            util::AutoPtr<media::IMediaEngine> mediaEngine;
            mediaEngine.queryInterface(rtcEngine, agora::rtc::AGORA_IID_MEDIA_ENGINE);
            if (mediaEngine) {
                mediaEngine->registerVideoFrameObserver(nullptr);
            }
        }
    }
    
    void setTurnOn(bool value) {
        turnOn = value;
    }
    
public:
    bool onCaptureVideoFrame(agora::rtc::VIDEO_SOURCE_TYPE type,
                             VideoFrame &videoFrame) override {
        if(!this->turnOn)
            return true;
        
        NSDictionary* options = @{
            (id)kCVPixelBufferIOSurfaceCoreAnimationCompatibilityKey: @1,
            (id)kCVPixelBufferIOSurfaceOpenGLESFBOCompatibilityKey: @1,
            (id)kCVPixelBufferIOSurfaceOpenGLESTextureCompatibilityKey: @1,
            (id)kCVPixelBufferOpenGLCompatibilityKey: @1,
        };

        CVPixelBufferRef retval = NULL;
        CVReturn status =
            CVPixelBufferCreate(kCFAllocatorDefault,
                                videoFrame.width, videoFrame.height,
                                kCVPixelFormatType_32BGRA,
                                (__bridge CFDictionaryRef)options, &retval);
        if (status != kCVReturnSuccess || !retval) {
          return true;
        }

        CVPixelBufferLockBaseAddress(retval,0);

        auto rowBytes = CVPixelBufferGetBytesPerRow(retval);
        auto ptrData = CVPixelBufferGetBaseAddress(retval);

        for(int i = 0; i < videoFrame.height; ++i) {
            memcpy((char *)ptrData + rowBytes * i,
                   videoFrame.yBuffer + i * videoFrame.yStride,
                   videoFrame.width * 4);
        }
        
        NSError *error;
        CEBeautyRender *myRender = (__bridge CEBeautyRender *)this->render;
        CVPixelBufferRef outputRef = [myRender renderPixelBuffer:retval error:&error];
        
        auto outputRowBytes = CVPixelBufferGetBytesPerRow(outputRef);
        auto outputPtrData = CVPixelBufferGetBaseAddress(outputRef);
        

        for(int i = 0; i < videoFrame.height; ++i) {
            memcpy(videoFrame.yBuffer + i * videoFrame.yStride,
                   (char *)outputPtrData + outputRowBytes * i,
                   videoFrame.width * 4);
        }

        CVPixelBufferUnlockBaseAddress(retval, 0);
        
        CVPixelBufferRelease(retval);
        
        return true;
    }
    
    bool onTranscodedVideoFrame(VideoFrame &videoFrame) override {
        return true;
    }
    
    bool onMediaPlayerVideoFrame(VideoFrame &videoFrame, int mediaPlayerId) override {
        return true;
    }
    
    bool onPreEncodeVideoFrame(agora::rtc::VIDEO_SOURCE_TYPE sourceType, VideoFrame &videoFrame) override {
        return true;
    }
    
    bool onRenderVideoFrame(const char *channelId, rtc::uid_t remoteUid, VideoFrame &videoFrame) override {
        return true;
    }
    
    media::base::VIDEO_PIXEL_FORMAT getVideoFormatPreference() override {
        return media::base::VIDEO_PIXEL_FORMAT::VIDEO_PIXEL_BGRA;
    }
    
    
    media::IVideoFrameObserver::VIDEO_FRAME_PROCESS_MODE
    getVideoFrameProcessMode() override {
        return media::IVideoFrameObserver::PROCESS_MODE_READ_WRITE;
    }
    
private:
    void *observer;
    void *render;
    bool turnOn;
    long long engineHandle;
};
} // namespace agora

@interface AgoraVideoFrameObserver ()
@property (nonatomic, assign) agora::VideoFrameObserver *observer;
@property (nonnull, strong) CEBeautyRender* render;
@end

@implementation AgoraVideoFrameObserver
- (instancetype)initWithEngineHandle:(NSUInteger)engineHandle render:(CEBeautyRender*)render{
    if (self = [super init]) {
        self.engineHandle = engineHandle;
        self.render = render;
    }
    return self;
}

- (void)setTurnOn: (BOOL)value {
    if(_observer) {
        _observer->setTurnOn(value);
    }
}

- (void)registerVideoFrameObserver {
    if (!_observer) {
        _observer =
        new agora::VideoFrameObserver(_engineHandle, (__bridge void *)self, (__bridge  void *)self.render);
    }
}

- (void)unregisterVideoFrameObserver {
    if (_observer) {
        delete _observer;
        _observer = nullptr;
    }
}
@end
