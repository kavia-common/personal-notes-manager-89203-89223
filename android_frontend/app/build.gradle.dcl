androidApplication {
    namespace = "org.example.app"

    dependencies {
        // AndroidX core UI
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.constraintlayout:constraintlayout:2.2.0")

        // Room (runtime + KTX + compiler via kapt-like dependency in DCL is not supported; Room runtime only)
        implementation("androidx.room:room-runtime:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")

        // Lifecycle
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

        // Remove sample dependencies
        // implementation("org.apache.commons:commons-text:1.11.0")
        // implementation(project(":utilities"))
    }
}
