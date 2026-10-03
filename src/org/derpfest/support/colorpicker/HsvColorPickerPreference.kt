/*
 * SPDX-FileCopyrightText: DerpFest AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package org.derpfest.support.colorpicker

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet

/** [ColorPickerPreference] that opens [HsvColorPickerDialog] instead of the legacy picker. */
class HsvColorPickerPreference(
    context: Context,
    attrs: AttributeSet?,
) : ColorPickerPreference(context, attrs) {

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
