/**
 * Created by alex on 2016/10/08.
 */

package com.itsabugnotafeature.securikey.models;

import android.support.v4.util.Pair;

import com.itsabugnotafeature.securikey.profiles.DefaultProfile;
import com.itsabugnotafeature.securikey.profiles.Profile;
import com.itsabugnotafeature.securikey.utils.TextUtil.EmptyStringException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProfileController {
    private static final String LOG_TAG = "ProfileController";

    public interface ProfileSuggestionListener {
        void onNewProfileSuggestions(String matchedText, List<Profile> profiles);
        void onNewProfileCreated(Profile profile);
        void onProfileClickedProfile(Profile profile);
    }

    private static final ProfileController INSTANCE = new ProfileController();

    private final Map<String, Profile> profileMap = new HashMap<>();

    private ProfileSuggestionListener listener;
    private String suggestionMatchingText;

    private ProfileController() {
        suggestionMatchingText = "";
        loadProfilesFromStorage();
    }

    public static ProfileController getInstance() {
        return INSTANCE;
    }

    /**
     * Load profiles from storage
     */
    private void loadProfilesFromStorage() {

        // TODO: initialise profiles from storage
        try {
            DefaultProfile testProfileOne = new DefaultProfile("facebook");
            DefaultProfile testProfileTwo = new DefaultProfile("imgur");
            DefaultProfile testProfileThree = new DefaultProfile("reddit");

            profileMap.put(testProfileOne.getName(), testProfileOne);
            profileMap.put(testProfileTwo.getName(), testProfileTwo);
            profileMap.put(testProfileThree.getName(), testProfileThree);
        } catch (EmptyStringException e) { }
    }

    public ProfileSuggestionListener getListener() {
        return listener;
    }

    public void setListener(ProfileSuggestionListener listener) {
        this.listener = listener;

        broadcastMatchingProfiles(suggestionMatchingText);
    }

    public void setSuggestionMatchingText(String textToMatch) {
        if (textToMatch != null) {
            suggestionMatchingText = textToMatch;
        } else {
            suggestionMatchingText = "";
        }

        broadcastMatchingProfiles(suggestionMatchingText);
    }

    public Pair<String, List<Profile>> getProfilesMatchingSuggestionText() {
        return Pair.create(suggestionMatchingText,
                getProfilesMatchingTextInternal(suggestionMatchingText));
    }

    public void onProfileSelected(Profile profile) {
        if (profile == null) {
            return;
        }

        if (listener != null) {
            listener.onProfileClickedProfile(profile);
        }
    }

    public void createNewProfile(String profileName) {
        try {
            DefaultProfile profile = new DefaultProfile(profileName);
            if (profileMap.containsKey(profile.getName())) {
                return;
            }
            profileMap.put(profile.getName(), profile);

            suggestionMatchingText = "";

            if (listener != null) {
                List<Profile> matchedProfiles = getProfilesMatchingTextInternal(suggestionMatchingText);
                listener.onNewProfileCreated(profile);
                listener.onNewProfileSuggestions(suggestionMatchingText, matchedProfiles);
            }
        } catch (EmptyStringException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private void broadcastMatchingProfiles(String textToMatch) {
        if (listener != null) {
            List<Profile> matchedProfiles = getProfilesMatchingTextInternal(textToMatch);
            listener.onNewProfileSuggestions(textToMatch, matchedProfiles);
        }
    }

    private List<Profile> getProfilesMatchingTextInternal(String textToMatch) {
        if (textToMatch.equals("")) {
            return new LinkedList<>(profileMap.values());
        }

        // TODO - fuzzy matching?
        List<Profile> matchedProfiles = new LinkedList<>();

        for (Profile profile : profileMap.values()) {
            if (profile.getName().contains(textToMatch)) {
                matchedProfiles.add(profile);
            }
        }

        return matchedProfiles;
    }
}
