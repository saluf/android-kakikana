# Kakikana 書き仮名
As a Japanese learner I **learned Furigana** (Japanese alphabets consist of Hiragana and Katakana) by typing and recognizing with assistance of many great apps. However, when it comes to writing, it is still challenging. As a result, this work is to help users and myself **learn by writing** with on-device TensorFlow Lite convolutional neural network (CNN) techniques. 

Note: This work is also my capstone project of the Udacity Android Developer Nanodegree. It has been reviewed by experienced engineer(s) and passed the rubrics.

<a href='https://play.google.com/store/apps/details?id=com.salab.project.kakikana'><img alt='Get it on Google Play' width="150" src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>

## Technical Features

- **On-device ML**: With Tensorflow Lite, transfer pre-trained model to Android device
- **Single-Activity flow**: Apply with Jetpack Navigation component to construct UI flows with Fragments.
- **MVVM Structure**: Follow recommended structure to organize code in UI / ViewModel(+LiveData) / Repository layers.
- **Firebase Services**: Authenticate users through Google and preserve data on Firebase Realtime Database

## Setup Guide

```
$ git clone https://github.com/saluf/android-kakikana.git
```

### Firebase Realtime Database & Authentication
The data persistence relies on Firebase Realtime Database and Firebase Authentication which authenticates users with Google accounts (data preservation across devices), and allows users to sign in anonymously.

1. [Setup your Firebase console](https://firebase.google.com/docs/android/setup#console)
2. Enable **Realtime Database** and **Authentication (Anonymous & Google)**
3. Put **server's Client ID** into string resources

```xml
<resources>
    <string name="firebase_client_id">YOUR CLIENT ID</string>
</resources>
```

### TensorFlow Lite
The work requires pre-trained converted TensorFlow models. The models training scripts are described [here](https://github.com/saluf/ml-furigana-handwriting-recognition).

1. Train or get two models, one for Hiragana, the other for Katakana
2. Put the models in **Assets** folder. Labels are already included in the repo
3. Setup reference from code to your model names

```java
// src/main/java/com/salab/project/kakikana/classifier/ClassifierHandler.java
private static Map<String, Object> getClassifierOptions(boolean isHiragana) {
        //...
        if (isHiragana) {
            options.put(KEY_CLASSIFIER_MODEL_FILE_NAME, MODEL_FILE_NAME_HERE); // model file name
            options.put(KEY_CLASSIFIER_LABEL_RESOURCE_ID, R.array.tf_labels_hiragana); // labels location
            options.put(KEY_CLASSIFIER_OUTPUT_CLASSES_COUNT, 46); // total classification class count
        } 
       // ...
    }
```

## Screenshots
<img src="/screenshots/login.png" width="150" height="308"/>  <img src="/screenshots/quiz.png" width="150" height="308"/>   <img src="/screenshots/quiz_result.png" width="150" height="308"/>    <img src="/screenshots/kana_list.png" width="150" height="308"/>

<img src="/screenshots/kana_detail.png" width="150" height="308"/>    <img src="/screenshots/scoreboard.png" width="150" height="308"/> <img src="/screenshots/profile.png" width="150" height="308"/>    <img src="/screenshots/widget.png" width="150" height="308"/>

## Licence
The work is under Apache License Version 2.0. Please check the License file for details.

