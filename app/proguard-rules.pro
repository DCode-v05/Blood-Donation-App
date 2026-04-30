# Project-specific ProGuard rules

# Firebase
-keep class com.google.firebase.** { *; }

# Models — keep field names so Firestore can serialise/deserialise
-keep class com.lifesaver.blooddonation.models.** { *; }

# Retrofit + OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers class * { @retrofit2.http.* <methods>; }

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
