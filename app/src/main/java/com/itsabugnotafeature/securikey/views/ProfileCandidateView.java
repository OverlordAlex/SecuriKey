/**
 * Created by Arne on 10/9/2016.
 */

package com.itsabugnotafeature.securikey.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itsabugnotafeature.securikey.Profile;
import com.itsabugnotafeature.securikey.R;
import com.itsabugnotafeature.securikey.SecureKeyboard;
import com.itsabugnotafeature.securikey.utils.NewProfileDrawableSpan;
import com.itsabugnotafeature.securikey.utils.ProfileDrawableMovementMethod;
import com.itsabugnotafeature.securikey.utils.ProfileDrawableSpan;
import com.itsabugnotafeature.securikey.utils.TextUtil;

import java.util.LinkedList;
import java.util.List;

public class ProfileCandidateView extends LinearLayout {
    private static final String LOG_TAG = "ProfileCandidateView";

    private List<Profile> profiles = new LinkedList<>();

    private TextView suggestionView;

    private SecureKeyboard listener;

    public ProfileCandidateView(Context context) {
        super(context);
        initialiseViews();
    }

    public ProfileCandidateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseViews();
    }

    public ProfileCandidateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProfileCandidateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialiseViews();
    }

    public void initialiseViews() {
        setBackgroundColor(Color.WHITE);

        ViewGroup.LayoutParams containerParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(containerParams);

        setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 100);
        suggestionView = new TextView(getContext());
        suggestionView.setGravity(Gravity.CENTER_VERTICAL);
        suggestionView.setSingleLine(true);
        suggestionView.setMovementMethod(ProfileDrawableMovementMethod.getInstance());
        suggestionView.setHint("Enter a profile name");
        suggestionView.setFocusable(false);
        suggestionView.setTextColor(Color.BLACK);

        addView(suggestionView, params);
    }

    public void prepareToReceiveMasterPassword() {
        suggestionView.setHint("Enter master password");
        suggestionView.setText("");
        profiles = new LinkedList<>();
        suggestionView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_lock_black_24dp), null, null, null);
    }

    public void updateMasterPasswordText(String updatedText) {
        suggestionView.setText(updatedText);
    }

    public void setProfileSuggestions(String matchedText, List<Profile> profiles) {
        Log.i(LOG_TAG, "setProfileSuggestions(): " + profiles.size());

        this.profiles = profiles;

        if (this.profiles.isEmpty() && TextUtil.hasValue(matchedText)) {
            CharSequence text =
                    constructSuggestionTextForNewProfile(matchedText, suggestionView.getTextSize());
            suggestionView.setText(text);
        } else {
            CharSequence text =
                    constructSuggestionText(matchedText, this.profiles, suggestionView.getTextSize());
            suggestionView.setText(text);
        }

        suggestionView.setCompoundDrawables(null, null, null, null);
        suggestionView.setHint("Enter a profile name");
    }

    private CharSequence constructSuggestionText(String matchedText, List<Profile> profiles, float textSize) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (Profile profile : profiles) {
            builder.append(" ").append(profile.getName());

            int highlightStartIndex = profile.getName().indexOf(matchedText);
            int highlightEndIndex = -1;
            if (highlightStartIndex != -1) {
                highlightEndIndex = highlightStartIndex + matchedText.length();
            }

            ProfileDrawableSpan span = new ProfileDrawableSpan(
                    getContext(), profile.getName(), textSize,
                    highlightStartIndex, highlightEndIndex);
            span.setProfile(profile);

            int startIndex = builder.length() - profile.getName().length();
            int endIndex = builder.length();

            builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    private CharSequence constructSuggestionTextForNewProfile(String newText, float textSize) {
        SpannableStringBuilder builder = new SpannableStringBuilder(" ");
        builder.append(newText);

        NewProfileDrawableSpan span = new NewProfileDrawableSpan(
                getContext(), newText, textSize);

        int startIndex = builder.length() - newText.length();
        int endIndex = builder.length();

        builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }

    /**
     * A connection back to the service to communicate with the text field
     * @param listener
     */
    public void setService(SecureKeyboard listener) {
        this.listener = listener;
    }
}