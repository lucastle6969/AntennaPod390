package de.danoeh.antennapod.core.service;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

public class OKHttpUtils {

    public static String responseToString(Response response) throws NullPointerException, IOException {
        String resultString = Objects.requireNonNull(response.body()).string();
        return resultString;
    }
}
