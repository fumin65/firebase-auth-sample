package dev.fumin.firebase.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQ_CODE_GOOGLE_SIGN_IN = 100
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        client = GoogleSignIn.getClient(this, googleSignInOptions)

        button.setOnClickListener {
            startActivityForResult(client.signInIntent, REQ_CODE_GOOGLE_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_GOOGLE_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            accountTask.result?.also {
                val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authResultTask ->
                        if (authResultTask.isSuccessful) {
                            if (BuildConfig.DEBUG) {
                                Log.d(
                                    "test",
                                    "Logged in ${authResultTask.result?.user?.displayName}"
                                )
                            }
                        }
                    }
            }
        }
    }
}
