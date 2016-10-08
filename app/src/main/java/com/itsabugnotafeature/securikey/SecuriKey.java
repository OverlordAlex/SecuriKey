package com.itsabugnotafeature.securikey;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by alex on 2016/10/08.
 *
 * Based on https://code.tutsplus.com/tutorials/create-a-custom-keyboard-on-android--cms-22615
 */

public class SecuriKey
        extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean caps = false;

    private boolean masterpass = false;
    private String keyString = "";
    private String masterpassString = "";

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
}

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode){
        case Keyboard.KEYCODE_DELETE :
            if (masterpass) {
                if( masterpassString.length() > 0) {
                    masterpassString = masterpassString.substring(0, masterpassString.length() - 1);
                }
            } else {
                if( keyString.length() > 0) {
                    keyString = keyString.substring(0, keyString.length() - 1);
                }
            }
            break;
        case Keyboard.KEYCODE_SHIFT:
            caps = !caps;
            keyboard.setShifted(caps);
            kv.invalidateAllKeys();
            break;
        case Keyboard.KEYCODE_DONE:
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            break;
         case 9999:
             if (masterpass) {
                 // user has entered the masterpassword
                 // put in the encrypted string

                 try {
                     // TODO this needs to be done
                     String password = md5(masterpassString + keyString);

                     ic.commitText(password.toString(), password.length() + 1);

                     masterpassString = "";
                     keyString = "";
                 } catch (Exception excp) {
                     // TODO
                 }
             } else {
                 // user has finished entering the key word

                 // TODO - fetch requirements
             }
             masterpass = !masterpass;
        default:
            char code = (char)primaryCode;
            if(Character.isLetter(code) && caps){
                code = Character.toUpperCase(code);
            }

            if (masterpass) {
                masterpassString += code;
            } else {
                keyString += code;
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
        kv = (KeyboardView)  getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }
}
