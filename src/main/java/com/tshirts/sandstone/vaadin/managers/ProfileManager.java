package com.tshirts.sandstone.vaadin.managers;

import com.google.gson.Gson;
import com.tshirts.sandstone.vaadin.util.Profile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProfileManager implements Closeable {
    static ArrayList<Profile> profiles;
    private static ProfileManager instance = null;


    private ProfileManager() {
        profiles = new ArrayList<>(List.of(readFromFile()));
    }

    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }

    public static Profile[] findBy(String type, String value) {
        Stream<Profile> profileStream = profiles.stream();
        return switch (type) {
            case "username" ->
                    profileStream.filter(profile -> profile.getUsername().toLowerCase().contains(value.toLowerCase())).toArray(Profile[]::new);
            case "email" ->
                    profileStream.filter(profile -> profile.getEmail().toLowerCase().contains(value.toLowerCase())).toArray(Profile[]::new);
            case "id" ->
                    profileStream.filter(profile -> String.valueOf(profile.getProfileId()).contains(value.toLowerCase())).toArray(Profile[]::new);
            case "firstName" ->
                    profileStream.filter(profile -> profile.getFirstName().toLowerCase().contains(value.toLowerCase())).toArray(Profile[]::new);
            case "lastName" ->
                    profileStream.filter(profile -> profile.getLastName().toLowerCase().contains(value.toLowerCase())).toArray(Profile[]::new);
            case "phoneNumber" ->
                    profileStream.filter(profile -> profile.getPhone().toLowerCase().contains(value.toLowerCase())).toArray(Profile[]::new);
            case "permissionLevel" ->
                    profileStream.filter(profile -> profile.getPermissionLevel().toString().toLowerCase().contains(value.toLowerCase())).toArray(Profile[]::new);
            default -> null;
        };
    }

    public boolean writeToFile() {
        Gson gson = new Gson();
        // To array
        Profile[] profileArray = new Profile[profiles.size()];
        profileArray = profiles.toArray(profileArray);
        // To json
        String json = gson.toJson(profileArray);
        // Write to file
        try (FileWriter file = new FileWriter("src/main/resources/profiles.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Profile[] readFromFile() {
        Gson gson = new Gson();
        Profile[] profileArray = null;
        try (FileReader file = new FileReader("src/main/resources/profiles.json")) {
            // Read the whole file
            BufferedReader reader = new BufferedReader(file);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            // Parse the json
            profileArray = gson.fromJson(builder.toString(), Profile[].class);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return profileArray;
    }

    public boolean addProfile(Profile profile) {
        return profiles.add(profile);
    }

    public boolean removeProfile(Profile profile) {
        return profiles.remove(profile);
    }

    public Profile getProfile(int profileId) {
        for (Profile profile : profiles) {
            if (profile.getProfileId() == profileId) {
                return profile;
            }
        }
        return null;
    }

    public Profile getProfile(String email) {
        for (Profile profile : profiles) {
            if (profile.getEmail().equals(email)) {
                return profile;
            }
        }
        return null;
    }

    public Profile getProfile(String usernameOrEmail, String password) {
        for (Profile profile : profiles) {
            if ((profile.getUsername().equals(usernameOrEmail) || profile.getEmail().equals(usernameOrEmail)) && profile.getPassword().equals(password.hashCode() + "")) {
                return profile;
            }
        }
        return null;
    }

    public ArrayList<Profile> getProfiles() {
        return profiles;
    }


    @Override
    public void close() throws IOException {
        if (writeToFile()) {
            System.out.println("Profiles saved to file");
            instance = null;
        } else {
            throw new IOException("Failed to save profiles to file");
        }
    }
}
