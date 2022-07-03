#include "SpaceCadetPinballJNI.h"
#include "../../../../SpaceCadetPinball/winmain.h"
#include "../../../../SpaceCadetPinball/Sound.h"
#include "../../../../SpaceCadetPinball/pinball.h"
#include "../../../../SpaceCadetPinball/control.h"
#include <jni.h>
#include <android/log.h>

static JavaVM* g_JavaVM = nullptr;
static jclass clazz = nullptr;
static JNIEnv *env = nullptr;

void SpaceCadetPinballJNI::show_error_dialog(std::string title, std::string message) {
    __android_log_print(ANDROID_LOG_ERROR, "SpaceCadetPinballJNI", "Error: %s, %s", title.c_str(), message.c_str());
}

void SpaceCadetPinballJNI::notifyGameState(int state) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "setState", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, state);
}

void SpaceCadetPinballJNI::setBallInPlunger(bool isInPlunger) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "setBallInPlunger", "(Z)V");

    env->CallStaticVoidMethod(clazz, mid, isInPlunger);
}

void SpaceCadetPinballJNI::addHighScore(int score) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "addHighScore", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, score);
}

int SpaceCadetPinballJNI::getHighScore() {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "getHighScore", "()I");

    return env->CallStaticIntMethod(clazz, mid);
}

void SpaceCadetPinballJNI::displayText(const char* text, int type) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "printString", "(Ljava/lang/String;I)V");

    jstring str = env->NewStringUTF(text);

    env->CallStaticVoidMethod(clazz, mid, str, type);
}

void SpaceCadetPinballJNI::clearText(int type) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "clearText", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, type);
}

void SpaceCadetPinballJNI::postScore(int score) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "postScore", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, score);
}

void SpaceCadetPinballJNI::postBallCount(int count) {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "postBallCount", "(I)V");

    env->CallStaticVoidMethod(clazz, mid, count);
}

void SpaceCadetPinballJNI::cheatsUsed() {
    if (env == nullptr) g_JavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (clazz == nullptr) clazz = env->FindClass("com/fexed/spacecadetpinball/JNIEntryPoint");
    jmethodID mid = env->GetStaticMethodID(clazz, "cheatsUsed", "()V");

    env->CallStaticVoidMethod(clazz, mid);
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

extern "C"
JNIEXPORT void JNICALL
Java_com_fexed_spacecadetpinball_MainActivity_putString(JNIEnv *env, jobject thiz, jint id, jstring str) {
    LPCSTR mstr = (*env).GetStringUTFChars(str, nullptr);
    pinball::set_rc_string(id, mstr);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_fexed_spacecadetpinball_MainActivity_checkCheatsUsed(JNIEnv *env, jobject thiz) {
    return control::check_cheats();
}