package kr.lifesemantics.kakaologin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashEnd: () -> Unit) {
    val scale = remember { Animatable(0f) } // 확대 애니메이션을 위한 Animatable 객체 초기화
    val alpha = remember { Animatable(0f) } // 페이드 인 애니메이션을 위한 Animatable 객체 초기화

    // 애니메이션 실행
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f, // 최종 스케일 값
            animationSpec = tween(durationMillis = 1000) // 1초 동안 애니메이션
        )
        alpha.animateTo(
            targetValue = 1f, // 최종 알파 값(투명도)
            animationSpec = tween(durationMillis = 1000) // 1초 동안 애니메이션
        )
        delay(1500) // 애니메이션 후 추가 대기 시간
        onSplashEnd() // 애니메이션이 끝나면 onSplashEnd 콜백 호출
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // 애니메이션 적용: scale과 alpha 값을 적용하여 Image에 애니메이션 효과를 줍니다.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
        )
    }
}