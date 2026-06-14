package com.avaliacao.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLog {

    private static final String CAMINHO = System.getProperty("java.io.tmpdir") + "/debug-avaliacao.log";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static synchronized void log(String msg) {
        System.out.println("[DEBUG] " + msg);
        try {
            Path path = Paths.get(CAMINHO);
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            try (PrintWriter out = new PrintWriter(new FileWriter(path.toFile(), true))) {
                out.println(DTF.format(LocalDateTime.now()) + " " + msg);
            }
        } catch (IOException e) {
            System.err.println("DebugLog erro arquivo: " + e.getMessage());
        }
    }

    public static synchronized void log(String msg, Throwable t) {
        System.out.println("[DEBUG] " + msg);
        t.printStackTrace(System.out);
        try {
            Path path = Paths.get(CAMINHO);
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            try (PrintWriter out = new PrintWriter(new FileWriter(path.toFile(), true))) {
                out.println(DTF.format(LocalDateTime.now()) + " " + msg);
                t.printStackTrace(out);
            }
        } catch (IOException e) {
            System.err.println("DebugLog erro arquivo: " + e.getMessage());
        }
    }
}
