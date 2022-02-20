package pt.ipca.cm_tp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.doAsync
import pt.ipca.cm_tp.R
import pt.ipca.cm_tp.databases.http.HttpHelper
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textInputName: TextInputLayout
    private lateinit var textInputEmail: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Bind button press to login function
        findViewById<Button>(R.id.button_sign_up).setOnClickListener {
            //performSignUp()
            // Validade Credentials
            doAsync {
                Log.d("ANSWER","helloworld!!")
                val http : HttpHelper = HttpHelper()
                textInputEmail = findViewById(R.id.til_email)
                val email = textInputEmail.editText?.text.toString()
                val studentID = email.substring(1, email.indexOf("@"))
                val jsonSTinfo = http.getStudent(studentID)
            }
        }
    }

    /**
     * Performs Firebase sign up.
     * If successful it will change current activity to Login activity
     * If it isn't successful it will alert user trough error
     * messages, depending on the cause
     */
    private fun performSignUp() {
        textInputName = findViewById(R.id.til_name)
        textInputEmail = findViewById(R.id.til_email)
        textInputPassword = findViewById(R.id.til_password)
        val textViewError = findViewById<TextView>(R.id.textView_error)

        findViewById<TextInputLayout>(R.id.til_password).addOnEditTextAttachedListener {
            if (textInputPassword.isErrorEnabled) {
                //Remove error message and layout space
                textInputEmail.error = null
                textInputEmail.isErrorEnabled = false
            }
        }

        // Get user information
        val name = textInputName.editText?.text.toString()
        val email = textInputEmail.editText?.text.toString()
        val password = textInputPassword.editText?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty())
        {
            // Reset visibility
            textViewError.visibility = View.GONE

            if (validateEmail(email) && validatePassword(password)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Set user
                            setUser(task.result.user!!.uid, email, name)
                            // Get intent and start activity
                            changeToLoginActivity()
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(this, getString(R.string.dialog_you_are_signed_up),
                                Toast.LENGTH_LONG).show()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, getString(R.string.dialog_ign_up_failed),
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
        } else {
            // If user doesn't fill out all fields
            textViewError.visibility = View.VISIBLE
        }
    }

    /**
     *
     */
    private fun setUser(id: String, name: String, email: String) {
        val db = Firebase.firestore

        var parts  = name.split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        parts.removeAt(0)
        val lastName = parts.joinToString(" ")

        val number = email.substring(1, email.indexOf("@"))

        // Create a new user with a first and last name
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "id" to number
        )

        // Add a new document with a generated ID
        db.collection("students").document()
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("FIRESTORE", "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w("FIRESTORE", "Error adding document", e)
            }
    }

    /**
     * Validates if email is from IPCA
     * @param   email   a string that will be evaluated
     * @return          true or false
     */
    private fun validateEmail(email: String): Boolean {
        if (email.isNotEmpty()) {
            //Remove error message and layout space
            textInputEmail.error = null
            textInputEmail.isErrorEnabled = false

            if ((email.contains("@ipca.pt")) || email.contains("@alunos.ipca.pt")) {
                //Remove error message and layout space
                textInputEmail.error = null
                textInputEmail.isErrorEnabled = false

                return true
            } else {
                //Display error message
                textInputEmail.error = getString(R.string.error_you_must_use_an_IPCA_email)
            }
        } else {
            //Display error message
            textInputEmail.error = getString(R.string.error_enter_an_email)
        }

        return false
    }

    /**
     * Validates if password is considered strong (has numbers,
     * uppercase and lowercase letters, and special characters).
     * @param   password    a string that will be evaluated
     * @return              true or false
     */
    private fun validatePassword(password: String): Boolean {
        if (password.isNotEmpty()) {
            // Remove error message and layout space
            textInputPassword.error = null
            textInputPassword.isErrorEnabled = false

            // Regex to validate if password is strong
            val regex = "^(?=.*[0-9])" +
                        "(?=.*[a-z])(?=.*[A-Z])" +
                        "(?=.*[@#$%^&+=?!])" +
                        "(?=\\S+$).{8,20}$"

            val p = Pattern.compile(regex)
            val m = p.matcher(password)

            // Display error message
            if (!m.matches()) {
                textInputPassword.error = getString(R.string.error_password_isnt_strong_enough)
            }

            return m.matches()

        } else {
            // Display error message
            textInputPassword.error = getString(R.string.error_enter_a_password)
        }

        return false
    }

    /**
     * Changes activity to Login activity
     */
    private fun changeToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}