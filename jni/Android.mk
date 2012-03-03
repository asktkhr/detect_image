LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

include $(OPENCV_MK_PATH)

LOCAL_MODULE    := native_sample
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
