package dk.itu.moapd.x9.diko.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageRepository (
    bucketUrl: FirebaseStorage = FirebaseStorage.getInstance()
)
{
    /**
     *  Based on Fabricio's examples.
     *  After that I changed it according to the design of my application.
     */
    companion object
    {
        private const val PATH_REPORTS = "reports"
    }

    private val storage = (bucketUrl)

    fun uploadFile(localUri: Uri, remotePath: String): Task<Uri> {
        val ref: StorageReference = storage.reference.child(PATH_REPORTS+remotePath)
        // Put the file and then request the downloadUrl.
        return ref.putFile(localUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                throw (task.exception ?: Exception("Upload failed"))
            }
            ref.downloadUrl
        }
    }
}