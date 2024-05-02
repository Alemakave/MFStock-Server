package ru.alemakave.slib.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.alemakave.slib.utils.version.exception.InvalidVersionFormatException;

import java.io.IOException;

import static ru.alemakave.slib.utils.version.VersionUtils.isValidVersion;

public class UpdateUtils {
    public static boolean checkUpdateFromGradle(String actualVersion, String url) {
        if (!isValidVersion(actualVersion)) {
            throw new InvalidVersionFormatException();
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String data = response.body().string();
            String version = null;
            for (String dataLine : data.split("\n")) {
                if (dataLine.startsWith("version")) {
                    version = dataLine.substring("version".length());
                    version = version.replace("'", "")
                                     .replace("\"", "");
                    break;
                }
            }

            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
