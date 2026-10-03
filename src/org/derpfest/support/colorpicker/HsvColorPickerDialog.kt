/*
 * SPDX-FileCopyrightText: DerpFest AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package org.derpfest.support.colorpicker

import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.util.function.Consumer

/**
 * HSV wheel plus brightness (and optional alpha). Shared by Settings and Launcher so both open the
 * same Compose picker.
 */
object HsvColorPickerDialog {

    @JvmStatic
    @JvmOverloads
    fun show(
        context: Context,
        initialColor: Int,
        title: CharSequence,
        onColorPicked: Consumer<Int>,
        alphaSlider: Boolean = false,
    ) {
        val selectedColor = intArrayOf(initialColor)
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                onColorPicked.accept(selectedColor[0])
            }
            .create()
        val composeView = ComposeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            context.findLifecycleOwner()?.let { owner ->
                setViewTreeLifecycleOwner(owner)
            }
            setContent {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    HsvPickerContent(
                        initialColor = initialColor,
                        alphaSlider = alphaSlider,
                        onColorChanged = { selectedColor[0] = it },
                    )
                }
            }
        }
        dialog.setView(composeView)
        dialog.window?.let { window ->
            val density = context.resources.displayMetrics.density
            val maxWidthPx = (400 * density + 0.5f).toInt()
            val screenWidth = context.resources.displayMetrics.widthPixels
            val lp = window.attributes
            lp.width = minOf(maxWidthPx, (screenWidth * 0.92f).toInt())
            window.attributes = lp
        }
        dialog.show()
    }
}

@Composable
private fun HsvPickerContent(
    initialColor: Int,
    alphaSlider: Boolean,
    onColorChanged: (Int) -> Unit,
) {
    val controller = rememberColorPickerController()
    val startColor = Color(initialColor)
    var selected by remember { mutableIntStateOf(initialColor) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HsvColorPicker(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape),
            controller = controller,
            initialColor = startColor,
            onColorChanged = { envelope ->
                selected = envelope.color.toArgb()
                onColorChanged(selected)
            },
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(18.dp)),
            controller = controller,
            initialColor = startColor,
            borderRadius = 18.dp,
            borderSize = 0.dp,
            wheelRadius = 14.dp,
        )
        if (alphaSlider) {
            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp)),
                controller = controller,
                initialColor = startColor,
                borderRadius = 18.dp,
                borderSize = 0.dp,
                wheelRadius = 14.dp,
            )
        }
        val shown = Color(selected)
        val label = if (android.graphics.Color.luminance(selected) > 0.6f) Color.Black else Color.White
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(shown),
            contentAlignment = Alignment.BottomStart,
        ) {
            BasicText(
                text = if (alphaSlider) selected.toHexArgb() else selected.toHexRgb(),
                modifier = Modifier.padding(12.dp),
                style = TextStyle(color = label, fontSize = 14.sp),
            )
        }
    }
}

private fun Int.toHexRgb(): String = "#%06X".format(0xFFFFFF and this)

private fun Int.toHexArgb(): String = "#%08X".format(this)

private tailrec fun Context.findLifecycleOwner(): LifecycleOwner? = when (this) {
    is LifecycleOwner -> this
    is ContextWrapper -> baseContext.findLifecycleOwner()
    else -> null
}
