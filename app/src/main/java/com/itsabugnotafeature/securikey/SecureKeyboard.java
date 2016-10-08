package com.itsabugnotafeature.securikey;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

/**
 * Created by alex on 2016/10/08.
 *
 * Based on https://code.tutsplus.com/tutorials/create-a-custom-keyboard-on-android--cms-22615
 */

public class SecureKeyboard
        extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean currentlyCapslock = false;

    private boolean entering_masterPassword = false;
    private String profileString = "";
    private String masterPasswordString = "";

    private ProfileController profileController = null;

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

                 try {
                     // TODO this needs to be done
                     String password = profileController.getCurrentProfile().getHash(masterPasswordString);

                     ic.commitText(password, password.length() + 1);

                     masterPasswordString = "";
                     profileString = "";
                 } catch (Exception excp) {
                     // TODO
                 }
             } else {
                 // user has finished entering the key word
                 // TODO - everything about this
                 Profile new_profile = profileController.getProfile(profileString);

                 if (new_profile == null) {
                     new_profile = profileController.addNewProfile(profileString);
                 }

                 profileController.setProfile(new_profile);
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

        profileController = new ProfileController();

        return kv;
    }

}
