package ru.alemakave.slib.utils;

import org.xml.sax.SAXException;
import ru.alemakave.slib.file.pom.POM;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;

import static ru.alemakave.slib.utils.OS.sep;

public class LibUtils {
    private static final HashMap<String, String> defaultRepositories = new HashMap<>();
    private final HashMap<String, String> repositories = new HashMap<>();
    private final String libDir;
    private final Collection<Lib> libs;
    private boolean installWithoutDependency;

    private static final List<String> paths = new ArrayList<>();

    public LibUtils(String libDir, Collection<Lib> libs) {
        this.libDir = libDir;
        this.libs = libs;
        repositories.putAll(defaultRepositories);
    }

    public void addRepository(String repositoryName, String repositoryUrl) {
        repositories.put(repositoryName, repositoryUrl);
    }

    public void addRepository(Map<String, String> repository) {
        repositories.putAll(repository);
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
            Logger.infoF("Check install lib: %s is %s\n", lib.toString(), isInstalled ? "installed" : "not installed");
            if (!isInstalled)
                Logger.errorF("Check install lib: %s is not installed\n", lib.toString());
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

    public boolean installLibWithoutDependency(Lib lib) {
        installWithoutDependency = true;
        boolean result = installLib(lib);
        installWithoutDependency = false;
        return result;
    }

    public boolean installLib(Lib lib) {
        boolean isInstallLib = downloadLib(lib) != -1;

        String path = buildPathToFile(lib);

        //if (OS.getJavaVersion().equals("14.0.2")) {
            if (new File(path + ".jar").exists())
                paths.add(path + ".jar");
            if (new File(path + ".so").exists())
                paths.add(path + ".so");
        //}
        //else {
        //    if (new File(path + ".jar").exists())
        //        isInstallLib &= loadClass(path + ".jar");
        //    if (new File(path + ".so").exists())
        //        isInstallLib &= loadNative(new File(path + ".so"));
        //}
        return isInstallLib;
    }

    public byte downloadLib(Lib lib) {
        int numberRepository = 0;
        String filepath = buildPathToFile(lib);

        if (!installWithoutDependency && !checkPom(lib)) {
            for (numberRepository = 0; numberRepository < repositories.size(); numberRepository++) {
                Downloader loader = new Downloader(buildDownloadUrl(lib, numberRepository) + ".pom");
                loader.setAutoCreateTreeDirs(true);
                if (loader.download(filepath + ".pom") != -1) break;
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

        return 1;
    }

    public String buildDownloadUrl(Lib lib, int numberRepository) {
        String libPath = lib.getGroupID().replaceAll("\\.", "/") + "/" + lib.getArtefactID() + "/" + lib.getVersion() + "/" + lib.getArtefactID() + "-" + lib.getVersion() + (lib.getArtifactSuffix() != null ? ("-" + lib.getArtifactSuffix()) : "");
        return repositories.get(repositories.keySet().toArray()[numberRepository].toString()) + libPath;
    }

    public String buildPathToFile(Lib lib) {
        StringBuilder pathToFile = new StringBuilder(libDir + sep);
        for (String s : lib.getGroupID().split("\\.")) {
            pathToFile.append(s).append(sep);
        }
        pathToFile.append(lib.getArtefactID()).append(sep);
        pathToFile.append(lib.getVersion()).append(sep);
        pathToFile.append(lib.getArtefactID()).append("-").append(lib.getVersion()).append((lib.getArtifactSuffix() != null ? ("-" + lib.getArtifactSuffix()) : ""));

        return pathToFile.toString();
    }

    @Deprecated
    private boolean loadClass(String pathToJar) {
        ClassLoader sysloader = ClassLoader.getSystemClassLoader();
        Logger.debug("System class loader: " + sysloader.getClass().getName());
        try {
            if (sysloader instanceof URLClassLoader) {
                URL u = new File(pathToJar).toURI().toURL();
                Class<?> sysclass = URLClassLoader.class;
                Method method = sysclass.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(sysloader, u);
            }
        } catch (Exception e) {
            Logger.fatal(e);
            try {
                throw new IOException("Error, could not add URL to system classloader");
            } catch (IOException e1) {
                Logger.error(e1);
                return false;
            }
        }
        return true;
    }

    private boolean loadNative(File nativeFile) {
        try {
            System.load(nativeFile.getCanonicalPath());
            return true;
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }
    }

    public File getMavenMetaDataFile(String groupID, String artifactID) {
        String tmpDir = libDir.substring(0, libDir.lastIndexOf(sep)) + sep + ".maven-metadata";
        if (!new File(tmpDir).exists()) //noinspection ResultOfMethodCallIgnored
            new File(tmpDir).mkdirs();
        for (String rep : defaultRepositories.values()) {
            Downloader loader = new Downloader(rep + groupID.replaceAll("\\.", "/") + "/" + artifactID + "/maven-metadata.xml");
            File metadata = new File(tmpDir + sep + artifactID + ".maven-metadata.xml");
            byte key = loader.download(metadata);
            if (key != -1) return metadata;
        }

        return null;
    }

    public String getClassPathCommandPart() {
        StringBuilder result = new StringBuilder();

        for (String path : paths) {
            result.append(path);
            if (OS.OperationSystem.current() == OS.OperationSystem.WINDOWS)
                result.append(";");
            else
                result.append(":");
        }
        result.append(System.getProperty("java.class.path"));

        return result.toString();
    }

    public JarFile getJarFile(Lib lib) throws IOException {
        return new JarFile(buildPathToFile(lib) + ".jar");
    }

    public void clear() {
        paths.clear();
    }

    static {
        defaultRepositories.put("Maven", "https://repo1.maven.org/maven2/");
        defaultRepositories.put("fusesource", "https://repo.fusesource.com/nexus/content/repositories/releases-3rd-party/");
    }
}
