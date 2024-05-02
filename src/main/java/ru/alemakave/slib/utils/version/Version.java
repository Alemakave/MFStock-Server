package ru.alemakave.slib.utils.version;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.alemakave.slib.utils.version.exception.VersionParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
@ToString
public class Version {
    private Integer major;
    private Integer minor;
    private Integer patch;
    private String preRelease;
    private String buildMetadata;

    private Version() {}

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    public Version(int major, int minor, int patch, String preRelease) {
        this(major, minor, patch, preRelease, null);
    }

    public Version(int major, int minor, int patch, String preRelease, String buildMetadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.buildMetadata = buildMetadata;
    }

    /**
     * https://semver.org/
     * https://regex101.com/r/vkijKf/1
     * @param version
     * @return
     */
    public static Version parse(String version) {
        final String regex = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(version);

        if (matcher.find()) {
            Version result = new Version();
            result.major = Integer.parseInt(matcher.group(1));
            result.minor = Integer.parseInt(matcher.group(2));
            result.patch = Integer.parseInt(matcher.group(3));
            result.preRelease = matcher.group(4);
            result.buildMetadata = matcher.group(5);

            return result;
        } else {
            throw new VersionParseException(version);
        }
    }
}
