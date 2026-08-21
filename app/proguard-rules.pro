# Keep MainActivity and all app components
-keep class com.example.** { *; }
-keepclassmembers class com.example.** { *; }

# Keep Room entities, DAOs, and database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Keep Moshi & Retrofit models
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
