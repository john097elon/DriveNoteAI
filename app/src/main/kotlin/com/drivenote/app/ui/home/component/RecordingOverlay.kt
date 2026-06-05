package com.drivenote.app.ui.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.drivenote.app.ui.theme.CalmMint
import com.drivenote.app.ui.theme.RecordRed
import java.util.Locale

@Composable
fun RecordingOverlay(
    recordingSeconds: Int,
    onStop: () -> Unit
) {
    val anim = rememberInfiniteTransition(label = "recording")

    val outerScale by anim.animateFloat(
        initialValue = 0.55f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "outerScale"
    )
    val innerScale by anim.animateFloat(
        initialValue = 0.72f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "innerScale"
    )

    val w1 by anim.animateFloat(0.25f, 0.85f, infiniteRepeatable(tween(600,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w1")
    val w2 by anim.animateFloat(0.50f, 1.00f, infiniteRepeatable(tween(780,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w2")
    val w3 by anim.animateFloat(0.20f, 0.95f, infiniteRepeatable(tween(520,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w3")
    val w4 by anim.animateFloat(0.60f, 1.00f, infiniteRepeatable(tween(850,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w4")
    val w5 by anim.animateFloat(0.35f, 0.90f, infiniteRepeatable(tween(680,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w5")
    val w6 by anim.animateFloat(0.45f, 0.80f, infiniteRepeatable(tween(730,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w6")
    val w7 by anim.animateFloat(0.30f, 1.00f, infiniteRepeatable(tween(640,  easing = FastOutSlowInEasing), RepeatMode.Reverse), "w7")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.84f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            color  = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape  = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(RecordRed, CircleShape)
                    )
                    Text(
                        text  = "녹음 중",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier.size(148.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(148.dp)
                            .graphicsLayer(scaleX = outerScale, scaleY = outerScale, alpha = 0.14f)
                            .background(RecordRed, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .graphicsLayer(scaleX = innerScale, scaleY = innerScale, alpha = 0.26f)
                            .background(RecordRed, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(RecordRed, CircleShape)
                    )
                }

                Row(
                    modifier = Modifier.height(48.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf(w1, w2, w3, w4, w5, w6, w7).forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(48.dp * h)
                                .clip(RoundedCornerShape(3.dp))
                                .background(CalmMint.copy(alpha = 0.82f))
                        )
                    }
                }

                Text(
                    text  = recordingSeconds.toTimerText(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick  = onStop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor   = MaterialTheme.colorScheme.onError
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("녹음 중지", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

private fun Int.toTimerText(): String {
    val min = this / 60
    val sec = this % 60
    return String.format(Locale.ROOT, "%02d:%02d", min, sec)
}
