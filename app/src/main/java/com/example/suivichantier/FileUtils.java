package com.example.suivichantier;

import java.io.File;

public class FileUtils {

    public static boolean createDirectory(File parentDir, String dirName) {
        File newDir = new File(parentDir, dirName);
        if (!newDir.exists()) {
            return newDir.mkdirs();  // Crée le répertoire et tous les sous-répertoires nécessaires
        }
        return false;  // Le répertoire existe déjà
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            // Supprimer tous les fichiers et sous-répertoires
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        // Supprimer le répertoire (ou fichier) actuel
        return dir.delete();
    }
}
