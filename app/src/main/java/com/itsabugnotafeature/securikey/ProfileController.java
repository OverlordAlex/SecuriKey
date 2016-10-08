package com.itsabugnotafeature.securikey;

import java.util.ArrayList;

/**
 * Created by alex on 2016/10/08.
 */

public class ProfileController {

    ArrayList<Profile> profiles = new ArrayList<>();
    int current_profile = 0;

    public ProfileController() {
        // TODO - change this over to from storage if possible, otherwise default
        loadDefaultProfiles();
    }

    /**
     * Load profiles from storage
     */
    public void loadProfilesFromStorage() {

    }

    /**
     * Load all the default profiles
     */
    public void loadDefaultProfiles() {
        profiles.add(new DefaultProfile());
    }

    public boolean profileExists(String name) {
        Profile profile = getProfile(name);
        if (profile != null) {
            return true;
        }

        return false;
    }

    public Profile getExactProfile(String name) {
        for (Profile profile : profiles) {
            if (profile.getName().equals(name)) {
                return profile;
            }
        }

        return null;
    }

    public Profile getProfile(String name) {
        // TODO - fuzzy matching?
        for (Profile profile : profiles) {
            if (profile.getName().contains(name)) {
                return profile;
            }
        }

        return null;
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public Profile addNewProfile(Profile profile) {
        addProfile(profile);
        return profile;
    }

    public Profile addNewProfile() {
        Profile profile = new DefaultProfile();
        return addNewProfile(profile);
    }

    public Profile addNewProfile(String name) {
        Profile profile = new DefaultProfile(name);
        return addNewProfile(profile);
    }

    public Profile getCurrentProfile() {
        // TODO - error checking testing
        return profiles.get(current_profile);
    }

    public int setProfile(Profile profile) {
        current_profile = profiles.indexOf(profile);
        return current_profile;
    }

    public ArrayList<String> getMatchingProfiles(String current) {
        // TODO - fuzzy matching?
        ArrayList<String> matched_profiles = new ArrayList<>();

        for (Profile profile : profiles) {
            if (profile.getName().contains(current)) {
                matched_profiles.add(profile.getName());
            }
        }

        return matched_profiles;
    }

}
