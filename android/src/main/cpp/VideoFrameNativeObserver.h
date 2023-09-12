//
// Created by Peter Liu on 2023/8/24.
//

#pragma once

#include <jni.h>

#include "IAgoraRtcEngine.h"
#include "AgoraMediaBase.h"
#include "IAgoraMediaEngine.h"


class VideoFrameNativeObserver : public agora::media::IVideoFrameObserver {
public:
    VideoFrameNativeObserver(
            JNIEnv *env,
            jobject jCaller);

    virtual ~VideoFrameNativeObserver();

    bool onCaptureVideoFrame(agora::rtc::VIDEO_SOURCE_TYPE type,
                             agora::media::base::VideoFrame &videoFrame) override;

    agora::media::base::VIDEO_PIXEL_FORMAT getVideoFormatPreference() override {
        return agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_PIXEL_I422;
    }

    VIDEO_FRAME_PROCESS_MODE getVideoFrameProcessMode() override {
        return PROCESS_MODE_READ_WRITE;
    }

    uint32_t getObservedFramePosition() override {
        return agora::media::base::POSITION_POST_CAPTURER;
    }

    bool getRotationApplied() override {
        return true;
    }

private:

    bool onPreEncodeVideoFrame(agora::rtc::VIDEO_SOURCE_TYPE sourceType,
                               VideoFrame &videoFrame) override {
        return true;
    }

    bool onMediaPlayerVideoFrame(VideoFrame &videoFrame,
                                 int mediaPlayerId) override {
        return true;
    }

    bool onRenderVideoFrame(const char *channelId,
                            uid_t remoteUid,
                            VideoFrame &videoFrame) override {
        return true;
    }

    bool onTranscodedVideoFrame(VideoFrame &videoFrame) override {
        return true;
    }


    typedef std::tuple<jobject, jbyteArray, jbyteArray, jbyteArray> JavaVideoFrameInfo;
    JavaVideoFrameInfo nativeToJavaVideoFrame(JNIEnv *env, VideoFrame &videoFrame);

    bool prepareDelegateMapping(JNIEnv* env);
    bool processVideoFrameYUV422(JNIEnv *env, agora::rtc::VIDEO_SOURCE_TYPE type, VideoFrame &videoFrame);
    bool processVideoFrameTexture(JNIEnv *env, agora::rtc::VIDEO_SOURCE_TYPE type, VideoFrame &videoFrame);
private:
    JNIEnv      *_env{nullptr};
    JNIEnv      *_lastEnv{nullptr};
    JavaVM      *_jvm{nullptr};

    jobject     _jNativeHandlerObj;

    jobject     _jDelegateObj{};
    jclass      _jDelegateCls{};
    jmethodID   _jDelegateOnCaptureVideoFrameMethodID{};

    jclass      _jVideoFrameCls;
    jmethodID   _jVideoFrameInit0MethodID;
    jmethodID   _jVideoFrameInit1MethodID;
};

