package net.gabor7d2.simpleinventory.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.gabor7d2.simpleinventory.persistence.Preferences
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var prefs: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser != null) {
            afterSignIn()
            return
        }

        prefs = Preferences(this)
        if (prefs.areLoginCredentialsSaved()) {
            binding.etEmail.setText(prefs.getEmail())
            binding.etPassword.setText(prefs.getPassword())
            signInToFirebase()
        }

        binding.buttonLogin.setOnClickListener {
            signInToFirebase()
        }
    }

    private fun signInToFirebase() {
        if (binding.etEmail.text.isEmpty() || binding.etPassword.text.isEmpty()) {
            Toast.makeText(
                baseContext,
                R.string.login_error_empty,
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        binding.buttonLogin.isEnabled = false

        // sign in to firebase or if user doesnt exist, register
        Firebase.auth.signInWithEmailAndPassword(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    prefs.storeLoginCredentials(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                    afterSignIn()
                } else {
                    Toast.makeText(
                        baseContext,
                        R.string.login_registering,
                        Toast.LENGTH_SHORT,
                    ).show()
                    Firebase.auth.createUserWithEmailAndPassword(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                prefs.storeLoginCredentials(
                                    binding.etEmail.text.toString(),
                                    binding.etPassword.text.toString()
                                )
                                afterSignIn()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    R.string.login_error,
                                    Toast.LENGTH_SHORT,
                                ).show()
                                binding.buttonLogin.isEnabled = true
                            }
                        }
                }
            }
    }

    private fun afterSignIn() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}