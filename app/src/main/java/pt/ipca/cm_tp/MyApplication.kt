package pt.ipca.cm_tp

import android.app.Application
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.cm_tp.databases.AppDatabase
import pt.ipca.cm_tp.databases.DeadlineRepository
import pt.ipca.cm_tp.databases.StudentRepository
import pt.ipca.cm_tp.databases.SubjectRepository

class MyApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val studentRepository by lazy { StudentRepository(database.studentDao()) }
    val subjectRepository by lazy { SubjectRepository(database.subjectDao()) }
    val deadlineRepository by lazy { DeadlineRepository(database.deadlineDao()) }
}