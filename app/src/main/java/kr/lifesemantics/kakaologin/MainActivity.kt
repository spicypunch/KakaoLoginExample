package kr.lifesemantics.kakaologin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kr.lifesemantics.kakaologin.ui.theme.KakaoLoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, "0fb17c2727b584dbb1cc317acabfd22a")
        setContent {
//            App() {
//                SplashScreen {
//                    MainScreen()
//                    Toast.makeText(this, "rlawhdaflsj", Toast.LENGTH_LONG).show()
//                }
//            }
            MainScreen()
        }
    }
}

@Composable
fun App(content: @Composable () -> Unit) {
    KakaoLoginTheme {
        content()
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { requestLogin(context) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kakao_login_large_wide),
                    contentDescription = "kakao",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun requestLogin(context: Context) {
    /**
     * 카카오계정으로 로그인 공통 callback 구성
     * 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
     */
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.i("Kakao", "로그인 실패 $error")
        } else if (token != null) {
            Log.i(
                "Kakao",
                "로그인 성공 accessToken: ${token.accessToken} refreshToeken: ${token.refreshToken}"
            )
        }
    }

    /**
     * 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
     */
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.i("Kakao", "카카오톡으로 로그인 실패", error)

                /**
                 * 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                 * 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                 */
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                /**
                 * 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                 */
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                Log.i("Kakao", "카카오톡으로 로그인 성공 ${token.accessToken}")
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}

//
//@Preview
//@Composable
//private fun Preview() {
//    App()
//}