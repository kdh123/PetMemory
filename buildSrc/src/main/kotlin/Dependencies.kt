import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

object Dependencies {

    const val composeActivity = "androidx.activity:activity-compose:${Versions.compose}"
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeUI = "androidx.compose.ui:ui"
    const val composeUIGraphic = "androidx.compose.ui:ui-graphics"
    const val composePreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial = "androidx.compose.material3:material3"
    const val composeUITest = "androidx.compose.ui:ui-test-junit4"
    const val composeUIToolingTest = "androidx.compose.ui:ui-tooling"
    const val composeUIManifestTest = "androidx.compose.ui:ui-test-manifest"
    const val composeFlowState = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.composeFlowState}"
    const val composeHilt = "androidx.hilt:hilt-navigation-compose:${Versions.composeHilt}"
    const val composeNavigation = "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
    const val composeGlide = "com.github.bumptech.glide:compose:${Versions.composeGlide}"


    const val constraintLayout = "androidx.constraintlayout:constraintlayout:"
    const val viewPager2 = "androidx.viewpager2:viewpager2:"
    const val recyclerView = "androidx.recyclerview:recyclerview:"


    const val googleGmsLocation = "com.google.android.gms:play-services-location:${Versions.googleGmsLocation}"
    const val androidxActivity = "androidx.activity:activity-ktx:${Versions.androidxActivity}"
    const val androidxFragment = "androidx.fragment:fragment-ktx:${Versions.androidxFragment}"
    const val googlematerial = "com.google.android.material:material:${Versions.googleMaterial}"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"

    const val cameraxCore = "androidx.camera:camera-core:${Versions.camerax}"
    const val cameraxCamera2 = "androidx.camera:camera-camera2:${Versions.camerax}"
    const val cameraxLifecycle = "androidx.camera:camera-lifecycle:${Versions.camerax}"
    const val cameraxView = "androidx.camera:camera-view:${Versions.camerax}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val naverMap = "com.naver.maps:map-sdk:${Versions.naverMap}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConvertor = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    const val dataStore = "androidx.datastore:datastore:${Versions.dataStore}"
    const val dataStoreCore = "androidx.datastore:datastore-core:${Versions.dataStore}"
    const val dataStorePreference = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    const val dataStorePreferenceCore = "androidx.datastore:datastore-preferences-core:${Versions.dataStore}"
    const val dataStoreProto = "com.google.protobuf:protobuf-javalite:${Versions.dataStoreProto}"

    const val room = "androidx.room:room-ktx:${Versions.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    const val googleGson = "com.google.code.gson:gson:${Versions.googleGson}"

    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val jUnitExt = "androidx.test.ext:junit:${Versions.jUnitExt}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutine}"

    const val truth = "com.google.truth:truth:${Versions.truth}"
}

fun DependencyHandler.compose() {
    implementaion("${Dependencies.composeUI}")
    implementaion("${Dependencies.composeActivity}")
    implementaion(platform("${Dependencies.composeBom}"))
    implementaion("${Dependencies.composeMaterial}")
    implementaion("${Dependencies.composePreview}")
    implementaion("${Dependencies.composePreview}")
    implementaion("${Dependencies.composeUIGraphic}")
    implementaion("${Dependencies.composeFlowState}")
    implementaion("${Dependencies.composeHilt}")
    implementaion("${Dependencies.composeNavigation}")
    implementaion("${Dependencies.composeGlide}")

    androidTestImplementation(platform("${Dependencies.composeBom}"))
    androidTestImplementation("${Dependencies.composeUITest}")
    debugImplementation("${Dependencies.composeUIToolingTest}")
    debugImplementation("${Dependencies.composeUIManifestTest}")
}

fun DependencyHandler.camerax() {
    implementaion("${Dependencies.cameraxCore}")
    implementaion("${Dependencies.cameraxCamera2}")
    implementaion("${Dependencies.cameraxLifecycle}")
    implementaion("${Dependencies.cameraxView}")
}

fun DependencyHandler.room() {
    implementaion("${Dependencies.room}")
    implementaion("${Dependencies.roomRuntime}")
    kapt("${Dependencies.roomCompiler}")
}

fun DependencyHandler.retrofit() {
    implementaion("${Dependencies.retrofit}")
    implementaion("${Dependencies.retrofitConvertor}")
    implementaion("${Dependencies.okhttp}")
    implementaion("${Dependencies.okhttpInterceptor}")
}

fun DependencyHandler.dataStore() {
    implementaion("${Dependencies.dataStore}")
    implementaion("${Dependencies.dataStoreCore}")
    implementaion("${Dependencies.dataStorePreference}")
    implementaion("${Dependencies.dataStorePreferenceCore}")
    implementaion("${Dependencies.dataStoreProto}")
}

fun DependencyHandler.hilt() {
    implementaion("${Dependencies.hilt}")
    kapt("${Dependencies.hiltCompiler}")
}

fun DependencyHandler.testCode() {
    testImplementation("${Dependencies.jUnit}")
    androidTestImplementation("${Dependencies.jUnitExt}")
    androidTestImplementation("${Dependencies.espresso}")
    androidTestImplementation("${Dependencies.coroutine}")
    androidTestImplementation("${Dependencies.truth}")
    testImplementation("${Dependencies.truth}")
}

fun DependencyHandler.dataModule() {
    implementaion(project(":data"))
}

fun DependencyHandler.domainModule() {
    implementaion(project(":domain"))
}