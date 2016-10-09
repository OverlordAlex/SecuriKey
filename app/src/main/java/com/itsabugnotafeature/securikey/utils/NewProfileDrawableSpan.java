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
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.itsabugnotafeature.securikey.models.ProfileController;

import java.lang.ref.WeakReference;

public class NewProfileDrawableSpan extends CustomClickableReplacementSpan {
    private static final String LOG_TAG = "NewProfileDrawableSpan";
    private static final String NEW_TEXT = "NEW";

    private final TextPaint paint;
    private final Context context;
    private final String spanText;
    private final int textColour;
    private final int bubbleColour;
    private final int newBubbleColour;
    private final float cornerRadiusPixels;

    private WeakReference<Drawable> cachedDrawableRef;

    public NewProfileDrawableSpan(Context context, String spanText, float textSizePixels) {
        this.context = context;
        this.spanText = spanText;

        textColour = Color.WHITE;
        bubbleColour = Color.BLUE;
        newBubbleColour = Color.GREEN;

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
        Rect newTextBounds = new Rect();
        paint.getTextBounds(NEW_TEXT, 0, NEW_TEXT.length(), newTextBounds);

        Rect textBounds = new Rect();
        paint.getTextBounds(spanText, 0, spanText.length(), textBounds);

        final float c = 0.4f;
        final float bubblePadding = textBounds.height() * c / 2f;

        int newTextWidth = (int) paint.measureText(NEW_TEXT);
        int textWidth = (int) paint.measureText(spanText);

        Layout newTextLayout = new StaticLayout(NEW_TEXT, paint, newTextWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        Layout textLayout = new StaticLayout(spanText, paint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        float bubbleHeight = textLayout.getHeight() + 2 * bubblePadding;
        float bubbleWidth = newTextLayout.getLineMax(0) + 6 + textLayout.getLineMax(0) + 2 * bubblePadding;

        RectF bubbleBounds = new RectF(0, 0, bubbleWidth, bubbleHeight);

        Bitmap bitmap = Bitmap.createBitmap((int) bubbleWidth, (int) bubbleHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw bubble slightly smaller than its bounds to emulate padding.
        paint.setColor(bubbleColour);
        RectF paddedBubble = new RectF(bubbleBounds.left,
                bubbleBounds.top,
                bubbleBounds.right,
                bubbleBounds.bottom);
        canvas.drawRoundRect(paddedBubble, cornerRadiusPixels, cornerRadiusPixels, paint);

        paint.setColor(newBubbleColour);
        paddedBubble = new RectF(bubbleBounds.left + bubblePadding + textWidth + 3,
                bubbleBounds.top,
                bubbleBounds.right,
                bubbleBounds.bottom);
        canvas.drawRoundRect(paddedBubble, cornerRadiusPixels, cornerRadiusPixels, paint);

        paddedBubble = new RectF(bubbleBounds.left + bubblePadding + textWidth + 3,
                bubbleBounds.top,
                bubbleBounds.left + textWidth + (newTextWidth / 2),
                bubbleBounds.bottom);
        canvas.drawRect(paddedBubble, paint);

        paint.setColor(textColour);

        canvas.save();
        canvas.translate(bubblePadding, bubblePadding);
        textLayout.draw(canvas);

        canvas.translate(textWidth + 6, 0);
        newTextLayout.draw(canvas);

        canvas.restore();

        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(new Rect(0, 0, (int) bubbleWidth, (int) bubbleHeight));
        return drawable;
    }

    @Override
    public void onClick(View widget) {
        ProfileController.getInstance().createNewProfile(spanText);
    }
}