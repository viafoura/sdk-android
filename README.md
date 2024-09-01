<p align="center">
<img src="https://github.com/viafoura/sdk-ios/assets/103942744/f4b6b449-c64b-452c-8260-8e1c1795266f" alt="Viafoura" title="Viafoura" width="557"/>
</p>

<p align="center">
<a href="https://central.sonatype.com/artifact/com.viafoura/android"><img src="https://maven-badges.herokuapp.com/maven-central/com.viafoura/android/badge.svg"></a>
</p>

# Viafoura Android SDK

This library allows you to integrate Viafoura tools into a native Android app.

### Add the SDK to your project
#### Using Gradle
Add the following line to your app level build.gradle
```
implementation 'com.viafoura:android:1.1.4'
```

### See our documentation

Please follow [the official Viafoura Android SDK documentation](https://documentation.viafoura.com/docs/add-the-viafoura-android-sdk-to-your-app).


## How to initialize the SDK?
1) Create an `Application` class for your app.
2) Initialize the SDK in the `onCreate()`. Make sure to replace SITE_UUID and SITE_DOMAIN with the values corresponding to your domain.

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ViafouraSDK.initialize(getApplicationContext(), "SITE_UUID", "SITE_DOMAIN");
    }
}
```

