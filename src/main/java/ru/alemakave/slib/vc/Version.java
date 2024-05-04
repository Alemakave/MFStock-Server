package ru.alemakave.slib.vc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import ru.alemakave.slib.vc.exception.VersionParseException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
@ToString
public class Version implements Comparable<Version> {
    /** MAJOR version when you make incompatible API changes */
    private Integer major;
    /** MINOR version when you add functionality in a backward compatible manner */
    private Integer minor;
    /** PATCH version when you make backward compatible bug fixes */
    private Integer patch;
    /** PRERELEASE prerelease modifier */
    private String prerelease;
    /** BUILD_METADATA - build metadata */
    private String buildMetadata;

    private Version() {}

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    public Version(int major, int minor, int patch, String prerelease) {
        this(major, minor, patch, prerelease, null);
    }

    public Version(int major, int minor, int patch, String prerelease, String buildMetadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prerelease = prerelease;
        this.buildMetadata = buildMetadata;
    }

    /**
     * Given a version number MAJOR.MINOR.PATCH, increment the:
     * <pre>
     *   MAJOR version when you make incompatible API changes
     *   MINOR version when you add functionality in a backward compatible manner
     *   PATCH version when you make backward compatible bug fixes
     * </pre>
     * Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.
     * <p>
     *   More information [<a href="https://semver.org/">Semver</a>]
     *   <br>
     *   Example [<a href="https://regex101.com/r/vkijKf/1">Regex101</a>]
     * </p>
     * </pre>
     *
     * @param version
     *        text format version
     *
     * @return   Version object
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
            result.prerelease = matcher.group(4);
            result.buildMetadata = matcher.group(5);

            return result;
        } else {
            throw new VersionParseException(version);
        }
    }

    /**
     * <pre>
     *     MAJOR - Compare as {@link Integer#compareTo(Integer)}
     *     MINOR - Compare as {@link Integer#compareTo(Integer)}
     *     PATCH - Compare as {@link Integer#compareTo(Integer)}
     *     BUILD_METADATA - Compare ignored
     * </pre>
     * Example: 1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-alpha.beta < 1.0.0-beta < 1.0.0-beta.2 < 1.0.0-beta.11 < 1.0.0-rc.1 < 1.0.0
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NotNull Version o) {
        if (!Objects.equals(getMajor(), o.getMajor())) {
            return getMajor().compareTo(o.getMajor());
        }

        if (!Objects.equals(getMinor(), o.getMinor())) {
            return getMinor().compareTo(o.getMinor());
        }

        if (!Objects.equals(getPatch(), o.getPatch())) {
            return getPatch().compareTo(o.getPatch());
        }

        if (getPrerelease() == null && o.getPrerelease() != null) {
            return 1;
        }

        if (getPrerelease() != null && o.getPrerelease() == null) {
            return -1;
        }

        if (getPrerelease() != null && o.getPrerelease() != null) {
            Pattern numericCheckPattern = Pattern.compile("^[0-9]*$");

            String[] thisPreReleaseParts = getPrerelease().split("\\.");
            String[] objPreReleaseParts = o.getPrerelease().split("\\.");

            int compareResult;

            for (int i = 0; i < Math.min(thisPreReleaseParts.length, objPreReleaseParts.length); i++) {
                if (numericCheckPattern.matcher(thisPreReleaseParts[i]).matches() && !numericCheckPattern.matcher(objPreReleaseParts[i]).matches()) {
                    return -1;
                }

                if (numericCheckPattern.matcher(thisPreReleaseParts[i]).matches()) {
                    compareResult = Integer.compare(Integer.parseInt(thisPreReleaseParts[i]), Integer.parseInt(objPreReleaseParts[i]));

                    if (compareResult != 0) {
                        return compareResult;
                    }
                } else {
                    compareResult = thisPreReleaseParts[i].compareTo(objPreReleaseParts[i]);

                    if (compareResult < 0) {
                        compareResult = -1;
                    } else if (compareResult > 0) {
                        compareResult = 1;
                    }

                    if (compareResult != 0) {
                        return compareResult;
                    }
                }
            }

            return Integer.compare(thisPreReleaseParts.length, objPreReleaseParts.length);
        }

        return 0;
    }
}
