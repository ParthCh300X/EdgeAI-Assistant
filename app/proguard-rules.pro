# TFLite — model inference
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.gpu.** { *; }
-dontwarn org.tensorflow.lite.**

# Room — database
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# Hilt / Dagger — dependency injection
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

# Your ML classes — never rename these
-keep class parth.appdev.edgeaiassistant.engine.ml.** { *; }
-keep class parth.appdev.edgeaiassistant.data.local.entity.** { *; }
-keep class parth.appdev.edgeaiassistant.data.local.model.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Keep annotations and signatures (needed by Room and Hilt)
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**