//
// Created by Peter Liu on 2023/8/24.
//

#include "VideoFrameNativeObserver.h"
#include "VMUtil.h"

#include <tuple>
#include <vector>

bool VideoFrameNativeObserver::onCaptureVideoFrame(agora::rtc::VIDEO_SOURCE_TYPE type,
                                                   agora::media::base::VideoFrame &videoFrame) {
    AttachThreadScoped sc(_jvm);
    JNIEnv *env = sc.env();

    prepareDelegateMapping(env);

    auto ret = true;

    if(videoFrame.type == agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_PIXEL_I422)
        ret = processVideoFrameYUV422(env, type, videoFrame);
    else if(videoFrame.type == agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_TEXTURE_OES)
        ret = processVideoFrameTexture(env, type, videoFrame);

    return ret;
}

VideoFrameNativeObserver::VideoFrameNativeObserver(JNIEnv *env, jobject jCaller)
        : _env(env), _jNativeHandlerObj(env->NewGlobalRef(jCaller)){

    _env->GetJavaVM(&_jvm);

    jclass jClassVideoFrame = _env->FindClass("com/sc/jojo/VideoFrame");
    _jVideoFrameCls = (jclass) env->NewGlobalRef(jClassVideoFrame);
    env->DeleteLocalRef(jClassVideoFrame);

    _jVideoFrameInit0MethodID = _env->GetMethodID(_jVideoFrameCls, "<init>", "(IIIIII[B[B[BIJI)V");
    _jVideoFrameInit1MethodID = _env->GetMethodID(_jVideoFrameCls, "<init>", "(IIIIJI)V");
}

VideoFrameNativeObserver::~VideoFrameNativeObserver() {
    _env->DeleteGlobalRef(_jNativeHandlerObj);
    _env->DeleteGlobalRef(_jVideoFrameCls);
}

VideoFrameNativeObserver::JavaVideoFrameInfo VideoFrameNativeObserver::nativeToJavaVideoFrame(
        JNIEnv *env,
        agora::media::IVideoFrameObserver::VideoFrame &videoFrame) {
    int yLength, uLength, vLength;

    switch (videoFrame.type) {
        case agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_PIXEL_I420: {
            yLength = videoFrame.yStride * videoFrame.height;
            uLength = videoFrame.uStride * videoFrame.height / 2;
            vLength = videoFrame.vStride * videoFrame.height / 2;
            break;
        }
        case agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_PIXEL_I422: {
            yLength = videoFrame.yStride * videoFrame.height;
            uLength = videoFrame.uStride * videoFrame.height;
            vLength = videoFrame.vStride * videoFrame.height;
            break;
        }
        case agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_PIXEL_RGBA: {
            yLength = videoFrame.width * videoFrame.height * 4;
            uLength = 0;
            vLength = 0;
            break;
        }
        case agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_TEXTURE_OES: {
            yLength = 0;
            uLength = 0;
            vLength = 0;
            break;
        }
    }

    jbyteArray yByteArray = env->NewByteArray(yLength);
    jbyteArray uByteArray = env->NewByteArray(uLength);
    jbyteArray vByteArray = env->NewByteArray(vLength);

    if (videoFrame.yBuffer && yLength > 0) {
        env->SetByteArrayRegion(
                yByteArray, 0, yLength,
                reinterpret_cast<const jbyte *>(videoFrame.yBuffer));
    }

    if (videoFrame.uBuffer && uLength > 0) {
        env->SetByteArrayRegion(
                uByteArray, 0, uLength,
                reinterpret_cast<const jbyte *>(videoFrame.uBuffer));
    }

    if (videoFrame.vBuffer && vLength > 0) {
        env->SetByteArrayRegion(
                vByteArray, 0, vLength,
                reinterpret_cast<const jbyte *>(videoFrame.vBuffer));
    }

    jobject javaVideoFrame =
            videoFrame.type != agora::media::base::VIDEO_PIXEL_FORMAT::VIDEO_TEXTURE_OES ?
                             env->NewObject(
                                     _jVideoFrameCls,
                                     _jVideoFrameInit0MethodID,
                                     (int)videoFrame.type,
                                     videoFrame.width,
                                     videoFrame.height,
                                     videoFrame.yStride,
                                     videoFrame.uStride,
                                     videoFrame.vStride,
                                     yByteArray,
                                     uByteArray,
                                     vByteArray,
                                     videoFrame.rotation,
                                     videoFrame.renderTimeMs,
                                     videoFrame.avsync_type
                             )
            :
                             env->NewObject(
                                     _jVideoFrameCls,
                                     _jVideoFrameInit1MethodID,
                                     videoFrame.width,
                                     videoFrame.height,
                                     videoFrame.textureId,
                                     videoFrame.rotation,
                                     videoFrame.renderTimeMs,
                                     videoFrame.avsync_type);

    return { javaVideoFrame, yByteArray, uByteArray, vByteArray};
}

bool VideoFrameNativeObserver::prepareDelegateMapping(JNIEnv *env) {
    if(_lastEnv != env) {
        jclass cls = env->GetObjectClass(_jNativeHandlerObj);
        jfieldID fidDelegate = env->GetFieldID(cls, "delegate",
                                               "Lcom/sc/jojo/VideoFrameObserverDelegate;");
        env->DeleteLocalRef(cls);

        _jDelegateObj = env->GetObjectField(_jNativeHandlerObj, fidDelegate);
        _jDelegateCls = env->GetObjectClass(_jDelegateObj);
        _jDelegateOnCaptureVideoFrameMethodID = env->GetMethodID(_jDelegateCls, "onCaptureVideoFrame", "(ILcom/sc/jojo/VideoFrame;)Z");
    }

    _lastEnv = env;

    return true;
}

bool VideoFrameNativeObserver::processVideoFrameYUV422(
        JNIEnv *env,
        agora::rtc::VIDEO_SOURCE_TYPE type,
        agora::media::IVideoFrameObserver::VideoFrame &videoFrame) {
    auto videoFrameInfo = nativeToJavaVideoFrame(env, videoFrame);

    auto ret = env->CallBooleanMethod(
            _jDelegateObj,
            _jDelegateOnCaptureVideoFrameMethodID,
            (int) type,
            std::get<0>(videoFrameInfo));

    std::vector<jbyteArray> yuvJByteArrays = {
            std::get<1>(videoFrameInfo),
            std::get<2>(videoFrameInfo),
            std::get<3>(videoFrameInfo),
    };

    std::vector<uint8_t *>  yuvRawBuffers = {
            videoFrame.yBuffer,
            videoFrame.uBuffer,
            videoFrame.vBuffer,
    };

    for (int idx = 0; idx < 3; ++idx) {
        env->GetByteArrayRegion(
                yuvJByteArrays[idx],
                0,
                env->GetArrayLength(yuvJByteArrays[idx]),
                reinterpret_cast<jbyte *>(yuvRawBuffers[idx]));
        env->DeleteLocalRef(yuvJByteArrays[idx]);
    }

    env->DeleteLocalRef(std::get<0>(videoFrameInfo));

    return ret;
}

bool
VideoFrameNativeObserver::processVideoFrameTexture(JNIEnv *env, agora::rtc::VIDEO_SOURCE_TYPE type,
                                                   agora::media::IVideoFrameObserver::VideoFrame &videoFrame) {
    auto videoFrameInfo = nativeToJavaVideoFrame(env, videoFrame);
    jobject jVideoFrameObj = std::get<0>(videoFrameInfo);

    auto ret = env->CallBooleanMethod(
            _jDelegateObj,
            _jDelegateOnCaptureVideoFrameMethodID,
            (int) type,
            jVideoFrameObj);

    jfieldID fieldIdTextureId = env->GetFieldID(_jVideoFrameCls, "textureId", "I");
    int textureId = env->GetIntField(jVideoFrameObj, fieldIdTextureId);

    env->DeleteLocalRef(std::get<0>(videoFrameInfo));
    env->DeleteLocalRef(std::get<1>(videoFrameInfo));
    env->DeleteLocalRef(std::get<2>(videoFrameInfo));
    env->DeleteLocalRef(std::get<3>(videoFrameInfo));

    //TODO: Not Effect, sh*t
    videoFrame.textureId = textureId;

    return ret;
}
