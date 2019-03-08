# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Loren\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##---------------Begin: proguard configuration for Firebase  ----------
## Firebase rules: https://firebase.google.com/docs/database/android/start/
## Add this global rule
-keepattributes Signature
#
## This rule will properly ProGuard all the model classes in
## the package com.yourcompany.models. Modify to fit the structure
## of your app.
-keepclassmembers class com.lbconsulting.divelogfirebase.models.** {
  *;
}
##---------------End: proguard configuration for Firebase  ----------


##---------------Begin: proguard configuration for greenrobot.eventbus  ----------
# http://greenrobot.org/eventbus/documentation/proguard/
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}
##---------------End: proguard configuration for greenrobot.eventbus  ----------


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
#-keepattributes Signature <-- already stated above

# For using GSON @Expose annotation
# -keepattributes *Annotation* <-- already stated above

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }<-- already stated above

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
##---------------End: proguard configuration for Gson  ----------


##---------------Start: proguard configuration for picasso  ----------
-dontwarn com.squareup.okhttp.**
##---------------End: proguard configuration for picasso  ----------


##---------------Start: proguard configuration for jsoup  ----------
-keeppackagenames org.jsoup.nodes
##---------------End: proguard configuration for jsoup  ----------


##---------------Start: proguard configuration miscelanous  ----------
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn retrofit2.**
##---------------End: proguard configuration miscelanous  ----------

