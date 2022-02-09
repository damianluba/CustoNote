package com.damian.custonote.data;

import android.os.AsyncTask;

import com.damian.custonote.data.model.LoggedInUser;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    LoggedInUser fakeUser;
    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch(Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private class LoginTask extends AsyncTask<HashMap<String, String>, Void, Void> {

        @Override
        protected Void doInBackground(HashMap<String, String>... hashMaps) {
            fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe");
            return null;
        }
    }
}