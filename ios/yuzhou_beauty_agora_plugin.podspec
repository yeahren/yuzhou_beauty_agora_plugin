#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint yuzhou_beauty_agora_plugin.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'yuzhou_beauty_agora_plugin'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter plugin project.'
  s.description      = <<-DESC
A new Flutter plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'AgoraRtcEngine_iOS'
  s.platform = :ios, '11.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.pod_target_xcconfig = {'OTHER_LDFLAGS' => ['-ObjC']}

  s.vendored_frameworks = [
    'Frameworks/Libraries/protobufc.framework',
    'Frameworks/Libraries/opencv2.framework',
    'Frameworks/Libraries/ceres.framework',
    'Frameworks/Libraries/MetalPetal.framework',
    'Frameworks/Libraries/CosmosBeautyKit.framework',
    'Frameworks/Libraries/CosmosCV.framework',
    'Frameworks/Libraries/Mantle.framework',
    'Frameworks/Libraries/MNN.framework',
    'Frameworks/Libraries/XEngine.framework',
  ]
  s.libraries = 'c++', 'z', 'iconv'
  s.weak_frameworks = [
  ]
  s.framework = [
    'Accelerate',
    'AssetsLibrary',
    'CoreImage',
    'CoreML',
    'CoreMotion',
    'Foundation',
    'OpenAL',
  ]
  s.resources = [
    'Frameworks/Resources/CosmosBeautyKit.bundle',
    'Frameworks/Resources/MetalPetal.bundle',
    'Frameworks/Resources/XEngine.bundle',
    'Frameworks/Resources/XEngineResource.bundle',
    'Frameworks/Resources/cv.bundle',
    'Frameworks/Resources/Lookup.bundle',
    'Frameworks/Resources/mnn.metallib',
  ]

end
