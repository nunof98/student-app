package pt.ipca.cm_tp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipca.cm_tp.MyApplication
import pt.ipca.cm_tp.R
import pt.ipca.cm_tp.databases.StudentRepository
import pt.ipca.cm_tp.ui.MainActivity
import pt.ipca.cm_tp.ui.RegisterActivity


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repositories
        val studentRepository = (requireActivity().application as MyApplication).studentRepository

        // Get student id number
        val studentID = (activity as MainActivity).studentID.toInt()
        studentRepository.findStudentById(studentID) { student ->
            // Set student name on textView
            requireView()
                .findViewById<TextView>(R.id.textView_student_name)
                .text = "${student?.firstName} ${student?.lastName} ● ${student?.id}"

            // Set student course on textView
            requireView()
                .findViewById<TextView>(R.id.textView_student_course)
                .text = "${student?.course} — ${student?.year}º year"

            // Set student email on textView
            requireView()
                .findViewById<TextView>(R.id.textView_student_mail)
                .text = "a${student?.id}@alunos.ipca.pt"
        }
    }
}