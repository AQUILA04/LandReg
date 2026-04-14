package com.lesadrax.registrationclient.data.utis;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Bordering;
import com.lesadrax.registrationclient.data.model.Checklist;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.model.RoleEnum;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.FileUtils;
import com.lesadrax.registrationclient.from.utils.FormDataUtils;
import com.lesadrax.registrationclient.sessionManager.SessionManager;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DataUtils {

    public static JsonObject actorData(Actor actor, String synchroBatchNumber) {

        if (actor.getFormValues() == null) return null;

        Map<String, FormValue> form = actor.getFormValues();

        Object role = FormDataUtils.getFormValueValue(form, "role");
        Object roleType = FormDataUtils.getFormValueValue(form, "roleType");

        JsonObject data = new JsonObject();
        Log.d("***Role", "=====> "+form.get("birthDate"));

        if( actor.getId() > 0 && synchroBatchNumber.isEmpty()) {
            // pour la mise à jour en envoyant ID
            data.addProperty("id", actor.getId());
            Log.d("*****ID", "=====> "+actor.getId());
        }


        if(RoleEnum.RoleType.PHYSICAL_PERSON.name().equals(roleType) || RoleEnum.RoleType.PHYSICAL_PERSON2.name().equals(roleType)) {

            JsonObject json = new JsonObject();
            JsonObject docData = new JsonObject();

            for (Map.Entry<String, FormValue> entry : form.entrySet()) {
                String key = entry.getKey();
                FormValue value = entry.getValue();
                //System.out.println("***"+key + " | " + value.getRemoteValue());

                if(value != null){
                    System.out.println("***"+key + " | " + value.getRemoteValue());
                    if ("integer".equals(value.getParseType())) {
                        if (value.getRemoteValue() instanceof Integer) {
                            json.addProperty(key, (int) (value.getRemoteValue()));
                        }
                    } else if ("boolean".equals(value.getParseType())) {
                        if (value.getRemoteValue() instanceof Boolean) {
                            json.addProperty(key, (Boolean) (value.getRemoteValue()));
                        }
                    }
                    else if(value.getRemoteValue() == null){
                        System.out.println("***"+key + " | " + value.getRemoteValue());
                        System.out.println("***"+key + " | " + value.getValue());
                        System.out.println("***"+key + " | " + value.getDisplay());
                        json.add(key, null);
                    }
                    else {
                        System.out.println(key + " | " + value.getRemoteValue());
                        if (value.getRemoteValue() instanceof String) {
                            json.addProperty(key, (String) value.getRemoteValue());
                        }
                    }

                    if (key.equals("identificationDocNumber")
                            || key.equals("identificationDocType") || key.equals("otherIdentificationDocType")
                            || key.equals("identificationDocPhotoContentType")) {
                        if(value.getRemoteValue() == null){
                            docData.add(key, null);
                        }
                        else if (value.getRemoteValue() instanceof String) {
                            docData.addProperty(key, (String) value.getRemoteValue());
                        }
                    }

                    if (key.equals("identificationDocPhoto") && value.getRemoteValue() instanceof String && !((String) value.getRemoteValue()).isEmpty()) {
                        docData.addProperty("identificationDocPhoto", FileUtils.convertFileToBase64WithPrefix((String) value.getRemoteValue()));
                    }
                    if(actor.getDocID() > 0){
                        docData.addProperty("id", actor.getDocID());
                    }
                }
            }

            // Vérification de hasIDDoc avant d'ajouter identificationDoc
            if (!(form.containsKey("hasIDDoc") && Boolean.FALSE.equals(Objects.requireNonNull(form.get("hasIDDoc")).getRemoteValue()) )) {
                json.add("identificationDoc", docData); // Ajoute uniquement si hasIDDoc est true ou absent.
            }

            if(form.containsKey("identificationDocPhoto") && form.get("identificationDocPhoto") == null) {
                json.add("identificationDoc", null);
            }
            if(actor.getPersonID() != 0)
                json.addProperty("id", actor.getPersonID());

            data.add("physicalPerson", json);
        }

        // Vérification et ajout pour privateLegalEntity
        else if (RoleEnum.RoleType.PRIVATE_LEGAL_ENTITY.name().equals(roleType)) {
            JsonObject json = new JsonObject();
            JsonObject docData = new JsonObject();

            for (Map.Entry<String, FormValue> entry : form.entrySet()) {
                String key = entry.getKey();
                FormValue value = entry.getValue();

               if(value != null){
                   if ("integer".equals(value.getParseType())) {
                       if (value.getRemoteValue() instanceof Integer) {
                           json.addProperty(key, (int) (value.getRemoteValue()));
                       }
                   } else if ("boolean".equals(value.getParseType())) {
                       if (value.getRemoteValue() instanceof Boolean) {
                           json.addProperty(key, (Boolean) (value.getRemoteValue()));
                       }
                   } else {
                       System.out.println(key + " | " + value.getRemoteValue());
                       if (value.getRemoteValue() instanceof String) {
                           json.addProperty(key, (String) value.getRemoteValue());
                       }
                   }

                   if (key.equals("identificationDocNumber")
                           || key.equals("identificationDocType") || key.equals("otherIdentificationDocType")
                           || key.equals("identificationDocPhotoContentType")) {
                       if (value.getRemoteValue() instanceof String) {
                           docData.addProperty(key, (String) value.getRemoteValue());
                       }
                   }

                   if (key.equals("identificationDocPhoto") && value.getRemoteValue() instanceof String) {
                       docData.addProperty("identificationDocPhoto", FileUtils.convertFileToBase64WithPrefix((String) value.getRemoteValue()));
                   }
               }

                // Vérification de hasIDDoc avant d'ajouter identificationDoc
                if (!(form.containsKey("hasIDDoc") && Boolean.FALSE.equals(form.get("hasIDDoc").getRemoteValue()))) {
                    json.add("identificationDoc", docData); // Ajoute uniquement si hasIDDoc est true ou absent.
                }
               }

            if(actor.getPersonID() != 0)
                json.addProperty("id", actor.getPersonID());

            data.add("privateLegalEntity", json);
        }

        // Autres types (publicLegalEntity, informalGroup, etc.) restent inchangés
        else if (RoleEnum.RoleType.PUBLIC_LEGAL_ENTITY.name().equals(roleType)) {
            JsonObject json = new JsonObject();

            for (Map.Entry<String, FormValue> entry : form.entrySet()) {
                String key = entry.getKey();
                FormValue value = entry.getValue();

                if(value != null){
                    if ("integer".equals(value.getParseType())) {
                        if (value.getRemoteValue() instanceof Integer) {
                            json.addProperty(key, (int) (value.getRemoteValue()));
                        }
                    } else if ("boolean".equals(value.getParseType())) {
                        if (value.getRemoteValue() instanceof Boolean) {
                            json.addProperty(key, (Boolean) (value.getRemoteValue()));
                        }
                    } else {
                        System.out.println(key + " | " + value.getRemoteValue());
                        if (value.getRemoteValue() instanceof String) {
                            json.addProperty(key, (String) value.getRemoteValue());
                        }
                    }
                }
            }

            if(actor.getPersonID() != 0)
                json.addProperty("id", actor.getPersonID());

            data.add("publicLegalEntity", json);
        }

        // Ajout des données générales
        if (role instanceof String) {
            if (RoleEnum.RoleType.PHYSICAL_PERSON2.name().equals(roleType)) {
                data.addProperty("type", "PHYSICAL_PERSON");
            } else {
                data.addProperty("type", (String) roleType);
            }
        }
        if (roleType instanceof String) {
            data.addProperty("role", (String) role);
        }

        data.add("fingerprintStores", fingerData(form, actor));

        data.addProperty("synchroBatchNumber", synchroBatchNumber);
        data.addProperty("synchroPacketNumber", UUID.randomUUID().toString());
        if(synchroBatchNumber.isEmpty()){
            Object uin = FormDataUtils.getFormValueValue(form, "uin");
            data.addProperty("uin", Objects.requireNonNull(uin).toString());
        }

        return data;
    }


    private static JsonArray fingerData(Map<String, FormValue> form, Actor actor){

        JsonArray data = new JsonArray();

        JsonObject finger1 = new JsonObject();
        JsonObject finger2 = new JsonObject();
        JsonObject finger3 = new JsonObject();

        if(FormDataUtils.getFormValueDisplay(form, "fingerFirstImage") != null && FormDataUtils.getFormValueDisplay(form, "fingerSecondImage") != null && FormDataUtils.getFormValueDisplay(form, "fingerThirdImage") != null){
            finger1.addProperty("fingerStr", FormDataUtils.getFormValueDisplay(form, "fingerFirstName"));
            finger1.addProperty("fingerprintImage", FileUtils.convertFileToBase64WithPrefix(FormDataUtils.getFormValueDisplay(form, "fingerFirstImage")));
            if (actor.getFinger1ID() > 0){
                finger1.addProperty("id", actor.getFinger1ID());
            }
            //
            finger2.addProperty("fingerStr", FormDataUtils.getFormValueDisplay(form, "fingerSecondName"));
            finger2.addProperty("fingerprintImage", FileUtils.convertFileToBase64WithPrefix(FormDataUtils.getFormValueDisplay(form, "fingerSecondImage")));
            if (actor.getFinger2ID() > 0){
                finger2.addProperty("id", actor.getFinger2ID());
            }
            finger3.addProperty("fingerStr", FormDataUtils.getFormValueDisplay(form, "fingerThirdName"));
            finger3.addProperty("fingerprintImage", FileUtils.convertFileToBase64WithPrefix(FormDataUtils.getFormValueDisplay(form, "fingerThirdImage")));
            if (actor.getFinger3ID() > 0){
                finger3.addProperty("id", actor.getFinger3ID());
            }
            data.add(finger1);
            data.add(finger2);
            data.add(finger3);

            return data;
        }

        // TODO Convert to bitmap
//        finger1.addProperty("fingerprintImage", );
//        finger2.addProperty("fingerprintImage", );
//        finger3.addProperty("fingerprintImage", );

       return null;
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
