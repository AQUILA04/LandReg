-- =====================================================
-- Script de création du schéma de base de données
-- Version: V0__SCHEMA.sql
-- Description: Création de toutes les tables et contraintes
-- =====================================================

-- Suppression des tables dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS fingerprint_matching_history CASCADE;
DROP TABLE IF EXISTS fingerprint_store CASCADE;
DROP TABLE IF EXISTS bordering CASCADE;
DROP TABLE IF EXISTS check_list_operation CASCADE;
DROP TABLE IF EXISTS conflict CASCADE;
DROP TABLE IF EXISTS finding CASCADE;
DROP TABLE IF EXISTS synchro_history CASCADE;
DROP TABLE IF EXISTS registration CASCADE;
DROP TABLE IF EXISTS actor CASCADE;
DROP TABLE IF EXISTS abstract_actor CASCADE;
DROP TABLE IF EXISTS private_legal_entity CASCADE;
DROP TABLE IF EXISTS public_legal_entity CASCADE;
DROP TABLE IF EXISTS informal_group CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS identification_doc CASCADE;
DROP TABLE IF EXISTS account_permission CASCADE;
DROP TABLE IF EXISTS profil_permission CASCADE;
DROP TABLE IF EXISTS user_permission CASCADE;
DROP TABLE IF EXISTS user_profil CASCADE;
DROP TABLE IF EXISTS user_account CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS refresh_token CASCADE;
DROP TABLE IF EXISTS deployment_licence CASCADE;
DROP TABLE IF EXISTS licence CASCADE;
DROP TABLE IF EXISTS parameter CASCADE;

-- =====================================================
-- Tables du module "common.entities"
-- =====================================================

-- 1. Table identification_doc
-- Description: Stocke les documents d'identification des personnes et entités
-- (CNI, passeport, récépissé, etc.)
-- Table identification_doc
CREATE TABLE identification_doc (
                                    id BIGSERIAL PRIMARY KEY,
                                    identification_doc_type VARCHAR(255),
                                    other_identification_doc_type VARCHAR(255),
                                    identification_doc_number VARCHAR(255),
                                    identification_doc_photo BYTEA,
                                    identification_doc_photo_content_type VARCHAR(255),
                                    reg_user_id VARCHAR(50) NOT NULL,
                                    date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    mod_user_id VARCHAR(50),
                                    date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);
COMMENT ON TABLE identification_doc IS 'Documents d''identification (CNI, passeport, récépissé) pour les personnes physiques et morales';

COMMENT ON COLUMN identification_doc.identification_doc_type IS 'Type de document (CNI, PASSEPORT, RECEPISSE, etc.)';
COMMENT ON COLUMN identification_doc.other_identification_doc_type IS 'Autre type de document si "AUTRE" est sélectionné';
COMMENT ON COLUMN identification_doc.identification_doc_number IS 'Numéro du document d''identification';
COMMENT ON COLUMN identification_doc.identification_doc_photo IS 'Photo ou scan du document (format binaire)';
COMMENT ON COLUMN identification_doc.identification_doc_photo_content_type IS 'Type MIME de la photo (image/jpeg, image/png, etc.)';

-- Table person
CREATE TABLE person (
                        id BIGSERIAL PRIMARY KEY,
                        lastname VARCHAR(25) NOT NULL,
                        firstname VARCHAR(55) NOT NULL,
                        sex VARCHAR(50),
                        uin VARCHAR(255) UNIQUE,
                        marital_status VARCHAR(50),
                        birth_date DATE,
                        place_of_birth VARCHAR(60),
                        nationality VARCHAR(60),
                        profession VARCHAR(255),
                        other_profession VARCHAR(255),
                        address VARCHAR(70),
                        primary_phone VARCHAR(11),
                        secondary_phone VARCHAR(11),
                        email VARCHAR(255),
                        has_handicap BOOLEAN,
                        socio_cultural_group VARCHAR(255),
                        handicap_type VARCHAR(255),
                        other_handicap_type VARCHAR(255),
                        has_id_doc BOOLEAN,
                        identification_doc_id BIGINT,
                        witness_uin VARCHAR(255),
                        role VARCHAR(255),
                        registration_status VARCHAR(50),
                        status_observation TEXT,
                        rid VARCHAR(255) UNIQUE,
                        synchro_batch_number TEXT,
                        synchro_packet_number TEXT UNIQUE,
                        reg_user_id VARCHAR(50) NOT NULL,
                        date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        mod_user_id VARCHAR(50),
                        date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                        CONSTRAINT fk_person_identification_doc FOREIGN KEY (identification_doc_id) REFERENCES identification_doc(id),
                        CONSTRAINT person_unique_constraint UNIQUE (lastname, firstname, sex, marital_status, birth_date, place_of_birth, nationality, profession, address, primary_phone, email)
);
COMMENT ON TABLE person IS 'Personnes physiques - informations démographiques et de contact des individus';

COMMENT ON COLUMN person.lastname IS 'Nom de famille';
COMMENT ON COLUMN person.firstname IS 'Prénom(s)';
COMMENT ON COLUMN person.sex IS 'Sexe (MASCULIN, FEMININ)';
COMMENT ON COLUMN person.uin IS 'Numéro d''Identification Unique (NIU) généré après validation';
COMMENT ON COLUMN person.marital_status IS 'Situation matrimoniale (CELIBATAIRE, MARIE, DIVORCE, VEUF)';
COMMENT ON COLUMN person.birth_date IS 'Date de naissance';
COMMENT ON COLUMN person.place_of_birth IS 'Lieu de naissance';
COMMENT ON COLUMN person.nationality IS 'Nationalité';
COMMENT ON COLUMN person.profession IS 'Profession';
COMMENT ON COLUMN person.other_profession IS 'Autre profession si non listée';
COMMENT ON COLUMN person.address IS 'Adresse physique';
COMMENT ON COLUMN person.primary_phone IS 'Téléphone principal';
COMMENT ON COLUMN person.secondary_phone IS 'Téléphone secondaire';
COMMENT ON COLUMN person.email IS 'Adresse email';
COMMENT ON COLUMN person.has_handicap IS 'Indique si la personne a un handicap';
COMMENT ON COLUMN person.handicap_type IS 'Type de handicap';
COMMENT ON COLUMN person.has_id_doc IS 'Indique si la personne possède une pièce d''identité';
COMMENT ON COLUMN person.witness_uin IS 'NIU du témoin';
COMMENT ON COLUMN person.registration_status IS 'Statut d''enregistrement';
COMMENT ON COLUMN person.rid IS 'Identifiant d''enregistrement (Registration ID)';

-- Table informal_group
CREATE TABLE informal_group (
                                id BIGSERIAL PRIMARY KEY,
                                uin VARCHAR(255) UNIQUE,
                                group_name VARCHAR(255) NOT NULL UNIQUE,
                                address VARCHAR(255) NOT NULL,
                                phone_number VARCHAR(11) UNIQUE,
                                secondary_phone_number VARCHAR(11),
                                email VARCHAR(255),
                                group_type VARCHAR(255) NOT NULL,
                                representative_uin VARCHAR(255),
                                representative_fullname VARCHAR(80),
                                secondary_representative_uin VARCHAR(255),
                                secondary_representative_fullname VARCHAR(80),
                                third_representative_uin VARCHAR(255),
                                third_representative_fullname VARCHAR(80),
                                mandate_photo BYTEA,
                                mandate_photo_content_type VARCHAR(255),
                                reg_user_id VARCHAR(50) NOT NULL,
                                date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                mod_user_id VARCHAR(50),
                                date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                                CONSTRAINT fk_informal_group_rep FOREIGN KEY (representative_uin) REFERENCES actor(uin),
                                CONSTRAINT fk_informal_group_sec_rep FOREIGN KEY (secondary_representative_uin) REFERENCES actor(uin),
                                CONSTRAINT fk_informal_group_third_rep FOREIGN KEY (third_representative_uin) REFERENCES actor(uin)
);

COMMENT ON TABLE informal_group IS 'Groupements informels (associations, coopératives, GIE) sans personnalité juridique formelle';

COMMENT ON COLUMN informal_group.uin IS 'Numéro d''Identification Unique du groupe';
COMMENT ON COLUMN informal_group.group_name IS 'Nom du groupe informel';
COMMENT ON COLUMN informal_group.address IS 'Adresse du groupe';
COMMENT ON COLUMN informal_group.phone_number IS 'Numéro de téléphone principal';
COMMENT ON COLUMN informal_group.group_type IS 'Type de groupe (ASSOCIATION, COOPERATIVE, GIE, etc.)';
COMMENT ON COLUMN informal_group.representative_uin IS 'NIU du premier représentant';
COMMENT ON COLUMN informal_group.representative_fullname IS 'Nom complet du premier représentant';
COMMENT ON COLUMN informal_group.mandate_photo IS 'Photo du mandat ou de l''acte de désignation';

-- Table private_legal_entity
CREATE TABLE private_legal_entity (
                                      id BIGSERIAL PRIMARY KEY,
                                      uin VARCHAR(255),
                                      company_name VARCHAR(60) NOT NULL UNIQUE,
                                      address VARCHAR(200),
                                      phone_number VARCHAR(11),
                                      secondary_phone_number VARCHAR(11),
                                      email VARCHAR(255),
                                      entity_type VARCHAR(50) NOT NULL,
                                      identification_doc_id BIGINT,
                                      main_activity VARCHAR(255) NOT NULL,
                                      acronym VARCHAR(255),
                                      company_created_date DATE,
                                      representative_uin VARCHAR(255),
                                      representative_fullname VARCHAR(255),
                                      rid VARCHAR(255),
                                      reg_user_id VARCHAR(50) NOT NULL,
                                      date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      mod_user_id VARCHAR(50),
                                      date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                                      CONSTRAINT fk_private_legal_entity_id_doc FOREIGN KEY (identification_doc_id) REFERENCES identification_doc(id)
);

COMMENT ON TABLE private_legal_entity IS 'Personnes morales de droit privé (entreprises, SARL, SA, SAS, etc.)';

COMMENT ON COLUMN private_legal_entity.uin IS 'Numéro d''Identification Unique de l''entité';
COMMENT ON COLUMN private_legal_entity.company_name IS 'Raison sociale ou dénomination officielle';
COMMENT ON COLUMN private_legal_entity.address IS 'Adresse du siège social';
COMMENT ON COLUMN private_legal_entity.phone_number IS 'Téléphone du siège';
COMMENT ON COLUMN private_legal_entity.entity_type IS 'Type d''entité (SARL, SA, SAS, GIE, etc.)';
COMMENT ON COLUMN private_legal_entity.main_activity IS 'Activité principale';
COMMENT ON COLUMN private_legal_entity.acronym IS 'Sigle ou acronyme';
COMMENT ON COLUMN private_legal_entity.company_created_date IS 'Date de création de l''entreprise';
COMMENT ON COLUMN private_legal_entity.representative_uin IS 'NIU du représentant légal';


-- Table public_legal_entity
CREATE TABLE public_legal_entity (
                                     id BIGSERIAL PRIMARY KEY,
                                     uin VARCHAR(255) UNIQUE,
                                     public_entity_type VARCHAR(50) NOT NULL,
                                     phone_number VARCHAR(255),
                                     name VARCHAR(255),
                                     reg_user_id VARCHAR(50) NOT NULL,
                                     date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     mod_user_id VARCHAR(50),
                                     date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);

COMMENT ON TABLE public_legal_entity IS 'Personnes morales de droit public (État, ministères, collectivités territoriales, établissements publics)';


COMMENT ON COLUMN public_legal_entity.uin IS 'Numéro d''Identification Unique de l''entité publique';
COMMENT ON COLUMN public_legal_entity.public_entity_type IS 'Type d''entité (ETAT, MINISTERE, COMMUNE, PREFECTURE, etc.)';
COMMENT ON COLUMN public_legal_entity.phone_number IS 'Numéro de téléphone de contact';
COMMENT ON COLUMN public_legal_entity.name IS 'Nom ou intitulé de l''entité publique';

-- 6. Table abstract_actor
-- Description: Table abstraite pour tous les types d'acteurs (personne physique, groupe, entité privée/publique)

-- Table abstract_actor
CREATE TABLE abstract_actor (
                                id BIGINT PRIMARY KEY,
                                uin VARCHAR(255) UNIQUE,
                                name VARCHAR(255),
                                phone VARCHAR(255),
                                registration_status VARCHAR(50) NOT NULL,
                                status_observation TEXT,
                                rid VARCHAR(255),
                                synchro_batch_number TEXT,
                                synchro_packet_number TEXT,
                                role VARCHAR(50),
                                type VARCHAR(50),
                                operator_agent VARCHAR(255),
                                physical_person_id BIGINT,
                                informal_group_id BIGINT,
                                private_legal_entity_id BIGINT,
                                public_legal_entity_id BIGINT,
                                reg_user_id VARCHAR(50) NOT NULL,
                                date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                mod_user_id VARCHAR(50),
                                date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                                CONSTRAINT fk_abstract_actor_physical_person FOREIGN KEY (physical_person_id) REFERENCES person(id),
                                CONSTRAINT fk_abstract_actor_informal_group FOREIGN KEY (informal_group_id) REFERENCES informal_group(id),
                                CONSTRAINT fk_abstract_actor_private_legal FOREIGN KEY (private_legal_entity_id) REFERENCES private_legal_entity(id),
                                CONSTRAINT fk_abstract_actor_public_legal FOREIGN KEY (public_legal_entity_id) REFERENCES public_legal_entity(id)
);
COMMENT ON TABLE abstract_actor IS 'Classe abstraite pour tous les types d''acteurs - stratégie d''héritage TABLE_PER_CLASS';

COMMENT ON COLUMN abstract_actor.uin IS 'Numéro d''Identification Unique (NIU)';
COMMENT ON COLUMN abstract_actor.name IS 'Nom de l''acteur (dénormalisé pour recherche rapide)';
COMMENT ON COLUMN abstract_actor.phone IS 'Téléphone (dénormalisé pour recherche rapide)';
COMMENT ON COLUMN abstract_actor.registration_status IS 'Statut d''enregistrement (PENDING, ACTOR, REJECTED, etc.)';
COMMENT ON COLUMN abstract_actor.rid IS 'Registration ID - identifiant technique pour l''enregistrement';
COMMENT ON COLUMN abstract_actor.role IS 'Rôle de l''acteur (TOPOGRAPHER, SOCIAL_LAND_AGENT, TIERS, etc.)';
COMMENT ON COLUMN abstract_actor.type IS 'Type d''acteur (PHYSICAL_PERSON, INFORMAL_GROUP, PRIVATE_LEGAL_ENTITY, PUBLIC_LEGAL_ENTITY)';

-- 7. Table actor
-- Description: Acteurs concrets du système foncier (hérite de AbstractActor)

-- Table actor (hérite de abstract_actor)
CREATE TABLE actor (
                       id BIGINT PRIMARY KEY,
                       uin VARCHAR(255) UNIQUE,
                       name VARCHAR(255),
                       phone VARCHAR(255),
                       registration_status VARCHAR(50) NOT NULL,
                       status_observation TEXT,
                       rid VARCHAR(255),
                       synchro_batch_number TEXT,
                       synchro_packet_number TEXT,
                       role VARCHAR(50),
                       type VARCHAR(50),
                       operator_agent VARCHAR(255),
                       physical_person_id BIGINT,
                       informal_group_id BIGINT,
                       private_legal_entity_id BIGINT,
                       public_legal_entity_id BIGINT,
                       reg_user_id VARCHAR(50) NOT NULL,
                       date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       mod_user_id VARCHAR(50),
                       date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                       CONSTRAINT fk_actor_physical_person FOREIGN KEY (physical_person_id) REFERENCES person(id),
                       CONSTRAINT fk_actor_informal_group FOREIGN KEY (informal_group_id) REFERENCES informal_group(id),
                       CONSTRAINT fk_actor_private_legal FOREIGN KEY (private_legal_entity_id) REFERENCES private_legal_entity(id),
                       CONSTRAINT fk_actor_public_legal FOREIGN KEY (public_legal_entity_id) REFERENCES public_legal_entity(id)
);
COMMENT ON TABLE actor IS 'Acteurs concrets du système foncier (propriétaires, témoins, géomètres, etc.) - hérite de AbstractActor';

COMMENT ON COLUMN actor.role IS 'Rôle spécifique de l''acteur dans les opérations foncières';
COMMENT ON COLUMN actor.type IS 'Type concret d''acteur';

-- 8. Table registration
-- Description: Enregistrements temporaires avant validation finale (hérite de AbstractActor)

-- Table registration (hérite de abstract_actor)
CREATE TABLE registration (
                              id BIGINT PRIMARY KEY,
                              uin VARCHAR(255) UNIQUE,
                              name VARCHAR(255),
                              phone VARCHAR(255),
                              registration_status VARCHAR(50) NOT NULL,
                              status_observation TEXT,
                              rid VARCHAR(255),
                              synchro_batch_number TEXT,
                              synchro_packet_number TEXT,
                              role VARCHAR(50),
                              type VARCHAR(50),
                              operator_agent VARCHAR(255),
                              physical_person_id BIGINT,
                              informal_group_id BIGINT,
                              private_legal_entity_id BIGINT,
                              public_legal_entity_id BIGINT,
                              reg_user_id VARCHAR(50) NOT NULL,
                              date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              mod_user_id VARCHAR(50),
                              date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                              CONSTRAINT fk_registration_physical_person FOREIGN KEY (physical_person_id) REFERENCES person(id),
                              CONSTRAINT fk_registration_informal_group FOREIGN KEY (informal_group_id) REFERENCES informal_group(id),
                              CONSTRAINT fk_registration_private_legal FOREIGN KEY (private_legal_entity_id) REFERENCES private_legal_entity(id),
                              CONSTRAINT fk_registration_public_legal FOREIGN KEY (public_legal_entity_id) REFERENCES public_legal_entity(id)
);
COMMENT ON TABLE registration IS 'Enregistrements temporaires en attente de validation pour devenir des acteurs à part entière';

COMMENT ON COLUMN registration.registration_status IS 'Statut de l''enregistrement (PENDING, VALIDATED, REJECTED)';

-- 9. Table fingerprint_store
-- Description: Stockage des empreintes digitales des acteurs (personnes physiques)

-- Table fingerprint_store
CREATE TABLE fingerprint_store (
                                   id BIGSERIAL PRIMARY KEY,
                                   rid VARCHAR(255) NOT NULL,
                                   hand_type VARCHAR(50),
                                   finger_name VARCHAR(50),
                                   fingerprint_image BYTEA,
                                   fingerprint_image_content_type VARCHAR(255),
                                   finger_str VARCHAR(255),
                                   actor_id BIGINT,
                                   reg_user_id VARCHAR(50) NOT NULL,
                                   date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   mod_user_id VARCHAR(50),
                                   date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                                   CONSTRAINT fk_fingerprint_store_actor FOREIGN KEY (actor_id) REFERENCES abstract_actor(id)
);
COMMENT ON TABLE fingerprint_store IS 'Données biométriques - empreintes digitales des acteurs (principalement personnes physiques)';

COMMENT ON COLUMN fingerprint_store.rid IS 'Registration ID lié à l''empreinte';
COMMENT ON COLUMN fingerprint_store.hand_type IS 'Type de main (RIGHT, LEFT)';
COMMENT ON COLUMN fingerprint_store.finger_name IS 'Nom du doigt (THUMB, INDEX, MIDDLE, RING, LITTLE)';
COMMENT ON COLUMN fingerprint_store.fingerprint_image IS 'Image de l''empreinte au format binaire';
COMMENT ON COLUMN fingerprint_store.fingerprint_image_content_type IS 'Type MIME de l''image (image/jpeg, image/png, etc.)';
COMMENT ON COLUMN fingerprint_store.actor_id IS 'Référence à l''acteur propriétaire de l''empreinte';
-- Table fingerprint_matching_history
CREATE TABLE fingerprint_matching_history (
                                              id BIGSERIAL PRIMARY KEY,
                                              rid VARCHAR(255),
                                              found_match BOOLEAN,
                                              matched_rid VARCHAR(255),
                                              status VARCHAR(50) DEFAULT 'SENT',
                                              reg_user_id VARCHAR(50) NOT NULL,
                                              date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              mod_user_id VARCHAR(50),
                                              date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);
COMMENT ON TABLE fingerprint_matching_history IS 'Historique des tentatives de correspondance d''empreintes digitales et leurs résultats';


COMMENT ON COLUMN fingerprint_matching_history.rid IS 'RID de l''empreinte envoyée pour recherche';
COMMENT ON COLUMN fingerprint_matching_history.found_match IS 'Indique si une correspondance a été trouvée';
COMMENT ON COLUMN fingerprint_matching_history.matched_rid IS 'RID de l''empreinte correspondante trouvée';
COMMENT ON COLUMN fingerprint_matching_history.status IS 'Statut de la recherche (SENT, RECEIVED, PROCESSING, ERROR)';

-- 11. Table check_list_operation
-- Description: Liste de contrôle pour les opérations foncières avec les acteurs impliqués
-- Table check_list_operation
CREATE TABLE check_list_operation (
                                      id BIGSERIAL PRIMARY KEY,
                                      mayor_uin VARCHAR(255) NOT NULL,
                                      traditional_chief_uin VARCHAR(255) NOT NULL,
                                      notable_uin VARCHAR(15) NOT NULL,
                                      geometer_uin VARCHAR(15) NOT NULL,
                                      owner_uin VARCHAR(255) NOT NULL,
                                      interested_third_party_uin VARCHAR(255),
                                      topographer_uin VARCHAR(255),
                                      social_land_agent_uin VARCHAR(255),
                                      tiers_uin VARCHAR(255),
                                      tiers_role VARCHAR(255),
                                      reg_user_id VARCHAR(50) NOT NULL,
                                      date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      mod_user_id VARCHAR(50),
                                      date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);
COMMENT ON TABLE check_list_operation IS 'Liste de contrôle des opérations foncières - regroupe tous les acteurs nécessaires (maire, chef, géomètre, etc.)';

COMMENT ON COLUMN check_list_operation.mayor_uin IS 'NIU du maire de la commune';
COMMENT ON COLUMN check_list_operation.traditional_chief_uin IS 'NIU du chef traditionnel';
COMMENT ON COLUMN check_list_operation.notable_uin IS 'NIU du notable';
COMMENT ON COLUMN check_list_operation.geometer_uin IS 'NIU du géomètre';
COMMENT ON COLUMN check_list_operation.owner_uin IS 'NIU du propriétaire';
COMMENT ON COLUMN check_list_operation.interested_third_party_uin IS 'NIU de la tierce partie intéressée';

-- 12. Table bordering
-- Description: Informations sur les limitrophes (voisins) d'une parcelle
-- Table bordering
CREATE TABLE bordering (
                           id BIGSERIAL PRIMARY KEY,
                           cardinal_point VARCHAR(50),
                           uin VARCHAR(255) NOT NULL,
                           check_list_operation_id BIGINT,
                           reg_user_id VARCHAR(50) NOT NULL,
                           date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           mod_user_id VARCHAR(50),
                           date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                           CONSTRAINT fk_bordering_check_list FOREIGN KEY (check_list_operation_id) REFERENCES check_list_operation(id)
);
COMMENT ON TABLE bordering IS 'Limitrophes - informations sur les voisins et les points cardinaux pour chaque parcelle';

COMMENT ON COLUMN bordering.cardinal_point IS 'Point cardinal (NORD, SUD, EST, OUEST)';
COMMENT ON COLUMN bordering.uin IS 'NIU du limitrophe (voisin)';
COMMENT ON COLUMN bordering.check_list_operation_id IS 'Référence à la liste de contrôle associée';

-- 13. Table conflict
-- Description: Gestion des conflits fonciers entre parties
-- Table conflict
CREATE TABLE conflict (
                          id BIGSERIAL PRIMARY KEY,
                          conflict_party VARCHAR(255),
                          first_conflict_party_nup VARCHAR(255),
                          first_conflict_party_occupation_duration_in_month VARCHAR(255),
                          second_conflict_party_nup VARCHAR(255),
                          second_conflict_party_occupation_duration_in_month VARCHAR(255),
                          conflict_object VARCHAR(255),
                          right_claimed VARCHAR(255),
                          right_claimed_origin VARCHAR(255),
                          institution_involved VARCHAR(255),
                          seizure_proof VARCHAR(255),
                          exhibit_and_evidence VARCHAR(255),
                          photo_of_proof BYTEA,
                          procedure_status VARCHAR(255),
                          settlement_date DATE,
                          settlement_compromise_nature VARCHAR(255),
                          settlement_actor VARCHAR(255),
                          regulation_witnesses VARCHAR(255),
                          final_decision_proof VARCHAR(255),
                          settlement_proof_photo BYTEA,
                          right_restriction_type VARCHAR(255),
                          currently_use_for VARCHAR(255),
                          agricultural_development_type VARCHAR(255),
                          point_of_attention VARCHAR(255),
                          mode_acquisition VARCHAR(255),
                          si_heritage_de_qui VARCHAR(255),
                          si_heritage_date_deces DATE,
                          girl_count INTEGER,
                          boy_count INTEGER,
                          date_acquisition DATE,
                          type_preuve_acquisition VARCHAR(255),
                          photo_preuve_acquisition BYTEA,
                          photo_temoignage BYTEA,
                          photo_fiche_temoignage BYTEA,
                          reg_user_id VARCHAR(50) NOT NULL,
                          date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          mod_user_id VARCHAR(50),
                          date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);
COMMENT ON TABLE conflict IS 'Gestion des conflits fonciers - informations sur les litiges entre parties, leur résolution et les preuves';

COMMENT ON COLUMN conflict.conflict_party IS 'Parties impliquées dans le conflit';
COMMENT ON COLUMN conflict.first_conflict_party_nup IS 'Numéro Unique Parcellaire de la première partie';
COMMENT ON COLUMN conflict.second_conflict_party_nup IS 'Numéro Unique Parcellaire de la seconde partie';
COMMENT ON COLUMN conflict.conflict_object IS 'Objet du conflit';
COMMENT ON COLUMN conflict.right_claimed IS 'Droit revendiqué';
COMMENT ON COLUMN conflict.institution_involved IS 'Institution saisie pour le règlement';
COMMENT ON COLUMN conflict.seizure_proof IS 'Preuve de la saisine';
COMMENT ON COLUMN conflict.procedure_status IS 'Statut de la procédure';
COMMENT ON COLUMN conflict.settlement_date IS 'Date de règlement du conflit';
COMMENT ON COLUMN conflict.mode_acquisition IS 'Mode d''acquisition du terrain';
COMMENT ON COLUMN conflict.girl_count IS 'Nombre de filles (pour succession)';
COMMENT ON COLUMN conflict.boy_count IS 'Nombre de garçons (pour succession)';

-- 14. Table finding
-- Description: Constatations foncières sur le terrain
-- Table finding
CREATE TABLE finding (
                         id BIGSERIAL PRIMARY KEY,
                         nup VARCHAR(255) NOT NULL,
                         region VARCHAR(255),
                         prefecture VARCHAR(255),
                         commune VARCHAR(255),
                         canton VARCHAR(255) NOT NULL,
                         locality VARCHAR(255) NOT NULL,
                         person_type VARCHAR(50),
                         uin VARCHAR(255) NOT NULL,
                         has_conflict BOOLEAN NOT NULL,
                         first_check_list_operation_id BIGINT,
                         last_check_list_operation_id BIGINT,
                         conflict_id BIGINT,
                         surface VARCHAR(255),
                         land_form VARCHAR(255),
                         synchro_batch_number VARCHAR(255),
                         synchro_packet_number VARCHAR(255),
                         operator_agent VARCHAR(255),
                         reg_user_id VARCHAR(50) NOT NULL,
                         date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         mod_user_id VARCHAR(50),
                         date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                         CONSTRAINT fk_finding_first_checklist FOREIGN KEY (first_check_list_operation_id) REFERENCES check_list_operation(id),
                         CONSTRAINT fk_finding_last_checklist FOREIGN KEY (last_check_list_operation_id) REFERENCES check_list_operation(id),
                         CONSTRAINT fk_finding_conflict FOREIGN KEY (conflict_id) REFERENCES conflict(id)
);
COMMENT ON TABLE finding IS 'Constatations foncières - informations collectées lors des visites terrain (parcelle, propriétaire, conflits)';

COMMENT ON COLUMN finding.nup IS 'Numéro Unique Parcellaire (NUP)';
COMMENT ON COLUMN finding.region IS 'Région de la parcelle';
COMMENT ON COLUMN finding.prefecture IS 'Préfecture de la parcelle';
COMMENT ON COLUMN finding.commune IS 'Commune de la parcelle';
COMMENT ON COLUMN finding.canton IS 'Canton de la parcelle';
COMMENT ON COLUMN finding.locality IS 'Localité ou village';
COMMENT ON COLUMN finding.person_type IS 'Type de personne (propriétaire)';
COMMENT ON COLUMN finding.uin IS 'NIU du propriétaire';
COMMENT ON COLUMN finding.has_conflict IS 'Indique si la parcelle a un conflit';
COMMENT ON COLUMN finding.surface IS 'Surface de la parcelle';
COMMENT ON COLUMN finding.land_form IS 'Forme du terrain';

-- 15. Table synchro_history
-- Description: Historique des synchronisations de données
-- Table synchro_history
CREATE TABLE synchro_history (
                                 id BIGSERIAL PRIMARY KEY,
                                 batch_number TEXT UNIQUE,
                                 total_offline_count INTEGER NOT NULL DEFAULT 0,
                                 synchro_candidate_count INTEGER NOT NULL DEFAULT 0,
                                 total_received_count INTEGER DEFAULT 0,
                                 success_packet_count INTEGER DEFAULT 0,
                                 failed_packet_count INTEGER DEFAULT 0,
                                 duplicated_packet_count INTEGER DEFAULT 0,
                                 pending_packet_count INTEGER DEFAULT 0,
                                 packets_number TEXT,
                                 init_date DATE DEFAULT CURRENT_DATE,
                                 first_packet_date DATE DEFAULT CURRENT_DATE,
                                 is_finished BOOLEAN DEFAULT FALSE,
                                 operator_agent VARCHAR(255),
                                 synchro_status VARCHAR(50) DEFAULT 'PENDING',
                                 last_packet_date TIMESTAMP,
                                 type VARCHAR(50),
                                 reg_user_id VARCHAR(50) NOT NULL,
                                 date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 mod_user_id VARCHAR(50),
                                 date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);

COMMENT ON TABLE synchro_history IS 'Historique des synchronisations de données entre serveur central et agents terrain (batchs et paquets)';

COMMENT ON COLUMN synchro_history.batch_number IS 'Numéro du lot de synchronisation';
COMMENT ON COLUMN synchro_history.total_offline_count IS 'Nombre total d''enregistrements hors ligne';
COMMENT ON COLUMN synchro_history.synchro_candidate_count IS 'Nombre d''enregistrements candidats à la synchronisation';
COMMENT ON COLUMN synchro_history.total_received_count IS 'Nombre total de paquets reçus';
COMMENT ON COLUMN synchro_history.success_packet_count IS 'Nombre de paquets synchronisés avec succès';
COMMENT ON COLUMN synchro_history.failed_packet_count IS 'Nombre de paquets en échec';
COMMENT ON COLUMN synchro_history.duplicated_packet_count IS 'Nombre de paquets en double';
COMMENT ON COLUMN synchro_history.pending_packet_count IS 'Nombre de paquets en attente';
COMMENT ON COLUMN synchro_history.packets_number IS 'Liste des numéros de paquets (concaténés)';
COMMENT ON COLUMN synchro_history.is_finished IS 'Indique si la synchronisation est terminée';
COMMENT ON COLUMN synchro_history.synchro_status IS 'Statut de la synchronisation (PENDING, UPLOADING, FINISHED, ERROR)';
COMMENT ON COLUMN synchro_history.type IS 'Type de synchronisation Acteur ou constatation';
-- =====================================================
-- Tables du module "common.securities"
-- =====================================================

-- Table user_profil
CREATE TABLE user_profil (
                             proid BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) UNIQUE,
                             reg_user_id VARCHAR(50) NOT NULL,
                             date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             mod_user_id VARCHAR(50),
                             date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);

-- Table user_permission
CREATE TABLE user_permission (
                                 permid BIGSERIAL PRIMARY KEY,
                                 permnam VARCHAR(255),
                                 permdfltnam VARCHAR(255),
                                 reg_user_id VARCHAR(50) NOT NULL,
                                 date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 mod_user_id VARCHAR(50),
                                 date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);

-- Table user_account
CREATE TABLE user_account (
                              accid BIGSERIAL PRIMARY KEY,
                              accuser VARCHAR(20) NOT NULL,
                              accpass VARCHAR(120) NOT NULL,
                              active BOOLEAN DEFAULT FALSE,
                              activation_key VARCHAR(20),
                              password_update_key VARCHAR(20),
                              password_update_time TIMESTAMP,
                              failed_connexion_attempt INTEGER,
                              proid BIGINT NOT NULL,
                              reg_user_id VARCHAR(50) NOT NULL,
                              date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              mod_user_id VARCHAR(50),
                              date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                              CONSTRAINT fk_user_account_profil FOREIGN KEY (proid) REFERENCES user_profil(proid)
);

-- Table users
CREATE TABLE users (
                       useid BIGSERIAL PRIMARY KEY,
                       usefstnam VARCHAR(20) NOT NULL,
                       uselstnam VARCHAR(20) NOT NULL,
                       usegend VARCHAR(10) NOT NULL,
                       usephon VARCHAR(15) NOT NULL,
                       useeml VARCHAR(50) NOT NULL UNIQUE,
                       accid BIGINT UNIQUE,
                       reg_user_id VARCHAR(50) NOT NULL,
                       date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       mod_user_id VARCHAR(50),
                       date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
                       CONSTRAINT fk_users_account FOREIGN KEY (accid) REFERENCES user_account(accid)
);

-- Table account_permission
CREATE TABLE account_permission (
                                    uacc_perms_id BIGSERIAL PRIMARY KEY,
                                    accid BIGINT,
                                    permid BIGINT,
                                    CONSTRAINT fk_account_permission_account FOREIGN KEY (accid) REFERENCES user_account(accid),
                                    CONSTRAINT fk_account_permission_permission FOREIGN KEY (permid) REFERENCES user_permission(permid),
                                    CONSTRAINT uk_account_permission UNIQUE (accid, permid)
);

-- Table profil_permission
CREATE TABLE profil_permission (
                                   upro_perms_id BIGSERIAL PRIMARY KEY,
                                   proid BIGINT,
                                   permid BIGINT,
                                   CONSTRAINT fk_profil_permission_profil FOREIGN KEY (proid) REFERENCES user_profil(proid),
                                   CONSTRAINT fk_profil_permission_permission FOREIGN KEY (permid) REFERENCES user_permission(permid),
                                   CONSTRAINT uk_profil_permission UNIQUE (proid, permid)
);

-- Table refresh_token
CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT,
                               token VARCHAR(255) NOT NULL,
                               expiry_date TIMESTAMP NOT NULL,
                               CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(useid)
);

-- Table deployment_licence
CREATE TABLE deployment_licence (
                                    id BIGSERIAL PRIMARY KEY,
                                    society_name VARCHAR(255),
                                    activation_code VARCHAR(255),
                                    issued_date DATE,
                                    renew_date DATE
);

-- Table licence
CREATE TABLE licence (
                         id BIGSERIAL PRIMARY KEY,
                         activation_code VARCHAR(255),
                         expiration_date DATE,
                         renewable BOOLEAN,
                         used BOOLEAN DEFAULT FALSE
);

-- Table parameter
CREATE TABLE parameter (
                           parid BIGSERIAL PRIMARY KEY,
                           parkey VARCHAR(255) NOT NULL UNIQUE,
                           parval VARCHAR(255),
                           pardesc VARCHAR(255),
                           reg_user_id VARCHAR(50) NOT NULL,
                           date_reg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           mod_user_id VARCHAR(50),
                           date_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           visibility VARCHAR(50) NOT NULL DEFAULT 'ENABLED'
);

-- =====================================================
-- Création des séquences
-- =====================================================

CREATE SEQUENCE IF NOT EXISTS actor_sequence_generator START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS fingerprint_sequence_generator START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS sequence_generator START WITH 1 INCREMENT BY 50;

-- =====================================================
-- Création des index pour optimiser les performances
-- =====================================================

-- Index pour abstract_actor
CREATE INDEX idx_abstract_actor_uin ON abstract_actor(uin);
CREATE INDEX idx_abstract_actor_type ON abstract_actor(type);
CREATE INDEX idx_abstract_actor_registration_status ON abstract_actor(registration_status);
CREATE INDEX idx_abstract_actor_rid ON abstract_actor(rid);

-- Index pour actor
CREATE INDEX idx_actor_uin ON actor(uin);
CREATE INDEX idx_actor_type ON actor(type);

-- Index pour registration
CREATE INDEX idx_registration_uin ON registration(uin);

-- Index pour person
CREATE INDEX idx_person_uin ON person(uin);
CREATE INDEX idx_person_rid ON person(rid);
CREATE INDEX idx_person_email ON person(email);
CREATE INDEX idx_person_primary_phone ON person(primary_phone);

-- Index pour informal_group
CREATE INDEX idx_informal_group_uin ON informal_group(uin);
CREATE INDEX idx_informal_group_phone ON informal_group(phone_number);

-- Index pour private_legal_entity
CREATE INDEX idx_private_legal_uin ON private_legal_entity(uin);
CREATE INDEX idx_private_legal_company ON private_legal_entity(company_name);

-- Index pour public_legal_entity
CREATE INDEX idx_public_legal_uin ON public_legal_entity(uin);

-- Index pour fingerprint_store
CREATE INDEX idx_fingerprint_rid ON fingerprint_store(rid);
CREATE INDEX idx_fingerprint_actor ON fingerprint_store(actor_id);

-- Index pour fingerprint_matching_history
CREATE INDEX idx_fingerprint_history_rid ON fingerprint_matching_history(rid);
CREATE INDEX idx_fingerprint_history_status ON fingerprint_matching_history(status);

-- Index pour check_list_operation
CREATE INDEX idx_checklist_mayor ON check_list_operation(mayor_uin);
CREATE INDEX idx_checklist_owner ON check_list_operation(owner_uin);
CREATE INDEX idx_checklist_geometer ON check_list_operation(geometer_uin);

-- Index pour bordering
CREATE INDEX idx_bordering_uin ON bordering(uin);
CREATE INDEX idx_bordering_checklist ON bordering(check_list_operation_id);

-- Index pour conflict
CREATE INDEX idx_conflict_party ON conflict(conflict_party);

-- Index pour finding
CREATE INDEX idx_finding_nup ON finding(nup);
CREATE INDEX idx_finding_uin ON finding(uin);
CREATE INDEX idx_finding_has_conflict ON finding(has_conflict);

-- Index pour synchro_history
CREATE INDEX idx_synchro_batch ON synchro_history(batch_number);
CREATE INDEX idx_synchro_status ON synchro_history(synchro_status);

-- Index pour les tables de sécurité
CREATE INDEX idx_user_account_username ON user_account(accuser);
CREATE INDEX idx_user_account_active ON user_account(active);
CREATE INDEX idx_users_email ON users(useeml);
CREATE INDEX idx_users_phone ON users(usephon);
CREATE INDEX idx_refresh_token_token ON refresh_token(token);
CREATE INDEX idx_parameter_key ON parameter(parkey);