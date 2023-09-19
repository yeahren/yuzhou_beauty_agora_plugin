#import "YuzhouBeautyAgoraPlugin.h"
#import <AgoraRtcKit/AgoraRtcKit.h>
#import "AgoraVideoFrameObserver.h"




@implementation YuzhouBeautyAgoraPlugin
AgoraRtcEngineKit* _engine;
AgoraVideoFrameObserver *_ob;

+ (NSString *)flutterBeautyKeyToObjC:(NSString*) flutterKey {
    NSString *ret = @"";
    /**
     enum SimpleBeautyType {
       CHEEKBONE_WIDTH,      // 颧骨
       THIN_FACE,            // 瘦脸
       EYE_HEIGHT,           // 眼高
       SKIN_SMOOTH,          // 磨皮
       SKIN_WHITENING,       // 美白
       RUDDY,                // 红润
       SHARPEN,              // 锐化
       FACE_WIDTH,           // 脸宽
       JAW_SHAPE,            // 削脸
       SHORTEN_FACE,         // 短脸
       CHIN_LENGTH,          // 下巴
       FOREHEAD,             // 额头
       JAW_WIDTH,            // 下颌骨
       BIG_EYE,              // 大眼
       EYE_BRIGHT,           // 亮眼
       SKIN_SMOOTHING_EYES,  // 祛眼袋
       EYE_DISTANCE,         // 眼距
       EYE_TILT,             // 眼睛角度
       NOSE_WIDTH,           // 鼻子宽度
       NOSE_LIFT,            // 鼻高
       NOSE_SIZE,            // 鼻子大小
       NOSE_RIDGE_WIDTH,     // 鼻梁
       NOSE_TIP_SIZE,        // 鼻尖
       MOUTH_SIZE,           // 嘴唇大小
       LIP_THICKNESS,        // 嘴唇厚度
       TEETH_WHITE,          // 白牙
       NASOLABIAL_FOLDS      // 祛法令纹
     }
     */
    
    NSDictionary *mapping = @{
        @"CHEEKBONE_WIDTH": CHEEKBONE_WIDTH,
        @"THIN_FACE": THIN_FACE,
        @"EYE_HEIGHT": EYE_HEIGHT,
        @"SKIN_SMOOTH": SKIN_SMOOTH,
        @"SKIN_WHITENING": SKIN_WHITENING,
        @"RUDDY":RUDDY,
        @"SHARPEN": SHARPEN,
        @"FACE_WIDTH": FACE_WIDTH,
        @"JAW_SHAPE": JAW_SHAPE,
        @"SHORTEN_FACE": SHORTEN_FACE,
        @"CHIN_LENGTH": CHIN_LENGTH,
        @"FOREHEAD": FOREHEAD,
        @"JAW_WIDTH": JAW2_WIDTH,
        @"BIG_EYE": BIG_EYE,
        @"EYE_BRIGHT": EYEBRIGHTEN,
        @"SKIN_SMOOTHING_EYES": EYESAREA,
        @"EYE_DISTANCE": EYE_DISTANCE,
        @"EYE_TILT": EYE_TILT,
        @"NOSE_WIDTH": NOSE_WIDTH,
        @"NOSE_LIFT": NOSE_LIFT,
        @"NOSE_SIZE": NOSE_SIZE,
        @"NOSE_RIDGE_WIDTH": NOSE_RIDGE_WIDTH,
        @"NOSE_TIP_SIZE": NOSE_TIP_SIZE,
        @"MOUTH_SIZE": MOUTH_SIZE,
        @"LIP_THICKNESS": LIP_THICKNESS,
        @"TEETH_WHITE": TEETHWHITEN,
        @"NASOLABIAL_FOLDS": NASOLABIALFOLDSAREA,
    };
    
    NSString *value = [mapping valueForKey:flutterKey];
    
    if(value != nil)
        ret = value;
    
    
    return ret;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"yuzhou_beauty_agora_plugin"
                                     binaryMessenger:[registrar messenger]];
    YuzhouBeautyAgoraPlugin* instance = [[YuzhouBeautyAgoraPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
    
}

- (void)APISetupLicense: (NSString*)license {
    if(license == nil)
        return;
    
    if(![self.license isEqualToString:license])
        [self.beautyRender setupWithLicense: license];
    
    self.license = license;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"init" isEqualToString:call.method]) {
        self.beautyRender = [[CEBeautyRender alloc] init];
        self.turnOn = false;
        
        [self APISetupLicense:self.license];
        
        result([NSNumber numberWithBool:true]);
    }
    else if([@"dispose" isEqualToString:call.method]) {
        if(_ob != nil) {
            [_ob unregisterVideoFrameObserver];
            _ob = nil;
        }
        self.turnOn = false;
        
        result([NSNumber numberWithBool:true]);
    }
    else if([@"setLicense" isEqualToString:call.method]) {
        if(self.beautyRender == nil)
            result([NSNumber numberWithBool:false]);
        
        [self APISetupLicense:call.arguments];
        
        result([NSNumber numberWithBool:true]);
    }
    else if([@"turnOnBeauty" isEqualToString:call.method]) {
        if(self.beautyRender == nil)
            result([NSNumber numberWithBool:false]);
        
        if(_ob == nil) {
            if([call.arguments isKindOfClass:[NSNumber class]]) {
                NSNumber *handler = call.arguments;
                _ob = [[AgoraVideoFrameObserver alloc] initWithEngineHandle:[handler longValue] render:self.beautyRender];
            }
            
            [_ob registerVideoFrameObserver];
        }
        
        self.turnOn = YES;
        
        if(_ob != nil)
            [_ob setTurnOn:self.turnOn];
        
        result([NSNumber numberWithBool:true]);
        
    }
    else if([@"turnOffBeauty" isEqualToString:call.method]) {
        if(self.beautyRender == nil)
            result([NSNumber numberWithBool:false]);
        
        self.turnOn = NO;

        if(_ob != nil)
            [_ob setTurnOn:self.turnOn];
        
        result([NSNumber numberWithBool:true]);
    }
    else if([@"setSimpleBeautyValue" isEqualToString:call.method]) {
        if(self.beautyRender == nil || !self.turnOn)
            result([NSNumber numberWithBool:false]);
        
        NSString *key = call.arguments[@"type"];
        NSString *mappedKey = [YuzhouBeautyAgoraPlugin flutterBeautyKeyToObjC:key];
        NSNumber *value = call.arguments[@"value"];
        
        [self.beautyRender setBeautyFactor:[value floatValue]
                                    forKey:mappedKey];
        
        result([NSNumber numberWithBool:true]);
    }
    else if([@"setMakeup" isEqualToString:call.method]) {
        if(self.beautyRender == nil || !self.turnOn)
            result([NSNumber numberWithBool:false]);
        
        NSString *path = call.arguments[@"path"];
        NSNumber *style_value = call.arguments[@"style_value"];
        NSNumber *lut_value = call.arguments[@"lut_value"];
        
        NSArray *params = [path componentsSeparatedByString:@".bundle/"];
        
        if([params count] >= 2) {
            NSString *path = [[NSBundle mainBundle] pathForResource:params[0] ofType:@"bundle"];
            NSString *fileName = [path stringByAppendingPathComponent:params[1]];
            
            [self.beautyRender clearMakeup];
            [self.beautyRender addMakeupPath:fileName];
            [self.beautyRender setBeautyFactor:[style_value floatValue] forKey:MAKEUP_ALL];
            [self.beautyRender setBeautyFactor:[style_value floatValue] forKey:MAKEUP_LUT];
            
            result([NSNumber numberWithBool:true]);
        }
        else {
            result([NSNumber numberWithBool:false]);
        }
    }
    else if([@"setSticker" isEqualToString:call.method]) {
        if(self.beautyRender == nil || !self.turnOn)
            result([NSNumber numberWithBool:false]);
        
        NSString *path = call.arguments[@"path"];
        
        NSArray *params = [path componentsSeparatedByString:@".bundle/"];
        
        if([params count] >= 2) {
            NSString *path = [[NSBundle mainBundle] pathForResource:params[0] ofType:@"bundle"];
            NSString *fileName = [path stringByAppendingPathComponent:params[1]];
            
            [self.beautyRender clearSticker];
            [self.beautyRender setMaskModelPath:fileName];
            
            result([NSNumber numberWithBool:true]);
        }
        else {
            result([NSNumber numberWithBool:false]);
        }
    }
    else if([@"clearMakeup" isEqualToString:call.method]) {
        if(self.beautyRender == nil || !self.turnOn)
            result([NSNumber numberWithBool:false]);
        
        [self.beautyRender clearMakeup];
        
        result([NSNumber numberWithBool:true]);
        
    }
    else if([@"clearSticker" isEqualToString:call.method]) {
        if(self.beautyRender == nil || !self.turnOn)
            result([NSNumber numberWithBool:false]);
        
        [self.beautyRender clearSticker];
        
        result([NSNumber numberWithBool:true]);
        
    }
    else {
        result(FlutterMethodNotImplemented);
    }
}

@end
