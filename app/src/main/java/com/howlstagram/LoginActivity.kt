package com.howlstagram

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var googleSingInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSingInClient = GoogleSignIn.getClient(this, gso)


        email_login_btn.setOnClickListener {
            signinAndSignup()
        }

        google_sign_in_button.setOnClickListener {
            // First step
            googleLogin()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 구글에서 승인된 정보를 가지고 오기
        if (requestCode == GOOGLE_LOGIN_CODE) {

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            println(result?.status.toString())
            if (result!!.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                //progress_bar.visibility = View.GONE
            }
        }
    }

    fun googleLogin(){
        var signInIntent = googleSingInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }


    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // 로그인 성공
                    moveMainPage(task.result?.user)
                } else{
                    // Login 실패
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(email_editText.text.toString(),email_editText.text.toString())
            ?.addOnCompleteListener {
            task ->
                if(task.isSuccessful){
                    // 회원가입
                    moveMainPage(task.result?.user)
                } else if(!task.exception?.message.isNullOrEmpty()){
                    // 로그인 에러 메시지
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else{
                    // Login if you have account
                    signinEmail()
                }
        }
    }
    fun signinEmail(){
        auth?.createUserWithEmailAndPassword(email_editText.text.toString(),email_editText.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // 로그인 성공
                    moveMainPage(task.result?.user)
                } else{
                    // Login 실패
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}