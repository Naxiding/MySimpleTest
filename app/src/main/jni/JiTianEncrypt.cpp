//
// Created by YangDing on 2020/4/21.
//

#include "JiTianEncrypt.h"
#include <jni.h>
#include "com_jitian_jni_JiTianEncrypt.h"
#include <string>
#include <cstdlib>
#include <ctime>
#include <android/log.h>
#include <math.h>

#define TAG "LogUtil-jni" // 这个是自定义的LOG的标识
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOG
static volatile uint8_t NUM_MAX_LENGTH = 9;
static volatile uint8_t NUM_MIN_LENGTH = 6;
static volatile uint8_t FUN_TYPE_1 = 0;
static volatile uint8_t FUN_TYPE_2 = 1;
static volatile uint8_t FUN_TYPE_NUM = 2;
static volatile uint8_t FUN_REMAINDER = 99;
static volatile uint8_t DATA_LENGTH = 7;

#ifdef __cplusplus
extern "C" {
#endif

uint8_t getRandomNumLength();
uint8_t getRandomFunType();
void encryptData(uint8_t numLength, uint8_t funType, uint8_t outData[]);
uint8_t getCertifiedData(uint8_t allData[]);
uint8_t countFun1(uint8_t realData[]);
uint8_t countFun2(uint8_t realData[]);

/*
 * Class:     com_jitian_jni_JiTianEncrypt
 * Method:    getEncryptData
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL
Java_com_jitian_jni_JiTianEncrypt_getEncryptData(JNIEnv *env, jobject thiz) {
    uint8_t numLength = NUM_MIN_LENGTH;
    uint8_t funType = getRandomFunType();
    uint8_t *outData = new uint8_t[DATA_LENGTH]();
    LOG("numLength:%d,funType:%d", numLength, funType);
    encryptData(numLength, funType, outData);
    jbyteArray resultArray = env->NewByteArray(DATA_LENGTH);
    jbyte *outArray = (jbyte *) outData;
    env->SetByteArrayRegion(resultArray, 0, DATA_LENGTH, outArray);
    delete[]outData;
    return resultArray;
}

/*
 * Class:     com_jitian_jni_JiTianEncrypt
 * Method:    getCertifiedData
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL
Java_com_jitian_jni_JiTianEncrypt_getCertifiedData(JNIEnv *env, jobject thiz,
                                                   jbyteArray encrypt_data) {
    jbyte *jInData = env->GetByteArrayElements(encrypt_data, nullptr);
    if (jInData == nullptr) {
        return 0;
    }
    uint8_t *inData = (uint8_t *) jInData;
    uint8_t result = getCertifiedData(inData);
    LOG("result:%d", result);
    env->ReleaseByteArrayElements(encrypt_data, jInData, 0);
    return result;
}

uint8_t getRandomNumLength() {
    uint8_t seed = (uint8_t) time(0);
    srand(seed);
    uint8_t numberLength = (uint8_t) (rand() % (NUM_MAX_LENGTH - NUM_MIN_LENGTH + 1) +
                                      NUM_MIN_LENGTH);
    return numberLength;
}

uint8_t getRandomFunType() {
    uint8_t seed = (uint8_t) time(0);
    srand(seed);
    return uint8_t(rand() % FUN_REMAINDER);
}

void encryptData(uint8_t numLength, uint8_t funType, uint8_t outData[]) {
    outData[0] = funType;
    int position = 1;
    int powNum = (int) pow(10, numLength - 1);
    uint8_t seed = (uint8_t) time(0);
    srand(seed);
    int data = rand() % (9 * powNum) + powNum;
    LOG("data:%d", data);
    while (position < numLength + 1) {
        outData[position] = (uint8_t) (data % 10);
        data /= 10;
        position++;
    }
}

uint8_t getCertifiedData(uint8_t allData[]) {
    uint8_t funType = allData[0] % FUN_TYPE_NUM;
    LOG("funType:%d", funType);
    if (funType == FUN_TYPE_1) {
        return countFun1(allData);
    } else if (funType == FUN_TYPE_2) {
        return countFun2(allData);
    }
    return 0;
}

uint8_t countFun1(uint8_t allData[]) {
    int position = 1;
    int sum = 0;
    while (position < DATA_LENGTH) {
        sum += allData[position];
        position++;
    }
    LOG("sum:%d", sum);
    return (uint8_t) sum % FUN_REMAINDER;
}

uint8_t countFun2(uint8_t allData[]) {
    int position = 1;
    int sum = 1;
    while (position < DATA_LENGTH) {
        if (allData[position] != 0) {
            sum *= allData[position];
        }
        position++;
    }
    LOG("sum:%d", sum);
    return (uint8_t) sum % FUN_REMAINDER;
}

#ifdef __cplusplus
}
#endif