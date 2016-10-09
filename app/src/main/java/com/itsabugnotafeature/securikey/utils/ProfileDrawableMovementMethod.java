/**
 * Created by User on 10/9/2016.
 */

package com.itsabugnotafeature.securikey.utils;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class ProfileDrawableMovementMethod extends LinkMovementMethod {

    private static final ProfileDrawableMovementMethod INSTANCE =
            new ProfileDrawableMovementMethod();

    private ProfileDrawableMovementMethod() {
    }

    public static ProfileDrawableMovementMethod getInstance() {
        return INSTANCE;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            CustomClickableReplacementSpan[] profileSpans = buffer.getSpans(off, off, CustomClickableReplacementSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else if (profileSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    profileSpans[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(profileSpans[0]),
                            buffer.getSpanEnd(profileSpans[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return false;
    }
}
