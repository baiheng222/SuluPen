LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
         $(call all-subdir-java-files) \
         com/hanvon/sulupen/pinyin/IPinyinDecoderService.aidl

LOCAL_MODULE := com.hanvon.sulupen.lib
STLPORT_FORCE_REBUILD := true

include $(BUILD_STATIC_JAVA_LIBRARY)
