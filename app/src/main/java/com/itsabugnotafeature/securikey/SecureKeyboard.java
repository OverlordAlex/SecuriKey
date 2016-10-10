/**
 * Created by alex on 2016/10/08.
 *
 * Based on https://code.tutsplus.com/tutorials/create-a-custom-keyboard-on-android--cms-22615
 */

package com.itsabugnotafeature.securikey;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.itsabugnotafeature.securikey.profiles.Profile;
import com.itsabugnotafeature.securikey.models.ProfileController;
import com.itsabugnotafeature.securikey.models.ProfileController.ProfileSuggestionListener;
import com.itsabugnotafeature.securikey.utils.TextUtil;
import com.itsabugnotafeature.securikey.views.ProfileCandidateView;

import java.util.List;

public class SecureKeyboard
        extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, ProfileSuggestionListener {
    private static final String LOG_TAG = "SecureKeyboard";

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean currentlyCapslock = false;

    private boolean entering_masterPassword = false;
    private Profile currentProfile;
    private String profileString = "";
    private String masterPasswordString = "";

    private ProfileCandidateView candidateView = null;

    private CompletionInfo[] completions;

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();

        switch (primaryCode) {

            case Keyboard.KEYCODE_DELETE:
                Log.i(LOG_TAG, "Keyboard.KEYCODE_DELETE");
                if (entering_masterPassword) {
                    if (masterPasswordString.length() > 0) {
                        masterPasswordString = masterPasswordString.substring(0, masterPasswordString.length() - 1);
                        candidateView.updateMasterPasswordText(masterPasswordString);
                    }
                } else {
                    if (profileString.length() > 0) {
                        profileString = profileString.substring(0, profileString.length() - 1);
                        updateCandidates();
                    }
                }
                break;

            case Keyboard.KEYCODE_SHIFT:
                Log.i(LOG_TAG, "Keyboard.KEYCODE_SHIFT");
                currentlyCapslock = !currentlyCapslock;
                keyboard.setShifted(currentlyCapslock);
                kv.invalidateAllKeys();
                break;

            case Keyboard.KEYCODE_DONE:
                Log.i(LOG_TAG, "Keyboard.KEYCODE_DONE");
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;

            case 9999:
                Log.i(LOG_TAG, "9999");
                if (entering_masterPassword
                        && currentProfile != null
                        && TextUtil.hasValue(masterPasswordString)) {

                    String generatedPassword =
                            currentProfile.getPasswordHash(masterPasswordString.toString());

                    ic.commitText(generatedPassword, generatedPassword.length() + 1);

                    entering_masterPassword = false;
                    masterPasswordString = "";
                    currentProfile = null;

                    requestHideSelf(0);

                    InputMethodManager ime = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (ime != null) {
                        ime.showInputMethodPicker();
                    }

                    ProfileController.getInstance().setSuggestionMatchingText("");
                }
                profileString = "";
                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && currentlyCapslock) {
                    code = Character.toUpperCase(code);
                }

                if (entering_masterPassword && currentProfile != null) {
                    masterPasswordString += code;
                    candidateView.updateMasterPasswordText(masterPasswordString);
                } else {
                    profileString += code;
                    updateCandidates();
                }

        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);

        Log.i(LOG_TAG, "Registering keyboard for callbacks.");
        ProfileController.getInstance().setListener(this);

        setCandidatesViewShown(true);

        return kv;
    }

    @Override
    public View onCreateCandidatesView() {
        candidateView = new ProfileCandidateView(this);
        candidateView.setService(this);

        Pair<String, List<Profile>> suggestions =
                ProfileController.getInstance().getProfilesMatchingSuggestionText();
        candidateView.setProfileSuggestions(suggestions.first, suggestions.second);

        return candidateView;
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        ProfileController.getInstance().setSuggestionMatchingText(profileString);
    }

    public void onNewProfileSuggestions(String matchedText, List<Profile> profiles) {
        Log.i(LOG_TAG, "onNewProfileSuggestions()");
        if (candidateView != null) {
            candidateView.setProfileSuggestions(matchedText, profiles);
        }
    }

    public void onNewProfileCreated(Profile profile) {
        // profileString = "";
    }

    public void onProfileClickedProfile(Profile profile) {
        entering_masterPassword = true;
        currentProfile = profile;
        profileString = "";
        masterPasswordString = "";
        candidateView.prepareToReceiveMasterPassword();
    }
}
