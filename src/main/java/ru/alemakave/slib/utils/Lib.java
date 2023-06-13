package ru.alemakave.slib.utils;

public class Lib {
    private String groupID;
    private String artifactID;
    private String version;
    private String artifactSuffix;

    public Lib(String libData) {
        this(libData.split(":"));
    }

    public Lib(String[] libData) {
        this(libData[0], libData[1], libData[2]);
        if (libData.length > 3) artifactSuffix = libData[3];
    }

    public Lib(String groupID, String artifactID, String version) {
        this(groupID, artifactID, version, null);
    }

    public Lib(String groupID, String artifactID, String version, String artifactSuffix) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
        this.artifactSuffix = artifactSuffix;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtefactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    public String getArtifactSuffix() {
        return artifactSuffix;
    }

    @Override
    public boolean equals(Object lib) {
        if (!(lib instanceof Lib)) return false;
        Lib l = (Lib)lib;
        return toString().equalsIgnoreCase(l.toString());
    }

    @Override
    public String toString() {
        String string;
        if (getArtifactSuffix() != null)
            string = String.format("%s:%s:%s:%s", getGroupID(), getArtefactID(), getVersion(), getArtifactSuffix());
        else
            string = String.format("%s:%s:%s", getGroupID(), getArtefactID(), getVersion());
        return string;
    }
}
