package com.lesadrax.registrationclient.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "roles")
public class Role implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;

    private String value;

    public Role(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Role() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return value.equals(role.value); // Compare uniquement par code
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }


    public static String getRoleNameByCode(String code) {
        // Initialisation de la liste des rôles
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("Maire/représentant", "MAYOR"));
        roles.add(new Role("Chef traditionnel / Coutumier", "TRADITIONAL_CHIEF"));
        roles.add(new Role("Notable du lieu", "NOTABLE"));
        roles.add(new Role("Géomètre de la mairie", "SURVEYOR"));
        roles.add(new Role("Propriétaire/ Mandataire", "OWNER_OR_REPRESENTATIVE"));
        roles.add(new Role("Angent Topographe", "TOPOGRAPHER"));
        roles.add(new Role("Agent Socio foncier", "SOCIAL_LAND_AGENT"));
        roles.add(new Role("Tiers Intéressé", "TIERS"));
        roles.add(new Role("Limitrophe", "BORDERING"));
        roles.add(new Role("Exploitant", "EXPLOITING"));
        roles.add(new Role("Groupe informel", "INFORMAL_GROUP"));
        roles.add(new Role("Personne morale de droit public", "PUBLIC_LEGAL_ENTITY"));
        roles.add(new Role("Personne morale de droit privé", "PRIVATE_LEGAL_ENTITY"));
        roles.add(new Role("Etat", "ETAT"));
        roles.add(new Role("Collectivité territoriale", "COLLECTIVITE_TERRITORIALE"));
        roles.add(new Role("Etablissement public", "ETABLISSEMENT_PUBLIC"));
        roles.add(new Role("ONG", "ONG"));
        roles.add(new Role("Association", "ASSOCIATION"));
        roles.add(new Role("Entreprise", "ENTREPRISE"));
        roles.add(new Role("Cooperative agricole", "COOPERATIVE_AGRICOLE"));

        // Parcours de la liste pour trouver le rôle correspondant
        for (Role role : roles) {
            if (role.getValue().equalsIgnoreCase(code)) {
                return role.getName();
            }
        }

        // Retourne une valeur par défaut si aucun rôle n'est trouvé
        return "Code inconnu";
    }

    public static String getFrenchTranslation(String status) {
        if (status == null) {
            return "Statut inconnu";
        }

        switch (status) {
            case "PENDING":
                return "En attente";
            case "QUEUED":
                return "En file d'attente";
            case "IN_PROGRESS":
                return "En cours";
            case "FAILED":
                return "Échec";
            case "VALIDATED":
                return "Validé";
            case "DUPLICATED":
                return "Doublon";
            case "ACTOR":
                return "Acteur";
            default:
                return "Statut inconnu";
        }
    }


}
