package com.lesadrax.registrationclient.from.utils;

import android.util.Log;

import com.lesadrax.registrationclient.data.model.Bordering;
import com.lesadrax.registrationclient.data.model.Checklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChecklistComparator {

    public static class ComparaisonResult {
        public boolean sontEgaux;
        public Map<String, Boolean> details;
        public List<String> differences;

        public ComparaisonResult() {
            this.details = new HashMap<>();
            this.differences = new ArrayList<>();
            this.sontEgaux = true;
        }

        public void addResult(String fieldName, boolean egal, String valeur1, String valeur2) {
            details.put(fieldName, egal);
            if (!egal) {
                sontEgaux = false;
                differences.add(fieldName + ": " + valeur1 + " ≠ " + valeur2);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("════════ RÉSULTAT COMPARAISON ════════\n");
            sb.append("Global: ").append(sontEgaux ? "✓ ÉGAUX" : "✗ DIFFÉRENTS").append("\n");
            sb.append("────────────────────────────────────\n");
            for (Map.Entry<String, Boolean> entry : details.entrySet()) {
                sb.append(String.format("%-25s: %s\n", entry.getKey(),
                        entry.getValue() ? "✓" : "✗"));
            }
            if (!differences.isEmpty()) {
                sb.append("────────────────────────────────────\n");
                sb.append("DÉTAILS DES DIFFÉRENCES:\n");
                for (String diff : differences) {
                    sb.append("  • ").append(diff).append("\n");
                }
            }
            sb.append("══════════════════════════════════════");
            return sb.toString();
        }
    }

    public static ComparaisonResult comparer(Checklist avant, Checklist apres) {
        ComparaisonResult result = new ComparaisonResult();
        Log.d("***checkA", "======> " + (avant != null ? avant.toString() : "null"));
        Log.d("***checkB", "======> " + (apres != null ? apres.toString() : "null"));

        if (avant == null || apres == null) {
            result.sontEgaux = false;
            result.differences.add("Un des objets est null: avant=" + avant + ", apres=" + apres);
            return result;
        }

        // Comparer tous les champs
        result.addResult("mayorUIN",
                sontEgaux(avant.getMayorUIN(), apres.getMayorUIN()),
                avant.getMayorUIN(), apres.getMayorUIN());

        result.addResult("traditionalChiefUIN",
                sontEgaux(avant.getTraditionalChiefUIN(), apres.getTraditionalChiefUIN()),
                avant.getTraditionalChiefUIN(), apres.getTraditionalChiefUIN());

        result.addResult("notableUIN",
                sontEgaux(avant.getNotableUIN(), apres.getNotableUIN()),
                avant.getNotableUIN(), apres.getNotableUIN());

        result.addResult("geometerUIN",
                sontEgaux(avant.getGeometerUIN(), apres.getGeometerUIN()),
                avant.getGeometerUIN(), apres.getGeometerUIN());

        result.addResult("ownerUIN",
                sontEgaux(avant.getOwnerUIN(), apres.getOwnerUIN()),
                avant.getOwnerUIN(), apres.getOwnerUIN());

        result.addResult("topographerUIN",
                sontEgaux(avant.getTopographerUIN(), apres.getTopographerUIN()),
                avant.getTopographerUIN(), apres.getTopographerUIN());

        result.addResult("socialLandAgentUIN",
                sontEgaux(avant.getSocialLandAgentUIN(), apres.getSocialLandAgentUIN()),
                avant.getSocialLandAgentUIN(), apres.getSocialLandAgentUIN());

        result.addResult("interestedThirdPartyUIN",
                sontEgaux(avant.getInterestedThirdPartyUIN(), apres.getInterestedThirdPartyUIN()),
                avant.getInterestedThirdPartyUIN(), apres.getInterestedThirdPartyUIN());

        // Comparer les listes Bordering
        boolean listesEgales = comparerListesBordering(avant.getBorderingList(), apres.getBorderingList(), result);
        result.details.put("borderingList", listesEgales);
        if (!listesEgales) {
            result.sontEgaux = false;
        }

        return result;
    }

    private static boolean sontEgaux(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    /**
     * Compare deux listes de Bordering sans tenir compte de l'ordre des éléments.
     * Vérifie que chaque élément de la première liste a un équivalent dans la deuxième liste.
     */
    private static boolean comparerListesBordering(List<Bordering> liste1, List<Bordering> liste2, ComparaisonResult result) {
        // Cas où les deux listes sont null
        if (liste1 == null && liste2 == null) return true;

        // Cas où une seule liste est null
        if (liste1 == null || liste2 == null) {
            result.differences.add("borderingList: une liste est null (liste1=" + liste1 + ", liste2=" + liste2 + ")");
            return false;
        }

        // Comparer les tailles
        if (liste1.size() != liste2.size()) {
            result.differences.add("borderingList: tailles différentes (" +
                    liste1.size() + " vs " + liste2.size() + ")");
            return false;
        }

        // Créer une copie de la deuxième liste pour pouvoir retirer les éléments trouvés
        List<Bordering> tempList2 = new ArrayList<>(liste2);

        // Parcourir chaque élément de la première liste
        for (int i = 0; i < liste1.size(); i++) {
            Bordering b1 = liste1.get(i);
            boolean found = false;

            // Chercher un élément correspondant dans la copie de la deuxième liste
            for (int j = 0; j < tempList2.size(); j++) {
                Bordering b2 = tempList2.get(j);

                // Gérer le cas où les deux éléments sont null
                if (b1 == null && b2 == null) {
                    found = true;
                    tempList2.remove(j);
                    break;
                }

                // Gérer le cas où un seul est null
                if (b1 == null || b2 == null) {
                    continue;
                }

                // Comparer les champs importants (cardinalPoint et uin)
                // On ignore l'id car il peut être différent (0 vs valeur réelle)
                boolean cardinalEgaux = sontEgaux(b1.getCardinalPoint(), b2.getCardinalPoint());
                boolean uinEgaux = sontEgaux(b1.getUin(), b2.getUin());

                if (cardinalEgaux && uinEgaux) {
                    found = true;
                    tempList2.remove(j); // Retirer l'élément trouvé pour ne pas le réutiliser
                    break;
                }
            }

            // Si aucun élément correspondant n'a été trouvé
            if (!found) {
                String cardinalPoint = (b1 != null) ? b1.getCardinalPoint() : "null";
                String uin = (b1 != null) ? b1.getUin() : "null";
                result.differences.add("borderingList: élément '" + cardinalPoint +
                        "' (UIN=" + uin + ") non trouvé dans la deuxième liste");
                return false;
            }
        }

        return true;
    }
}