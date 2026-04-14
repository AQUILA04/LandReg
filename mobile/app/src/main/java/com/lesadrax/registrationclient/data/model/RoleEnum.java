package com.lesadrax.registrationclient.data.model;

public enum RoleEnum {

    MAYOR("Maire", RoleType.PHYSICAL_PERSON),
    TRADITIONAL_CHIEF("Chef traditionnel", RoleType.PHYSICAL_PERSON),
    NOTABLE("Notable", RoleType.PHYSICAL_PERSON),
    SURVEYOR("Géomètre", RoleType.PHYSICAL_PERSON),
    BORDERING("Limitrophe", RoleType.PHYSICAL_PERSON),
    OWNER_OR_REPRESENTATIVE("Propriétaire/Mandataire", RoleType.PHYSICAL_PERSON),
    EXPLOITING("Exploitant", RoleType.PHYSICAL_PERSON),
    TOPOGRAPHER("Agent Topographe", RoleType.PHYSICAL_PERSON2),
    SOCIAL_LAND_AGENT("Agent Socio-foncier", RoleType.PHYSICAL_PERSON2),
    PRIVATE_LEGAL_ENTITY("Personne morale de droit privé", RoleType.PRIVATE_LEGAL_ENTITY),
    PUBLIC_LEGAL_ENTITY("Personne morale de droit public", RoleType.PUBLIC_LEGAL_ENTITY),
    INFORMAL_GROUP("Groupe informel", RoleType.INFORMAL_GROUP);

    private String tag;
    private RoleType type;

    RoleEnum(String tag, RoleType type) {
        this.tag = tag;
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public RoleType getType() {
        return type;
    }

    @Override
    public String toString() {
        return tag;
    }

    public enum RoleType {
        PHYSICAL_PERSON,
        PHYSICAL_PERSON2,
        INFORMAL_GROUP,
        PRIVATE_LEGAL_ENTITY,
        PUBLIC_LEGAL_ENTITY,
    }
}
