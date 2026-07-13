package com.lesadrax.registrationclient.data.utis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lesadrax.registrationclient.data.mapper.ActorRegistrationMapper;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Bordering;
import com.lesadrax.registrationclient.data.model.Checklist;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.FileUtils;

import java.util.Map;

public class DataUtils {

    public static JsonObject actorData(Actor actor, String synchroBatchNumber) {
        return ActorRegistrationMapper.toJsonObject(
            ActorRegistrationMapper.mapToV1(actor, synchroBatchNumber)
        );
    }


    public   static JsonObject operationData(Operation operation, String synchroBatchNumber){

        if ((operation.getFormValues() == null || operation.getChecklistBeforeOperation() == null || operation.getChecklistAfterOperation() == null) && !synchroBatchNumber.equals("update")) return null;

        JsonObject data = new JsonObject();
        Checklist opB = operation.getChecklistBeforeOperation();
        Checklist opA = operation.getChecklistAfterOperation();
        Map<String, FormValue> form = operation.getFormValues();

        for (Map.Entry<String, FormValue> entry : form.entrySet()) {
            String key = entry.getKey();
            FormValue value = entry.getValue();

            if ("integer".equals(value.getParseType())){
                if (value.getRemoteValue() instanceof Integer) {
                    data.addProperty(key, (int) (value.getRemoteValue()));
                    System.out.println("***Clé "+key+" | "+value.getRemoteValue());
                }
                else
                if(value.getRemoteValue() instanceof Double){
                    System.out.println("***NewR "+key+" | "+value.getRemoteValue());
                    data.addProperty(key, (((Double) value.getRemoteValue()).intValue()));
                }
                else if(value.getRemoteValue() instanceof String){
                    data.addProperty(key, Integer.valueOf(value.getRemoteValue().toString()));

                }
                System.out.println("***New "+key+" | "+value.getRemoteValue());
            } else if ("boolean".equals(value.getParseType())){
                if (value.getRemoteValue() instanceof Boolean) {
                    System.out.println("*******"+key+" | "+value.getRemoteValue());
                    data.addProperty(key, (Boolean) (value.getRemoteValue()));
                }
            } else {
                System.out.println(key+" | "+value.getRemoteValue());
                if (value.getRemoteValue() instanceof String) {
                    data.addProperty(key, (String) value.getRemoteValue());
                    System.out.println("***Clé "+key+" | "+value.getRemoteValue());
                }
            }

        }

        if(opA != null)
           data.add("lastCheckListOperation", convertChecklistToJson(opA));
        if(opB != null)
            data.add("firstCheckListOperation", convertChecklistToJson(opB));

        JsonObject conflict = buildJsonObject(form);
        if(operation.getConflitID() > 0){
            conflict.addProperty("id", operation.getConflitID()); //mis pour la modification d'une constatation
            data.addProperty("id", operation.getId());
        }

        data.add("conflict", buildJsonObject(form));
        data.addProperty("synchroPacketNumber", UUID.randomUUID().toString());
        data.addProperty("synchroBatchNumber", synchroBatchNumber);

        return data;

    }



    public static JsonObject convertChecklistToJson(Checklist checklist) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("mayorUIN", checklist.getMayorUIN());
        jsonObject.addProperty("traditionalChiefUIN", checklist.getTraditionalChiefUIN());
        jsonObject.addProperty("notableUIN", checklist.getNotableUIN());
        jsonObject.addProperty("geometerUIN", checklist.getGeometerUIN());
        jsonObject.addProperty("ownerUIN", checklist.getOwnerUIN());
        jsonObject.addProperty("topographerUIN", checklist.getTopographerUIN());
        jsonObject.addProperty("socialLandAgentUIN", checklist.getSocialLandAgentUIN());
        jsonObject.addProperty("interestedThirdPartyUIN", checklist.getInterestedThirdPartyUIN());

        // Convertir la liste des Bordering en JsonArray
        JsonArray borderingArray = new JsonArray();
        if (checklist.getBorderingList() != null) {
            for (Bordering bordering : checklist.getBorderingList()) {
                JsonObject borderingJson = new JsonObject();
                borderingJson.addProperty("cardinalPoint", bordering.getCardinalPoint());
                borderingJson.addProperty("uin", bordering.getUin());
                borderingArray.add(borderingJson);
            }
        }

        jsonObject.add("borderingList", borderingArray);

        return jsonObject;
    }


    public static JsonObject buildJsonObject(Map<String, FormValue> formValues) {
        JsonObject json = new JsonObject();

        // Liste des clés à vérifier
        String[] requiredKeys = {
                "conflictParty", "firstConflictPartyNUP", "firstConflictPartyOccupationDurationInMonth",
                "secondConflictPartyNUP", "secondConflictPartyOccupationDurationInMonth", "conflictObject",
                "rightClaimed", "rightClaimedOrigin", "institutionInvolved", "seizureProof",
                "exhibitAndEvidence", "photoOfProof", "procedureStatus", "settlementDate",
                "settlementCompromiseNature", "settlementActor", "regulationWitnesses", "finalDecisionProof",
                "settlementProofPhoto", "rightRestrictionType", "currentlyUseFor", "agriculturalDevelopmentType",
                "pointOfAttention", "modeAcquisition", "siHeritageDeQui", "siHeritageDateDeces", "girlCount",
                "boyCount", "dateAcquisition", "typePreuveAcquisition", "photoPreuveAcquisition",
                "photoTemoignage", "photoFicheTemoignage"
        };

        // Parcours des clés et ajout au JsonObject
        for (String key : requiredKeys) {
            if (formValues.containsKey(key)) {
                FormValue value = formValues.get(key);

                if (value != null && value.getRemoteValue() != null) {
                    Object remoteValue = value.getRemoteValue();

                    // Vérification du type et ajout au JSON
                    switch (key) {
                        case "firstConflictPartyOccupationDurationInMonth":
                        case "secondConflictPartyOccupationDurationInMonth":
                        case "girlCount":
                        case "boyCount":
                            if (remoteValue instanceof Integer) {
                                json.addProperty(key, (Integer) remoteValue);
                            }
                            else if(remoteValue instanceof  Double){
                                json.addProperty(key, (((Double) value.getRemoteValue()).intValue()));
                            }
                            break;

                        case "settlementDate":
                        case "siHeritageDateDeces":
                        case "dateAcquisition":
                            if (remoteValue instanceof String) {
                                json.addProperty(key, (String) remoteValue); // Assurez-vous que la date est au format attendu
                            }
                            break;

                        default:
                            if (remoteValue instanceof String) {
                                // Exemple de traitement pour convertir un fichier en base64 si nécessaire
                                if (key.startsWith("photo") || key.startsWith("settlementProofPhoto")) {
                                    json.addProperty(key, FileUtils.convertFileToBase64WithPrefix((String) remoteValue));
                                } else {
                                    json.addProperty(key, (String) remoteValue);
                                }
                            }
                            break;
                    }
                }
            } else {
                // Si une clé manque, on peut loguer un avertissement ou lever une exception si nécessaire
                System.out.println("La clé suivante est manquante : " + key);
            }
        }

        return json;
    }

}
