package ru.alemakave.slib.utils;

import org.xml.sax.SAXException;
import ru.alemakave.slib.file.pom.POM;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static ru.alemakave.slib.utils.OS.sep;

public class LibUtils {
    private static final HashMap<String, String> defaultRepositories = new HashMap<>();
    private final HashMap<String, String> repositories = new HashMap<>();
    private final String libDir;
    private final Collection<Lib> libs;
    private boolean installWithoutDependency;

    public LibUtils(String libDir, Collection<Lib> libs) {
        this.libDir = libDir;
        this.libs = libs;
        repositories.putAll(defaultRepositories);
    }

    public void addRepository(String repositoryName, String repositoryUrl) {
        repositories.put(repositoryName, repositoryUrl);
    }

    public boolean checkPom(Lib lib) {
        File pomFile = new File(buildPathToFile(lib) + ".pom");
        return pomFile.exists();
    }

    public boolean checkJar(Lib lib) {
        File jarFile = new File(buildPathToFile(lib) + ".jar");
        return jarFile.exists();
    }

    public boolean installLibs() {
        boolean isAllInstalled = true;
        for (Lib lib : libs) {
            boolean isInstalled = installLib(lib);
            String libType = "lib";
            if (lib instanceof IntegratedJarLib) {
                libType = "integrated lib";
            } else if (lib instanceof IntegratedNativeLib) {
                libType = "integrated native lib";
            }
            Logger.infoF("Check install %s: %s is %s\n", libType, lib.toString(), isInstalled ? "installed" : "not installed");
            if (!isInstalled)
                Logger.errorF("Check install %s: %s is not installed\n", libType, lib.toString());
            isAllInstalled &= isInstalled;
        }

        return isAllInstalled;
    }

    public boolean installLibsWithoutDependency() {
        installWithoutDependency = true;
        boolean result = installLibs();
        installWithoutDependency = false;
        return result;
    }

    public boolean installLib(Lib lib) {
        boolean isInstallLib;
        if (lib == null)
            return false;

        if (lib instanceof IntegratedLib) {
            isInstallLib = extractLib((IntegratedLib)lib) != -1;
        } else {
            isInstallLib = downloadLib(lib) != DownloadStatus.NOT_FOUND;
        }

        return isInstallLib;
    }

    public DownloadStatus downloadLib(Lib lib) {
        int numberRepository = 0;
        String filepath = buildPathToFile(lib);

        if (!installWithoutDependency && !checkPom(lib)) {
            for (numberRepository = 0; numberRepository < repositories.size(); numberRepository++) {
                Downloader loader = new Downloader(buildDownloadUrl(lib, numberRepository) + ".pom");
                loader.setAutoCreateTreeDirs(true);
                if (loader.download(filepath + ".pom") != DownloadStatus.NOT_FOUND) break;
            }
        }
        try {
            if (!installWithoutDependency) {
                POM pom = new POM(filepath + ".pom", this);
                List<Lib> deps = pom.getDependencies();
                for (Lib dep : deps) {
                    Logger.debug("Founded dependency: " + dep.toString());
                }
                LibUtils lu = new LibUtils(libDir, deps);
                lu.installLibs();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        if (!checkJar(lib)) {
            Downloader loader = new Downloader(buildDownloadUrl(lib, numberRepository) + ".jar");
            loader.setAutoCreateTreeDirs(true);
            return loader.download(filepath + ".jar");
        }

        return DownloadStatus.EXISTS;
    }

    public byte extractLib(IntegratedLib lib) {
        try {
            String libPath = buildLibPath(lib);
            if (lib instanceof IntegratedJarLib) {
                libPath += ".jar";
            } else if (lib instanceof IntegratedNativeLib) {
                switch (OS.OperationSystem.current()) {
                    case WINDOWS:
                        libPath += ".dll";
                        break;
                    case LINUX:
                        libPath += ".so";
                        break;
                }
            }
            File extractedFile = new File(libDir, libPath);
            if (extractedFile.exists()) {
                return 1;
            }
            Logger.info("Extract: " + "libs/" + libPath);
            InputStream libInputStream = getClass().getProtectionDomain().getClassLoader().getResourceAsStream("libs/" + libPath);
            if (libInputStream != null) {
                //noinspection ResultOfMethodCallIgnored
                extractedFile.getParentFile().mkdirs();
                byte[] libData = libInputStream.readAllBytes();
                FileOutputStream libOutputStream = new FileOutputStream(extractedFile);
                libOutputStream.write(libData);
                libOutputStream.close();
                libInputStream.close();
                Logger.info("Saved to: " + extractedFile.getAbsolutePath());
            } else {
                Logger.errorF("Lib input stream for \"%s\" is null", libPath);
                return -1;
            }

            return 0;
        } catch (IOException e) {
            Logger.error("IOException");
            Logger.error(e);
            return -1;
        }
    }

    public String buildLibPath(Lib lib) {
        //return lib.getGroupID().replaceAll("\\.", "/") + "/" + lib.getArtefactID() + "/" + lib.getVersion() + "/" + lib.getArtefactID() + "-" + lib.getVersion() + (lib.getArtifactSuffix() != null ? ("-" + lib.getArtifactSuffix()) : "");
        StringBuilder result = new StringBuilder();

        result.append(lib.getGroupID().replaceAll("\\.", "/"));
        result.append("/");
        result.append(lib.getArtefactID());
        result.append("/");
        result.append(lib.getVersion());
        result.append("/");
        result.append(String.format("%s-%s", lib.getArtefactID(), lib.getVersion()));
        if (lib.getArtifactSuffix() != null) {
            result.append("-");
            result.append(lib.getArtifactSuffix());
        }
        if (lib instanceof IntegratedNativeLib) {
            result.append("-");
            result.append(((IntegratedNativeLib) lib).getSystemBitTypeAsString());
        }

        return result.toString();
    }

    public String buildDownloadUrl(Lib lib, int numberRepository) {
        String libPath = buildLibPath(lib);
        return repositories.get(repositories.keySet().toArray()[numberRepository].toString()) + libPath;
    }

    public String buildPathToFile(Lib lib) {
        return libDir + sep + buildLibPath(lib).replace("/", sep);
    }

    public File getMavenMetaDataFile(String groupID, String artifactID) {
        String tmpDir = libDir.substring(0, libDir.lastIndexOf(sep)) + sep + ".maven-metadata";
        if (!new File(tmpDir).exists()) //noinspection ResultOfMethodCallIgnored
            new File(tmpDir).mkdirs();
        for (String rep : defaultRepositories.values()) {
            Downloader loader = new Downloader(rep + groupID.replaceAll("\\.", "/") + "/" + artifactID + "/maven-metadata.xml");
            File metadata = new File(tmpDir + sep + artifactID + ".maven-metadata.xml");
            DownloadStatus key = loader.download(metadata);
            if (key != DownloadStatus.NOT_FOUND) return metadata;
        }

        return null;
    }

    public String getNativeDirsCommandPart() {
        StringBuilder result = new StringBuilder();

        for (Lib lib : libs) {
            String path = buildPathToFile(lib);
            File libFile = new File(path + ".dll");
            if (libFile.exists()) {
                result.append("\"");
                result.append(libFile.getParentFile().getAbsolutePath());
                result.append("\"");
                result.append(File.pathSeparator);
            }
        }
        result.append(System.getProperty("java.library.path"));

        return result.toString();
    }

    public String getClassPathCommandPart() {
        StringBuilder result = new StringBuilder();

        for (Lib lib : libs) {
            String path = buildPathToFile(lib);
            if (new File(path + ".jar").exists()) {
                result.append(path);
                result.append(".jar");
                result.append(File.pathSeparator);
            }
        }
        result.append(System.getProperty("java.class.path"));

        return result.toString();
    }

    static {
        defaultRepositories.put("Maven", "https://repo1.maven.org/maven2/");
        defaultRepositories.put("fusesource", "https://repo.fusesource.com/nexus/content/repositories/releases-3rd-party/");
    }
}
