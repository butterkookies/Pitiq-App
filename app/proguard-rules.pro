# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.pitiq.app.**$$serializer { *; }
-keepclassmembers class com.pitiq.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.pitiq.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Supabase
-keep class io.github.jan.supabase.** { *; }

# ZXing
-keep class com.google.zxing.** { *; }

# Hilt / Dagger
-dontwarn dagger.**
-dontwarn javax.inject.**
