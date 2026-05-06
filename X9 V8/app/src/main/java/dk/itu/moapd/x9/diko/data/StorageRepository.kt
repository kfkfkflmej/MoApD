package dk.itu.moapd.x9.diko.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageRepository (
    bucketUrl: FirebaseStorage = FirebaseStorage.getInstance()
)
{
    companion object
    {
        private const val PATH_REPORTS = "reports"
    }

    private val storage = (bucketUrl)

    /**
     * Uploads a local file (identified by [localUri]) to [remotePath] in the storage bucket and
     * returns a Task that resolves with the public download Uri of the uploaded file.
     *
     * @param localUri The Uri of the local file to upload.
     * @param remotePath The path in the storage bucket where the file will be uploaded.
     *
     * @return A Task that resolves with the public download Uri of the uploaded file.
     */
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