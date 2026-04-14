package com.lesadrax.registrationclient.from.utils;

import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class FileUtils {

    /**
     * Convertit un fichier à partir de son chemin absolu en une chaîne Base64 avec préfixe data et type MIME.
     * @param absolutePath Le chemin absolu du fichier.
     * @return La chaîne Base64 avec le préfixe data et le type MIME, ou null en cas d'erreur.
     */
    public static String convertFileToBase64WithPrefix(String absolutePath) {
        File file = new File(absolutePath);

        if (!file.exists() || !file.isFile()) {
            System.err.println("Le fichier n'existe pas ou le chemin n'est pas valide : " + absolutePath);
            return null;
        }

        // Détecter le type MIME à partir de l'extension du fichier
        String mimeType = getMimeType(absolutePath);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Lire le fichier dans un tableau de bytes
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);

            // Encoder en Base64
            String base64Data = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                base64Data = Base64.getEncoder().encodeToString(fileBytes);
            }

            // Ajouter le préfixe avec le type MIME
            return "data:" + mimeType + ";base64," + base64Data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Détecte le type MIME d'un fichier en fonction de son extension.
     * @param absolutePath Le chemin absolu du fichier.
     * @return Le type MIME du fichier.
     */
    private static String getMimeType(String absolutePath) {
        String mimeType = "application/octet-stream"; // Valeur par défaut pour les fichiers binaires

        try {
            // Détecter le type MIME
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mimeType = Files.probeContentType(new File(absolutePath).toPath());
            }
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // Si aucun type MIME trouvé, utiliser par défaut
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mimeType;
    }

}

