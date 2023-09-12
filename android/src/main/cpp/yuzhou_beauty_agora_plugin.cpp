#include <jni.h>

#include "AgoraMediaBase.h"
#include "IAgoraMediaEngine.h"
#include "IAgoraRtcEngine.h"
// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("agora_rtc_yuzhou_beauty");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("agora_rtc_yuzhou_beauty")
//      }
//    }

#include "AgoraMediaBase.h"
#include "IAgoraMediaEngine.h"
#include "IAgoraRtcEngine.h"
#include "VideoFrameNativeObserver.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_sc_jojo_NativeEngineHandler_nativeRegisterVideoFrameObserver(
        JNIEnv *env, jobject thiz, jlong native_handler_ptr) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fidNativeObserverPtr = env->GetFieldID(cls, "nativeObserverPtr", "J");
    jlong nativeObserverPtr = env->GetLongField(thiz, fidNativeObserverPtr);

    auto rtcEngine = reinterpret_cast<agora::rtc::IRtcEngine *>(native_handler_ptr);
    if (rtcEngine) {
        agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;
        mediaEngine.queryInterface(rtcEngine, agora::rtc::AGORA_IID_MEDIA_ENGINE);
        if (mediaEngine) {
            auto newNativeObserverPtr = new VideoFrameNativeObserver(env, thiz);
            mediaEngine->registerVideoFrameObserver(newNativeObserverPtr);
            env->SetLongField(thiz, fidNativeObserverPtr, (jlong)newNativeObserverPtr);
        }
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_sc_jojo_NativeEngineHandler_nativeUnregisterVideoFrameObserver(
        JNIEnv *env, jobject thiz, jlong native_handler_ptr) {
    if(native_handler_ptr == 0)
        return;

    jclass cls = env->GetObjectClass(thiz);
    jfieldID fidNativeObserverPtr = env->GetFieldID(cls, "nativeObserverPtr", "J");
    jlong nativeObserverPtr = env->GetLongField(thiz, fidNativeObserverPtr);
    env->SetLongField(thiz, fidNativeObserverPtr, (jlong)0L);

    if(nativeObserverPtr != 0) {
        auto rtcEngine = reinterpret_cast<agora::rtc::IRtcEngine *>(native_handler_ptr);

        agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;
        mediaEngine.queryInterface(rtcEngine, agora::rtc::AGORA_IID_MEDIA_ENGINE);
        if (mediaEngine) {
            mediaEngine->registerVideoFrameObserver(nullptr);
            delete (VideoFrameNativeObserver *)nativeObserverPtr;
        }
    }


}