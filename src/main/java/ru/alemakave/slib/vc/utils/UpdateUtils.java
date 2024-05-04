package ru.alemakave.slib.vc.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.alemakave.slib.vc.Version;
import ru.alemakave.slib.vc.exception.InvalidVersionFormatException;

import static ru.alemakave.slib.vc.utils.VersionUtils.isValidVersion;

public class UpdateUtils {
    public static void checkUpdateFromGradle(String actualVersionString, String url, String message) {
        new Thread(() -> {
            if (!isValidVersion(actualVersionString)) {
                throw new InvalidVersionFormatException(actualVersionString);
            }

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() == null) {
                    System.out.println("Failed to get remote version information!");
                    return;
                }

                String data = response.body().string();
                for (String dataLine : data.split("\n")) {
                    if (dataLine.startsWith("version")) {
                        String version = dataLine.substring("version".length());
                        version = version.replace("'", "")
                                .replace("\"", "")
                                .trim();

                        Version actualVersion = Version.parse(actualVersionString);
                        Version remoteVersion = Version.parse(version);
                        if (actualVersion.compareTo(remoteVersion) < 0) {
                            System.out.println(message);
                        }

                        return;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
