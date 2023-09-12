package com.sc.jojo.beauty

import com.cosmos.beauty.filter.BeautyType
import com.cosmos.beauty.module.beauty.AutoBeautyType
import com.cosmos.beauty.module.beauty.BeautyBodyType
import com.cosmos.beauty.module.beauty.MakeupType
import com.cosmos.beauty.module.beauty.SimpleBeautyType
import com.cosmos.beauty.module.makeup.IMakeupBeautyModule
import com.cosmos.config_type.type.*

val OneKeyBeautyTypeMap = mapOf(
    Pair(one_key_nature, AutoBeautyType.AUTOBEAUTY_NATURAL),
    Pair(one_key_cute, AutoBeautyType.AUTOBEAUTY_CUTE),
    Pair(one_key_girl_god, AutoBeautyType.AUTOBEAUTY_GODDESS),
    Pair(one_key_pure_white, AutoBeautyType.AUTOBEAUTY_PUREWHITE)
)

val BeautyTypeMap = mapOf(
    Pair(beauty_skin_smooth, SimpleBeautyType.SKIN_SMOOTH),
    Pair(beauty_white, SimpleBeautyType.SKIN_WHITENING),
    Pair(beauty_ruddy, SimpleBeautyType.RUDDY),
    Pair(beauty_big_eye, SimpleBeautyType.BIG_EYE),
    Pair(beauty_face_thin, SimpleBeautyType.THIN_FACE),
    Pair(beauty_sharpen, SimpleBeautyType.SHARPEN),

    Pair(micro_face_width, SimpleBeautyType.FACE_WIDTH),
    Pair(micro_face_cut, SimpleBeautyType.JAW_SHAPE),
    Pair(micro_face_short, SimpleBeautyType.SHORTEN_FACE),
    Pair(micro_fore_head, SimpleBeautyType.FOREHEAD),
    Pair(micro_chin, SimpleBeautyType.CHIN_LENGTH),
    Pair(micro_cheek_bones, SimpleBeautyType.CHEEKBONE_WIDTH),
    Pair(micro_jaw, SimpleBeautyType.JAW_WIDTH),
    Pair(micro_nose_width, SimpleBeautyType.NOSE_WIDTH),
    Pair(micro_nose_height, SimpleBeautyType.NOSE_LIFT),
    Pair(micro_nose_size, SimpleBeautyType.NOSE_SIZE),
    Pair(micro_nose_bridge, SimpleBeautyType.NOSE_RIDGE_WIDTH),
    Pair(micro_nose_tip, SimpleBeautyType.NOSE_TIP_SIZE),
    Pair(micro_eye_angle, SimpleBeautyType.EYE_TILT),
    Pair(micro_eye_distance, SimpleBeautyType.EYE_DISTANCE),
    Pair(micro_eye_height, SimpleBeautyType.EYE_HEIGHT),
    Pair(micro_lip_thickness, SimpleBeautyType.LIP_THICKNESS),
    Pair(micro_mouth_size, SimpleBeautyType.MOUTH_SIZE),
    Pair(micro_nasolabial, SimpleBeautyType.NASOLABIAL_FOLDS),
    Pair(micro_eye_bag, SimpleBeautyType.SKIN_SMOOTHING_EYES),
    Pair(micro_eye_light, SimpleBeautyType.EYE_BRIGHT),
    Pair(micro_teeth_white, SimpleBeautyType.TEETH_WHITE)
)

val BeautyTypeReverseMap = mapOf(
    Pair(SimpleBeautyType.SKIN_SMOOTH, beauty_skin_smooth),
    Pair(SimpleBeautyType.SKIN_WHITENING, beauty_white),
    Pair(SimpleBeautyType.RUDDY, beauty_ruddy),
    Pair(SimpleBeautyType.BIG_EYE, beauty_big_eye),
    Pair(SimpleBeautyType.THIN_FACE, beauty_face_thin),

    Pair(SimpleBeautyType.FACE_WIDTH, micro_face_width),
    Pair(SimpleBeautyType.JAW_SHAPE, micro_face_cut),
    Pair(SimpleBeautyType.SHORTEN_FACE, micro_face_short),
    Pair(SimpleBeautyType.FOREHEAD, micro_fore_head),
    Pair(SimpleBeautyType.CHIN_LENGTH, micro_chin),
    Pair(SimpleBeautyType.CHEEKBONE_WIDTH, micro_cheek_bones),
    Pair(SimpleBeautyType.JAW_WIDTH, micro_jaw),
    Pair(SimpleBeautyType.NOSE_WIDTH, micro_nose_width),
    Pair(SimpleBeautyType.NOSE_LIFT, micro_nose_height),
    Pair(SimpleBeautyType.NOSE_SIZE, micro_nose_size),
    Pair(SimpleBeautyType.NOSE_RIDGE_WIDTH, micro_nose_bridge),
    Pair(SimpleBeautyType.NOSE_TIP_SIZE, micro_nose_tip),
    Pair(SimpleBeautyType.EYE_TILT, micro_eye_angle),
    Pair(SimpleBeautyType.EYE_DISTANCE, micro_eye_distance),
    Pair(SimpleBeautyType.EYE_HEIGHT, micro_eye_height),
    Pair(SimpleBeautyType.LIP_THICKNESS, micro_lip_thickness),
    Pair(SimpleBeautyType.MOUTH_SIZE, micro_mouth_size),
    Pair(SimpleBeautyType.NASOLABIAL_FOLDS, micro_nasolabial),
    Pair(SimpleBeautyType.SKIN_SMOOTHING_EYES, micro_eye_bag),
    Pair(SimpleBeautyType.EYE_BRIGHT, micro_eye_light),
    Pair(SimpleBeautyType.TEETH_WHITE, micro_teeth_white)
)

val BeautyInnerTypeMap = mapOf(
    Pair(beauty_white_nature, BeautyType.WHITETYPE.WHITE_T1),
    Pair(beauty_ruddy_nature, BeautyType.RUDDYTYPE.RUDDY_T1)
)

val MakeupTypeMap = mapOf(
    Pair(makeup_lip, MakeupType.MAKEUP_LIP),
    Pair(makeup_blush, MakeupType.MAKEUP_BLUSH),
    Pair(makeup_pupil, MakeupType.MAKEUP_PUPIL),
    Pair(makeup_eye_shadow, MakeupType.MAKEUP_EYESHADOW),
    Pair(makeup_brow, MakeupType.MAKEUP_EYEBOW),
    Pair(makeup_facial, MakeupType.MAKEUP_FACIAL)
)

val MakeupLipTextureMap = mapOf(
    1 to IMakeupBeautyModule.LIP_TEXTURE_TYPE_NATURAL,
    2 to IMakeupBeautyModule.LIP_TEXTURE_TYPE_FROG,
    3 to IMakeupBeautyModule.LIP_TEXTURE_TYPE_MIRROR,
    4 to IMakeupBeautyModule.LIP_TEXTURE_TYPE_FLASH
)

val MakeupStyleMakeupTypeMap = mapOf(
    Pair(makeup_style_default, MakeupType.MAKEUP_STYLE)
)
val MakeupStyleLookupTypeMap = mapOf(
    Pair(makeup_style_default, MakeupType.MAKEUP_LUT)
)
val BeautyBodyStyleLookupTypeMap = mapOf(
    Pair(bodyType_slimming, BeautyBodyType.SLIMMING),
    Pair(bodyType_leglong, BeautyBodyType.LONG_LEG)
)
