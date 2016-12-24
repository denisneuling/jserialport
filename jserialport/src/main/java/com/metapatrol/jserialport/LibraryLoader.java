package com.metapatrol.jserialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
class LibraryLoader {
    private static final String LIBRARY_NAME = "jserialport";

    public static synchronized void load() throws UnsatisfiedLinkError, IllegalStateException, SecurityException {
        LibraryLoader libraryLoader = new LibraryLoader(LIBRARY_NAME);
        if (libraryLoader.trySystemLibraryLoading()) return;
        if (libraryLoader.tryLoadingFromTemporaryFolder()) return;
        String details = libraryLoader.getResult().asFormattedString();
        throw new UnsatisfiedLinkError("Couldn't load native library '" + LIBRARY_NAME + "'. " + details);
    }


    private String libraryName;
    private final LoaderResult loaderResult = new LoaderResult();

    private LibraryLoader(String libraryName){
        this.libraryName = libraryName;
    }

    private boolean trySystemLibraryLoading() {
        try {
            Runtime.getRuntime().loadLibrary(libraryName);
            loaderResult.setLoadedFromSystemLibraryPath(true);
            return true;
        } catch (UnsatisfiedLinkError e) {
            loaderResult.setLoadedFromSystemLibraryPath(false);
        }
        return false;
    }

    private boolean tryLoadingFromTemporaryFolder() throws SecurityException, IllegalStateException {

        String nativeLibName = System.mapLibraryName(libraryName);
        loaderResult.setNativeLibName(nativeLibName);

        File tempFolder;
        try {
            tempFolder = Files.createTempDirectory(libraryName).toFile();
        } catch (IOException e) {
            throw new IllegalStateException("Can't create temporary folder. Make sure you have a temp. folder with write access available.", e);
        }

        File libFile = new File(tempFolder, nativeLibName);
        loaderResult.setTemporaryLibFile(libFile.getAbsolutePath());
        String libNameWithinClasspath = "/lib/" + determineOsArchName() + "/" + nativeLibName;
        loaderResult.setLibNameWithinClasspath(libNameWithinClasspath);
        try {
            loaderResult.setUsedThisClassloader(copyStreamToFile(getClass().getResourceAsStream(libNameWithinClasspath), libFile.toPath()));
            if (!loaderResult.isUsedThisClassloader()) {
                loaderResult.setUsedSystemClassloader(copyStreamToFile(ClassLoader.getSystemClassLoader().getResourceAsStream(libNameWithinClasspath), libFile.toPath()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't write to " + libFile, e);
        }
        libFile.deleteOnExit();

        if (loaderResult.isUsedThisClassloader() || loaderResult.isUsedSystemClassloader()) {
            loaderResult.setMadeReadable(libFile.setReadable(true));
            loaderResult.setMadeExecutable(libFile.setExecutable(true));
            Runtime.getRuntime().load(libFile.getAbsolutePath());
            return true;
        }
        return false;
    }

    private boolean copyStreamToFile(InputStream inputStream, Path targetPath) throws IOException {
        if (inputStream == null) return false;
        Files.copy(inputStream, targetPath);
        inputStream.close();
        return true;
    }

    private LoaderResult getResult() {
        return loaderResult;
    }

    private String determineOsArchName() {
        return determineOS() + "-" + determineArch();
    }

    private String determineOS() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        if (OS.LINUX.matches(osName)) return OS.LINUX.name;
        if (OS.WIN32.matches(osName)) return OS.WIN32.name;
        if (OS.OSX.matches(osName)) return OS.OSX.name;
        return osName;
    }

    private String determineArch() {
        String osArch = System.getProperty("os.arch").toLowerCase(Locale.US);
        if (ARCH.X86_AMD64.matches(osArch)) return ARCH.X86_AMD64.name;
        if (ARCH.X86.matches(osArch)) return ARCH.X86.name;
        if (ARCH.ARM32_VFP_HFLT.matches(osArch)) return ARCH.ARM32_VFP_HFLT.name;
        return osArch;
    }

    private class LoaderResult {
        private Boolean alreadyLoaded = null;
        private Boolean loadedFromSystemLibraryPath = null;
        private String nativeLibName;
        private String libNameWithinClasspath;
        private Boolean usedThisClassloader;
        private Boolean usedSystemClassloader;
        private Boolean madeReadable;
        private Boolean madeExecutable;
        private String temporaryLibFile;

        String asFormattedString() {
            String result = "";
            result += "os.name=\"" + System.getProperty("os.name") + "\"";
            result += ", ";
            result += "os.arch=\"" + System.getProperty("os.arch") + "\"";
            result += ", ";
            result += "os.version=\"" + System.getProperty("os.version") + "\"";
            result += ", ";
            result += "java.vm.name=\"" + System.getProperty("java.vm.name") + "\"";
            result += ", ";
            result += "java.vm.version=\"" + System.getProperty("java.vm.version") + "\"";
            result += ", ";
            result += "java.vm.vendor=\"" + System.getProperty("java.vm.vendor") + "\"";
            result += ", ";
            result += "alreadyLoaded=\"" + alreadyLoaded + "\"";
            if (loadedFromSystemLibraryPath != null) {
                result += ", ";
                result += "loadedFromSystemLibraryPath=\"" + loadedFromSystemLibraryPath + "\"";
            }
            if (nativeLibName != null) {
                result += ", ";
                result += "nativeLibName=\"" + nativeLibName + "\"";
            }
            if (temporaryLibFile != null) {
                result += ", ";
                result += "temporaryLibFile=\"" + temporaryLibFile + "\"";
            }
            if (libNameWithinClasspath != null) {
                result += ", ";
                result += "libNameWithinClasspath=\"" + libNameWithinClasspath + "\"";
            }
            if (usedThisClassloader != null) {
                result += ", ";
                result += "usedThisClassloader=\"" + usedThisClassloader + "\"";
            }
            if (usedSystemClassloader != null) {
                result += ", ";
                result += "usedSystemClassloader=\"" + usedSystemClassloader + "\"";
            }
            if (madeReadable != null) {
                result += ", ";
                result += "madeReadable=\"" + madeReadable + "\"";
            }
            if (madeExecutable != null) {
                result += ", ";
                result += "madeExecutable=\"" + madeExecutable + "\"";
            }
            result += ", ";
            result += "java.library.path=\"" + System.getProperty("java.library.path") + "\"";

            return String.format("[LoaderResult: %s]", result);
        }

        void setAlreadyLoaded(Boolean alreadyLoaded) {
            this.alreadyLoaded = alreadyLoaded;
        }

        void setLoadedFromSystemLibraryPath(Boolean loadedFromSystemLibraryPath) {
            this.loadedFromSystemLibraryPath = loadedFromSystemLibraryPath;
        }

        void setNativeLibName(String nativeLibName) {
            this.nativeLibName = nativeLibName;
        }

        void setLibNameWithinClasspath(String libNameWithinClasspath) {
            this.libNameWithinClasspath = libNameWithinClasspath;
        }

        void setTemporaryLibFile(String temporaryLibFile) {
            this.temporaryLibFile = temporaryLibFile;
        }

        void setUsedThisClassloader(boolean usedThisClassloader) {
            this.usedThisClassloader = usedThisClassloader;
        }

        boolean isUsedThisClassloader() {
            return true == usedThisClassloader;
        }

        void setUsedSystemClassloader(boolean usedSystemClassloader) {
            this.usedSystemClassloader = usedSystemClassloader;
        }

        boolean isUsedSystemClassloader() {
            return true == usedSystemClassloader;
        }

        void setMadeReadable(boolean madeReadable) {
            this.madeReadable = madeReadable;
        }

        void setMadeExecutable(boolean madeExecutable) {
            this.madeExecutable = madeExecutable;
        }
    }

    private enum ARCH {
        ARM32_VFP_HFLT("arm32-vfp-hflt", "arm"),
        X86("x86", "i386", "i486", "i586", "i686", "pentium"),
        X86_AMD64("x86-amd64", "x86_64", "amd64", "em64t", "universal");

        final String name;
        final String[] aliases;

        ARCH(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }

        boolean matches(String aName) {
            for (String alias : aliases) {
                if (aName.contains(alias)) return true;
            }
            return false;
        }
    }

    private enum OS {
        WIN32("win32", "win32", "windows"),
        LINUX("linux", "linux", "unix"),
        OSX("darwin", "darwin", "mac os x", "mac", "osx");

        final String name;
        final String[] aliases;

        OS(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }

        boolean matches(String aName) {
            for (String alias : aliases) {
                if (aName.contains(alias)) return true;
            }
            return false;
        }
    }
}
