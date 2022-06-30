#include "SpaceCadetPinballJNI.h"
#include "../../../../SpaceCadetPinball/winmain.h"
#include "../../../../SpaceCadetPinball/Sound.h"
#include <jni.h>
#include <android/log.h>

static JavaVM* g_JavaVM = NULL;

void SpaceCadetPinballJNI::show_error_dialog(std::string title, std::string message) {
    __android_log_print(ANDROID_LOG_ERROR, "SpaceCadetPinballJNI", "Error: %s, %s", title.c_str(), message.c_str());
}

void SpaceCadetPinballJNI::notifyGameState(int state) {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "setState", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, state);
}

void SpaceCadetPinballJNI::setBallInPlunger(bool isInPlunger) {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "setBallInPlunger", "(Z)V");

    env->CallStaticVoidMethod(clazz, mid, isInPlunger);
}

void SpaceCadetPinballJNI::addHighScore(int score) {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "addHighScore", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, score);
}

int SpaceCadetPinballJNI::getHighScore() {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "getHighScore", "()I");

    return env->CallStaticIntMethod(clazz, mid);
}

void SpaceCadetPinballJNI::displayText(const char* text, int type) {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);


    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "printString", "(Ljava/lang/String;I)V");

    jstring str = env->NewStringUTF(text);

    env->CallStaticVoidMethod(clazz, mid, str, type);
}

void SpaceCadetPinballJNI::clearText(int type) {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);


    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "clearText", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, type);
}

void SpaceCadetPinballJNI::postScore(int score) {
    // issue #18
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);


    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "postScore", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, score);
}

void SpaceCadetPinballJNI::postBallCount(int count) {
    JNIEnv *env;
    g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);


    jclass clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "postBallCount", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, count);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fexed_spacecadetpinball_MainActivity_initNative(JNIEnv *env, jobject thiz,
        jstring data_path) {
winmain::BasePath = (char *) env->GetStringUTFChars(data_path, nullptr);
    env->GetJavaVM(&g_JavaVM);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_fexed_spacecadetpinball_MainActivity_setVolume(JNIEnv *env, jobject thiz, jint vol) {
    Sound::SetVolume(vol);
}