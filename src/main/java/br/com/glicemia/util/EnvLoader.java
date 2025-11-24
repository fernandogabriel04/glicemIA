package br.com.glicemia.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static final Map<String, String> env = new HashMap<>();
    private static boolean loaded = false;

    static {
        loadEnv();
    }

    private static void loadEnv() {
        if (loaded) return;

        String envPath = Paths.get(System.getProperty("user.dir"), ".env").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(envPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    env.put(key, value);
                }
            }
            loaded = true;
        } catch (IOException e) {
            System.err.println("Aviso: Arquivo .env não encontrado. Usando database.properties como fallback.");
        }
    }

    public static String get(String key) {
        return env.getOrDefault(key, System.getenv(key));
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
