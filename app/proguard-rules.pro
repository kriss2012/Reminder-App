#####################################################################
# Baseline Android/Kotlin hygiene
#####################################################################

# Keep important attributes commonly used by reflection and JSON libs
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses

# Preserve enum methods used reflectively by some libs
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable creators (covers Navigation args that use Parcelable)
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# If any Serializable args/entities are used (Navigation or elsewhere)
# (Optional) Uncomment if using custom Serializable types
# -keep class ** implements java.io.Serializable { *; }

#####################################################################
# Room (2.7.x+)
#####################################################################

# Room finds the generated DB implementation reflectively by name.
# This rule ensures its default constructor remains and class name isn’t stripped.
-keep class * extends androidx.room.RoomDatabase { <init>(); }

#####################################################################
# Gson (2.10.1)
#####################################################################

# Preserve generic type info and annotations that Gson relies on
-keepattributes Signature
-keepattributes *Annotation*

# Silence legacy JDK internals warnings Gson may touch
-dontwarn sun.misc.**

# Keep TypeToken and subclasses (helps with generic parsing)
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Keep custom adapters and factories (so @JsonAdapter and manual registration work)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Preserve fields annotated with @SerializedName even if obfuscated
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# If NOT using @SerializedName and relying on raw field names,
# keep your model package fields (adjust the package):
 -keep class com.pillpal.model.** { <fields>; }

#####################################################################
# kotlinx-serialization-json (1.8.x)
#####################################################################

# The library ships consumer rules; generally no extra rules are needed.
# If using @Serializable classes with NAMED companion objects, add rules below
# (replace packages/classes with the actual ones that have named companions):

# Needed for reflection on declared classes of companions
-keepattributes InnerClasses

# Example pattern for named companion serializers (ProGuard & R8 compat mode)
# -if @kotlinx.serialization.Serializable class
#   com.example.HasNamedCompanion,
#   com.example.HasNamedCompanion2
# {
#     static **$* *;
# }
# -keepnames class <1>$$serializer {
#     static <1>$$serializer INSTANCE;
# }

# Example for R8 full mode (AGP 8.3+ defaults). Keep both serializer and class.
# -if @kotlinx.serialization.Serializable class
#   com.example.HasNamedCompanion,
#   com.example.HasNamedCompanion2
# {
#     static **$* *;
# }
# -keepnames class <1>$$serializer { static <1>$$serializer INSTANCE; }
# -keepclasseswithmembers,allowshrinking,allowobfuscation,allowaccessmodification class
#   com.example.HasNamedCompanion,
#   com.example.HasNamedCompanion2
# { *; }

#####################################################################
# Navigation Compose
#####################################################################

# Navigation Compose itself typically needs no special rules.
# If passing custom types via arguments and those types are Parcelable,
# the baseline Parcelable rule above is sufficient.

# If declaring custom Serializable types in navigation arguments (XML graphs),
# consider keeping names or annotate those classes with @Keep:
# -keepnames class com.yourapp.navargs.** implements java.io.Serializable

#####################################################################
# Hilt / Dagger (2.56.x)
#####################################################################

# In most app modules, Hilt requires no additional rules; generated classes
# are referenced directly and retained by R8.

# If using @EntryPoint / @EarlyEntryPoint accessed reflectively across modules,
# these optional rules keep the annotated entry points (low risk to keep):
# -keep,allowshrinking,allowobfuscation @dagger.hilt.EntryPoint class *
# -keep,allowshrinking,allowobfuscation @dagger.hilt.android.EarlyEntryPoint class *

# If build outputs complain about missing Hilt classes in multi-module setups
# under newer AGP/R8 defaults, prefer fixing build configuration (e.g., disable
# minification in library modules and rely on consumer rules), rather than
# broad keep rules.

#####################################################################
# Noise suppression (optional)
#####################################################################

# Some projects reference javax annotations not present at runtime on Android
-dontwarn javax.annotation.**
