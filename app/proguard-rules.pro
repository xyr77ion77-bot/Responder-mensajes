-keep class com.replyflow.app.service.ReplyNotificationListener { *; }
-keepclassmembers class * extends android.app.NotificationListenerService { *; }
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
