package com.example.chapter9

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chapter9.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("loginActivity", "error $error")
            error.printStackTrace()
        } else if (token != null) {
            Log.e("loginActivity", "login with kakao account token : $token")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        KakaoSdk.init(this, "853366caf1f19672313c0606e6d41bb9")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.kakaoTalkLoginButton.setOnClickListener {

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                //카카오톡 로그인
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->

                    if(error != null) {
                        //카카오톡 로그인 실패
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.e("loginActivity", "token == $token")
                    }

                }
            } else {
                //카카오 계정 로그인
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }

        }


    }
}