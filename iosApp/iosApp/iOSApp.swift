import SwiftUI
import ComposeApp
import FirebaseCore

// MARK: - App entry point

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        KoinHelperKt.doInitKoin(firebaseDelegate: SwiftFirebaseDelegate())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}