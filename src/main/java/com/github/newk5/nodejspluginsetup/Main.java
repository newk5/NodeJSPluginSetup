package com.github.newk5.nodejspluginsetup;

import com.github.newk5.nodejspluginsetup.util.FileResourceUtils;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java.util.logging.LogManager;
import java.util.stream.Collectors;

public class Main {

    static boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, InterruptedException, ExecutionException, IOException {
        String currentDir = System.getProperty("user.dir");
        FileResourceUtils resources = new FileResourceUtils();

        String jarPath = currentDir + File.separator + "nodejs-vcmp-plugin.jar";
        LogManager.getLogManager().reset();

        System.setProperty("line.separator", isWin ? "\r\n" : "\n");
        File src = new File("src");
        File node_modules = new File("node_modules");

        if (!src.exists() || src.isFile()) {
            src.mkdirs();
        }
        if (!node_modules.exists() || node_modules.isFile()) {
            node_modules.mkdirs();
        }
        File main = new File(src, "main.js");
        if (!main.exists() || main.isDirectory()) {
            Files.write(main.toPath(), resources.readResource("main.js").getBytes());
        }
        File serverCfg = new File("server.cfg");
        if (!serverCfg.exists() || serverCfg.isDirectory()) {
            writeFile(
                    "server.cfg",
                    "gamemode Default,plugins libjavapluginrel64,port 8192,maxplayers 100".split(",")
            );
        }
        File javapluginProps = new File("javaplugin.properties");
        if (!javapluginProps.exists() || javapluginProps.isDirectory()) {
            writeFile(
                    "javaplugin.properties",
                    ("jvmLibPath=" + getLibjvmDir() + ",mainEventsClass=com.github.newk5.vcmp.nodejs.plugin.ServerEventHandler,classPath=" + jarPath + ",maxMemory=12m").split(",")
            );
        }

        Gson gson = new Gson();
        new DownloadResource(gson)
                .setUrl("https://api.github.com/repos/newk5/vcmp-java-plugin/releases/latest")
                .setFilename(isWin ? "libjavapluginrel64.dll" : "libjavapluginrel64.so")
                .setSaveDir("plugins")
                .onComplete(() -> {
                    System.out.println("");
                    new DownloadResource(gson)
                            .setUrl("https://api.github.com/repos/newk5/vcmp-nodejs-plugin/releases/latest")
                            .setFilename("nodejs-vcmp-plugin.jar")
                            .onComplete(() -> {
                                System.out.println("");
                                new DownloadResource(gson)
                                        .setUrl("https://api.github.com/repos/newk5/vcmp-server-mirror/releases/latest")
                                        .setFilename(isWin ? "server64.exe" : "mpsvrrel64")
                                        .onComplete(() -> {
                                            System.out.println("");
                                            System.out.println("Finished all downloads");
                                            System.exit(0);
                                        }).start();

                            }).start();
                })
                .start();

        // Start downloading
    }

    public static String getLibjvmDir() {
        String javaHome = System.getProperty("java.home");
        File f = new File(javaHome);
        File generalJavaFolder = f.getParentFile().getParentFile();

        List< File> javaFolders = Arrays
                .stream(generalJavaFolder.listFiles())
                .filter(folder -> (folder.getName().contains("java-8") || folder.getName().contains("jdk-8") || folder.getName().contains("java-1.8")) && folder.isDirectory())
                .collect(Collectors.toList());

        for (File java8Folder : javaFolders) {

            if (java8Folder != null) {
                File jvmBinary = java8Folder;
                jvmBinary = new File(jvmBinary, "jre");
                jvmBinary = new File(jvmBinary, "bin");
                jvmBinary = new File(jvmBinary, "server");
                jvmBinary = new File(jvmBinary, isWin ? "jvm.dll" : "libjvm.so");

                if (jvmBinary.exists() && jvmBinary.isFile()) {
                    return jvmBinary.getAbsolutePath();
                } else {
                    //failed to find binary, searching in alternative directory...
                    jvmBinary = java8Folder;
                    jvmBinary = new File(jvmBinary, "jre");
                    jvmBinary = new File(jvmBinary, "lib");
                    jvmBinary = new File(jvmBinary, "amd64");
                    jvmBinary = new File(jvmBinary, "server");
                    jvmBinary = new File(jvmBinary, isWin ? "jvm.dll" : "libjvm.so");
                    if (jvmBinary.exists() && jvmBinary.isFile()) {
                        return jvmBinary.getAbsolutePath();
                    }
                }
            }
        }
        return "specify path to " + (isWin ? "jvm.dll" : "libjvm.so") + " here";

    }

    public static void writeFile(String file, String[] lines) throws IOException {
        File fout = new File(file);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < lines.length; i++) {
            bw.write(lines[i]);
            bw.newLine();
        }

        bw.close();
    }

}
