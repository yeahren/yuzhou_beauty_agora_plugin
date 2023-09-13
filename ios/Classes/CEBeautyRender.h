//
//  CEBeautyRender.h
//  CosmosEffectSample
//
//  Created by cosmos783 on 2019/12/19.
//  Copyright © 2019 cosmos783_fish@sina.cn. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import <CosmosBeautyKit/CosmosBeautySDK.h>

NS_ASSUME_NONNULL_BEGIN

@interface CEBeautyRender : NSObject

- (void)addBeauty;
- (void)removeBeauty;

- (void)addLookup;
- (void)removeLookup;

- (void)addSticker;
- (void)removeSticker;

// 如果是相机，需要传入前置/后置位置, 该参数仅在相机模式下设置
@property (nonatomic, assign) AVCaptureDevicePosition devicePosition;

// 目前摄像头相对于人脸的旋转角度, 该参数仅在相机模式下设置
@property (nonatomic, assign) CBRenderModuleCameraRotate cameraRotate;

// 图像数据形式, 默认CERenderInputTypeStream。 相机或视频CERenderInputTypeStream，静态图片CERenderInputTypeStatic
@property (nonatomic, assign) CBRenderInputType inputType;

- (BOOL)setupWithLicense: (NSString *)license;

// 设置美颜参数
- (void)setBeautyFactor:(float)value forKey:(CBBeautyFilterKey)key;

- (void)setBeautyWhiteVersion:(NSInteger)version;
- (void)setBeautyreddenVersion:(NSInteger)version;

- (void)setAutoBeautyWithType:(CBBeautyAutoType)type;
- (void)setMakeupLipsType:(NSUInteger)type;
// 设置lookup素材路径
- (void)setLookupPath:(NSString *)lookupPath;
// 设置lookup滤镜浓度
- (void)setLookupIntensity:(CGFloat)intensity;
// 清除滤镜效果
- (void)clearLookup;

// 设置贴纸资源路径
- (void)setMaskModelPath:(NSString *)path;
- (void)clearSticker;

// 美妆效果
- (void)addMakeupPath:(NSString *)path;
- (void)clearMakeup;
- (void)removeMakeupLayerWithType:(CBBeautyFilterKey)type;

- (CVPixelBufferRef _Nullable)renderPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                          error:(NSError * __autoreleasing _Nullable *)error;


- (CVPixelBufferRef _Nullable)renderPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                        context:(MTIContext*)context
                                          error:(NSError * __autoreleasing _Nullable *)error;

- (MTIImage * _Nullable)renderToImageWithPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                             context:(MTIContext*)context
                                               error:(NSError * __autoreleasing _Nullable *)error;



@end

NS_ASSUME_NONNULL_END
