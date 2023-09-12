//
//  CEBeautyRender.m
//  CosmosEffectSample
//
//  Created by cosmos783 on 2019/12/19.
//  Copyright © 2019 cosmos783_fish@sina.cn. All rights reserved.
//

#import "CEBeautyRender.h"
#import <CosmosBeautyKit/CosmosBeautySDK.h>

@interface CEBeautyRender () <CBRenderModuleManagerDelegate>

@property (nonatomic, strong) CBRenderModuleManager *render;
@property (nonatomic, strong) CBRenderFilterBeautyMakeupModule *beautyDescriptor;

@property (nonatomic, strong) CBRenderFilterLookupModule *lookupDescriptor;

@property (nonatomic, strong) CBRenderFilterStickerModule *stickerDescriptor;

@end

@implementation CEBeautyRender

- (BOOL)setupWithLicense: (NSString *)license {
    CBRenderError ret = [CosmosBeautySDK initSDKWith:license];
    
    [CosmosBeautySDK setupCVModelPath:[NSBundle.mainBundle URLForResource:@"cv" withExtension:@"bundle"].path];
    
    return ret == CBRenderErrorNone;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        CBRenderModuleManager *render = [[CBRenderModuleManager alloc] init];
        render.devicePosition = AVCaptureDevicePositionFront;
        render.inputType = CBRenderInputTypeStream;
        render.delegate = self;
        self.render = render;
        
        _beautyDescriptor = [[CBRenderFilterBeautyMakeupModule alloc] init];
        [render registerModule:_beautyDescriptor];

        _lookupDescriptor = [[CBRenderFilterLookupModule alloc] init];
        [render registerModule:_lookupDescriptor];
        
        _stickerDescriptor = [[CBRenderFilterStickerModule alloc] init];
        [render registerModule:_stickerDescriptor];
        
        //[self setInputType:CBRenderInputTypeStatic]
        
//        NSString *rootPath = [NSBundle.mainBundle pathForResource:@"Lookup" ofType:@"bundle"];
//        [self setLookupPath:[rootPath stringByAppendingPathComponent:@"lookup_ziran"]];
//        [self setLookupIntensity:0.4];
    }
    return self;
}

- (void)addBeauty {
    _beautyDescriptor = [[CBRenderFilterBeautyMakeupModule alloc] init];
    [_render registerModule:_beautyDescriptor];
}

- (void)removeBeauty {
    [_render unregisterModule:_beautyDescriptor];
    _beautyDescriptor = nil;
}

- (void)addLookup {
    _lookupDescriptor = [[CBRenderFilterLookupModule alloc] init];
    [_render registerModule:_lookupDescriptor];
}

- (void)removeLookup {
    [_render unregisterModule:_lookupDescriptor];
    _lookupDescriptor = nil;
}
- (void)setMakeupLipsType:(NSUInteger)type{
    [_beautyDescriptor setMakeupLipsType:type];
}

- (void)addSticker {
    _stickerDescriptor = [[CBRenderFilterStickerModule alloc] init];
    [_render registerModule:_stickerDescriptor];
}

- (void)removeSticker {
    [_render unregisterModule:_stickerDescriptor];
    _stickerDescriptor = nil;
}

- (CVPixelBufferRef _Nullable)renderPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                          error:(NSError * __autoreleasing _Nullable *)error {
    return [self.render renderFrame:pixelBuffer error:error];
}


- (CVPixelBufferRef _Nullable)renderPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                        context:(MTIContext*)context
                                          error:(NSError * __autoreleasing _Nullable *)error {
    CVPixelBufferRef renderedPixelBuffer = NULL;
    renderedPixelBuffer =  [self.render renderFrame:pixelBuffer context:context error:error];
    return renderedPixelBuffer;
}


- (MTIImage *)renderToImageWithPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                   context:(nonnull MTIContext *)context
                                     error:(NSError *__autoreleasing  _Nullable * _Nullable)error
{
    return [self.render renderFrameToImage:pixelBuffer context:context error:error];
}

- (void)setInputType:(CBRenderInputType)inputType {
    self.render.inputType = inputType;
}

- (CBRenderInputType)inputType {
    return self.render.inputType;
}

- (void)setCameraRotate:(CBRenderModuleCameraRotate)cameraRotate {
    self.render.cameraRotate = cameraRotate;
}

- (CBRenderModuleCameraRotate)cameraRotate {
    return self.render.cameraRotate;
}

- (void)setDevicePosition:(AVCaptureDevicePosition)devicePosition {
    self.render.devicePosition = devicePosition;
}

- (AVCaptureDevicePosition)devicePosition {
    return self.render.devicePosition;
}

- (void)setBeautyFactor:(float)value forKey:(CBBeautyFilterKey)key {
    [self.beautyDescriptor setBeautyFactor:value forKey:key];
    
}
- (void)setAutoBeautyWithType:(CBBeautyAutoType)type{
    [self.beautyDescriptor adjustAutoBeautyWithType:type];
}

- (void)setBeautyWhiteVersion:(NSInteger)version{
    [self.beautyDescriptor setBeautyWhiteVersion:(CBBeautyWhittenFilterVersion)version];
}
- (void)setBeautyreddenVersion:(NSInteger)version{
    [self.beautyDescriptor setBeautyRaddenVersion:(CBBeautyReddenFilterVersion)version];
}

- (void)setLookupPath:(NSString *)lookupPath {
    [self.lookupDescriptor setLookupResourcePath:lookupPath];
    [self.lookupDescriptor setIntensity:1.0];
}

- (void)setLookupIntensity:(CGFloat)intensity {
    [self.lookupDescriptor setIntensity:intensity];
}

- (void)clearLookup {
    [self.lookupDescriptor clear];
}

- (void)setMaskModelPath:(NSString *)path {
    [self.stickerDescriptor setMaskModelPath:path];
}

- (void)clearSticker {
    [self.stickerDescriptor clear];
}
// 美妆效果
- (void)clearMakeup {
    [self.beautyDescriptor clearMakeup];
}

- (void)addMakeupPath:(NSString *)path {
    [self.beautyDescriptor addMakeupWithResourceURL:path];
}

- (void)removeMakeupLayerWithType:(CBBeautyFilterKey)type {
    [self.beautyDescriptor removeMakeupLayerWithType:type];
}

- (void)renderModuleManager:(CBRenderModuleManager *)manager faceFeatureCount:(NSInteger)faceFeatures{
    NSLog(@"face count %d",faceFeatures);
}

@end

