package com.taobao.arthas.mvel.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Self-extracting launcher for the arthas-mvel fork.
 *
 * <p>This is the {@code Main-Class} of the single-file {@code arthas-mvel.jar}. The whole Arthas
 * distribution (agent / core / spy / client / async-profiler / the {@code mvel} command plugin)
 * is bundled inside this jar under the {@code arthas-home/} prefix. On first run it is extracted
 * to {@code ~/.arthas-mvel/<version>/arthas}, and then the standard arthas
 * {@link com.taobao.arthas.boot.Bootstrap} is invoked against that directory.
 *
 * <p>So a user only needs this one file — no separate distribution, no network — to run:
 * <pre>java -jar arthas-mvel.jar &lt;pid&gt;</pre>
 *
 * @author xhinliang
 */
public final class ArthasMvelLauncher {

    private static final String HOME_PREFIX = "arthas-home/";
    /** A file that must exist for an extracted home to be considered complete. */
    private static final String MARKER = "arthas-core.jar";

    private ArthasMvelLauncher() {
    }

    public static void main(String[] args) throws Exception {
        File home = prepareHome();
        List<String> newArgs = new ArrayList<String>();
        // If the caller already passed --arthas-home, respect it and don't override.
        if (!hasArthasHome(args)) {
            newArgs.add("--arthas-home");
            newArgs.add(home.getAbsolutePath());
        }
        for (String arg : args) {
            newArgs.add(arg);
        }
        com.taobao.arthas.boot.Bootstrap.main(newArgs.toArray(new String[0]));
    }

    private static boolean hasArthasHome(String[] args) {
        for (String arg : args) {
            if ("--arthas-home".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static File prepareHome() throws IOException {
        String version = readVersion();
        File home = new File(System.getProperty("user.home"),
                ".arthas-mvel" + File.separator + version + File.separator + "arthas");
        if (new File(home, MARKER).isFile()) {
            return home; // already extracted for this version
        }
        if (!home.exists() && !home.mkdirs()) {
            throw new IOException("Cannot create arthas-mvel home: " + home.getAbsolutePath());
        }
        System.out.println("[arthas-mvel] extracting bundled Arthas to " + home.getAbsolutePath() + " ...");
        extractBundledHome(home);
        return home;
    }

    private static void extractBundledHome(File targetDir) throws IOException {
        File self = locateSelfJar();
        ZipFile zip = new ZipFile(self);
        try {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(HOME_PREFIX) || name.equals(HOME_PREFIX)) {
                    continue;
                }
                String rel = name.substring(HOME_PREFIX.length());
                File out = new File(targetDir, rel);
                if (entry.isDirectory()) {
                    out.mkdirs();
                    continue;
                }
                File parent = out.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                InputStream in = zip.getInputStream(entry);
                try {
                    copy(in, out);
                } finally {
                    in.close();
                }
            }
        } finally {
            zip.close();
        }
    }

    private static File locateSelfJar() throws IOException {
        URL location = ArthasMvelLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        if (location == null) {
            throw new IOException("Cannot locate arthas-mvel.jar (no code source)");
        }
        File file;
        try {
            file = new File(location.toURI().getSchemeSpecificPart());
        } catch (Exception e) {
            file = new File(location.getPath());
        }
        if (!file.isFile()) {
            throw new IOException("arthas-mvel must be run as a jar (java -jar arthas-mvel.jar), but code source is: "
                    + file.getAbsolutePath());
        }
        return file;
    }

    private static String readVersion() {
        InputStream in = ArthasMvelLauncher.class.getResourceAsStream("/arthas-mvel-launcher.properties");
        if (in != null) {
            try {
                Properties props = new Properties();
                props.load(in);
                String v = props.getProperty("version");
                if (v != null && !v.trim().isEmpty()) {
                    return v.trim();
                }
            } catch (IOException ignored) {
                // fall through
            } finally {
                try {
                    in.close();
                } catch (IOException ignored) {
                    // ignore
                }
            }
        }
        return "unknown";
    }

    private static void copy(InputStream in, File out) throws IOException {
        OutputStream os = new FileOutputStream(out);
        try {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                os.write(buf, 0, n);
            }
        } finally {
            os.close();
        }
    }
}
