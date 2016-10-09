package com.itsabugnotafeature.securikey;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputConnection;

import com.itsabugnotafeature.securikey.models.ProfileController;
import com.itsabugnotafeature.securikey.models.ProfileController.ProfileSuggestionListener;
import com.itsabugnotafeature.securikey.views.ProfileCandidateView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2016/10/08.
 *
 * Based on https://code.tutsplus.com/tutorials/create-a-custom-keyboard-on-android--cms-22615
 */

public class SecureKeyboard
        extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, ProfileSuggestionListener {
    private static final String LOG_TAG = "SecureKeyboard";

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean currentlyCapslock = false;

    private boolean entering_masterPassword = false;
    private String profileString = "";
    private String masterPasswordString = "";

    private ProfileCandidateView candidateView = null;

    private CompletionInfo[] completions;

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();

        switch(primaryCode){

        case Keyboard.KEYCODE_DELETE :
            if (entering_masterPassword) {
                if( masterPasswordString.length() > 0) {
                    masterPasswordString = masterPasswordString.substring(0, masterPasswordString.length() - 1);
                }
            } else {
                if( profileString.length() > 0) {
                    profileString = profileString.substring(0, profileString.length() - 1);
                }
            }
            break;

        case Keyboard.KEYCODE_SHIFT:
            currentlyCapslock = !currentlyCapslock;
            keyboard.setShifted(currentlyCapslock);
            kv.invalidateAllKeys();
            break;

        case Keyboard.KEYCODE_DONE:
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            break;

         case 9999:
             if (entering_masterPassword) {
                 // user has entered the masterpassword
                 // put in the encrypted string
                     // TODO this needs to be done
//                     String password = ProfileController
//                             .getInstance()
//                             .getCurrentProfile()
//                             .getPasswordHash(masterPasswordString);
//
//                     ic.commitText(password, password.length() + 1);
//
//                     masterPasswordString = "";
//                     profileString = "";
             } else {
                 // user has finished entering the key word
                 // TODO - everything about this
//                 Profile new_profile = ProfileController
//                         .getInstance()
//                         .getProfile(profileString);
//
//                 if (new_profile == null) {
//                     new_profile = ProfileController
//                             .getInstance()
//                             .addProfile(profileString);
//                 }
//
//                 if (new_profile == null) {
//                     // TODO: give error feedback to user
//                 }
//
//                 ProfileController.getInstance().setCurrentProfile(new_profile);
             }
             entering_masterPassword = !entering_masterPassword;

        default:
            char code = (char)primaryCode;
            if(Character.isLetter(code) && currentlyCapslock){
                code = Character.toUpperCase(code);
            }

            if (entering_masterPassword) {
                masterPasswordString += code;
            } else {
                profileString += code;
            }

        }
        updateCandidates();
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

    public void pickSuggestionManually(int index) {
        if (completions != null && index >= 0 && index < completions.length) {
            CompletionInfo ci = completions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (candidateView != null) {
                // TODO
//                candidateView.clear();
            }

            currentlyCapslock = false;

        } else {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.

            if (entering_masterPassword) {
                // user has entered the masterpassword
                // put in the encrypted string
//                String password = ProfileController
//                        .getInstance()
//                        .getCurrentProfile()
//                        .getPasswordHash(masterPasswordString);
//
//                InputConnection ic = getCurrentInputConnection();
//                ic.commitText(password, password.length() + 1);
//
//                masterPasswordString = "";
//                profileString = "";
            } else {
                // user has finished entering the key word
                // TODO - everything about this
//                Profile new_profile = ProfileController
//                        .getInstance()
//                        .getProfile(profileString);
//
//                if (new_profile == null) {
//                    new_profile = ProfileController
//                            .getInstance()
//                            .addProfile(profileString);
//                }
//
//                if (new_profile == null) {
//                    // TODO: give error feedback to user
//                }
//
//                ProfileController.getInstance().setCurrentProfile(new_profile);
            }
            entering_masterPassword = !entering_masterPassword;
        }

    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        completions = completions;
        if (completions == null) {
            setSuggestions(null, false, false);
            return;
        }

        List<String> stringList = new ArrayList<String>();
        for (int i = 0; i < completions.length; i++) {
            CompletionInfo ci = completions[i];
            if (ci != null) stringList.add(ci.getText().toString());
        }
        setSuggestions(stringList, true, true);
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
//        if (suggestions != null && suggestions.size() > 0) {
//            setCandidatesViewShown(true);
//        } else if (isExtractViewShown()) {
//            setCandidatesViewShown(true);
//        }
//        if (candidateView != null) {
            // TODO
//            candidateView.setSuggestions(suggestions, completions, typedWordValid);
//        }
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
        profileString = profile.getName();
    }
}
