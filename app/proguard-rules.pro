-keep class com.replyflow.app.service.ReplyNotificationListener { *; }
-keepclassmembers class * extends android.app.NotificationListenerService { *; }
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
