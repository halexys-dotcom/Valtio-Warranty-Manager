# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext$ScheduledPost {
    public <init>(...);
}

# Jetpack Compose
-keepclassmembers class androidx.compose.ui.platform.AndroidComposeView {
    *** getCoroutineContext();
}

# Room Database
-keep class com.valtio.app.data.local.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# iText PDF
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# SLF4J (iText dependency)
-dontwarn org.slf4j.**

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepattributes *Annotation*
-keepattributes Signature

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**

# General optimizations
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
