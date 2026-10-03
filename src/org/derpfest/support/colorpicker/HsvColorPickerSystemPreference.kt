/*
 * SPDX-FileCopyrightText: DerpFest AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package org.derpfest.support.colorpicker

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet

/**
 * [ColorPickerSystemPreference] that opens [HsvColorPickerDialog] instead of the legacy picker.
 * The selected color is still stored in Settings.System.
 */
open class HsvColorPickerSystemPreference(
    context: Context,
    attrs: AttributeSet?,
) : ColorPickerSystemPreference(context, attrs) {

    override fun showDialog(state: Bundle?) {
        if (!isEnabled) {
            return
        }
        HsvColorPickerDialog.show(
            context,
            displayColor,
            title ?: "",
            { picked -> onColorChanged(picked) },
            /* alphaSlider */ false,
        )
    }
}
