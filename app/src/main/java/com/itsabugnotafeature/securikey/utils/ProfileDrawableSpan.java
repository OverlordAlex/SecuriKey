/**
 * Created by Arne on 10/9/2016.
 */

package com.itsabugnotafeature.securikey.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;

import com.itsabugnotafeature.securikey.Profile;
import com.itsabugnotafeature.securikey.models.ProfileController;

import java.lang.ref.WeakReference;

public class ProfileDrawableSpan extends CustomClickableReplacementSpan {
    private static final String LOG_TAG = "ProfileDrawableSpan";

    private final TextPaint paint;
    private final Context context;
    private final String spanText;
    private final int textColour;
    private final int bubbleColour;
    private final float cornerRadiusPixels;
    private final int highlightStartIndex;
    private final int highlightEndIndex;

    private Profile profile;

    private WeakReference<Drawable> cachedDrawableRef;

    public ProfileDrawableSpan(Context context, String spanText, float textSizePixels) {
        this(context, spanText, textSizePixels, -1, -1);
    }

    public ProfileDrawableSpan(Context context,
                               String spanText,
                               float textSizePixels,
                               int highlightStartIndex,
                               int highlightEndIndex) {
        this.context = context;
        this.spanText = spanText;
        this.highlightEndIndex = highlightEndIndex;
        this.highlightStartIndex = highlightStartIndex;

        textColour = Color.WHITE;
        bubbleColour = Color.BLUE;

        Resources resources = context.getResources();
        cornerRadiusPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, resources.getDisplayMetrics());

        this.paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.density = context.getResources().getDisplayMetrics().density;
        paint.setTextSize(textSizePixels);
    }

    public String getSpanText() {
        return spanText;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fontMetrics) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();

        if (fontMetrics != null) {
            Paint.FontMetricsInt paintFontMetrics = paint.getFontMetricsInt();
            // Keep our font metrics the same as our paint's font metrics.
            fontMetrics.ascent = paintFontMetrics.ascent;
            fontMetrics.descent = paintFontMetrics.descent;
            fontMetrics.top = paintFontMetrics.top;
            fontMetrics.bottom = paintFontMetrics.bottom;
        }

        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getCachedDrawable();
        canvas.save();

        int drawableCenter = drawable.getIntrinsicHeight() / 2;
        int fontTop = paint.getFontMetricsInt().top;
        int fontBottom = paint.getFontMetricsInt().bottom;
        int transY = (bottom - drawable.getBounds().bottom) -
                (((fontBottom - fontTop) / 2) - drawableCenter);

        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> reference = cachedDrawableRef;
        Drawable drawable = null;

        if (reference != null) {
            drawable = reference.get();
        }

        if (drawable == null) {
            drawable = getDrawable();
            cachedDrawableRef = new WeakReference<>(drawable);
        }

        return drawable;
    }

    private Drawable getDrawable() {
        Rect textBounds = new Rect();
        paint.getTextBounds(spanText, 0, spanText.length(), textBounds);

        final float c = 0.4f;
        final float bubblePadding = textBounds.height() * c / 2f;
        float bubbleHeight = 2 * bubblePadding + textBounds.height();
        float bubbleWidth = 2 * bubblePadding + paint.measureText(spanText);

        Layout layout = new StaticLayout(spanText, paint, (int) bubbleWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        bubbleHeight = layout.getHeight() + 2 * bubblePadding;
        bubbleWidth = layout.getLineMax(0) + 2 * bubblePadding;
        RectF bubbleBounds = new RectF(0, 0, bubbleWidth, bubbleHeight);

        Bitmap bitmap = Bitmap.createBitmap((int) bubbleWidth, (int) bubbleHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw bubble slightly smaller than its bounds to emulate padding.
        float paddingPx = 1f;
        paint.setColor(bubbleColour);
        RectF paddedBubble = new RectF(bubbleBounds.left + paddingPx,
                bubbleBounds.top + paddingPx,
                bubbleBounds.right - paddingPx,
                bubbleBounds.bottom - paddingPx);
        canvas.drawRoundRect(paddedBubble, cornerRadiusPixels, cornerRadiusPixels, paint);

        paint.setColor(textColour);

        if (highlightStartIndex != -1 && highlightEndIndex != -1) {
            Spannable spannable = new SpannableString(spanText);
            spannable.setSpan(new ForegroundColorSpan(Color.RED),
                    highlightStartIndex, highlightEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            layout = new StaticLayout(spannable,
                    paint, (int) bubbleWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        }

        canvas.save();
        canvas.translate(bubblePadding, bubblePadding);
        layout.draw(canvas);
        canvas.restore();

        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(new Rect(0, 0, (int) bubbleWidth, (int) bubbleHeight));
        return drawable;
    }

    @Override
    public void onClick(View widget) {
        ProfileController.getInstance().onProfileSelected(profile);
    }
}