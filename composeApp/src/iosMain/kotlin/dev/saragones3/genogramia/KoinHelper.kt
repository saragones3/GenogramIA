package dev.saragones3.genogramia

import dev.saragones3.genogramia.data.firebase.FirebaseAuthDelegate
import dev.saragones3.genogramia.data.firebase.FirestoreDelegate
import dev.saragones3.genogramia.di.getSharedModules
import dev.saragones3.genogramia.di.iosFirebaseModule
import org.koin.core.context.startKoin

fun doInitKoin(
    authDelegate: FirebaseAuthDelegate,
    firestoreDelegate: FirestoreDelegate,
) {
    startKoin {
        modules(getSharedModules() + iosFirebaseModule(authDelegate, firestoreDelegate))
    }
}
