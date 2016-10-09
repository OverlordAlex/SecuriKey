/**
 * Created by User on 10/9/2016.
 */

package com.itsabugnotafeature.securikey.utils;

import android.text.style.ReplacementSpan;
import android.view.View;

public abstract class CustomClickableReplacementSpan extends ReplacementSpan {

    abstract void onClick(View widget);
}
