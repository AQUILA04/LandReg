-- Mise à jour de la table actor
-- 1. Personnes physiques
UPDATE actor
SET name = TRIM(CONCAT(p.firstname, ' ', p.lastname)),
    phone = p.primary_phone
FROM person p
WHERE actor.physical_person_id = p.id
  AND actor.type = 'PHYSICAL_PERSON'
  AND actor.name IS NULL;

-- 2. Groupes informels
UPDATE actor
SET name = ig.group_name,
    phone = ig.phone_number
FROM informal_group ig
WHERE actor.informal_group_id = ig.id
  AND actor.type = 'INFORMAL_GROUP'
  AND actor.name IS NULL;

-- 3. Entités légales privées
UPDATE actor
SET name = ple.company_name,
    phone = ple.phone_number
FROM private_legal_entity ple
WHERE actor.private_legal_entity_id = ple.id
  AND actor.type = 'PRIVATE_LEGAL_ENTITY'
  AND actor.name IS NULL;

-- 4. Entités légales publiques
UPDATE actor
SET name = pub.name,
    phone = pub.phone_number
FROM public_legal_entity pub
WHERE actor.public_legal_entity_id = pub.id
  AND actor.type = 'PUBLIC_LEGAL_ENTITY'
  AND actor.name IS NULL;

-- Mise à jour de la table registration (si applicable)
-- 1. Personnes physiques
UPDATE registration
SET name = TRIM(CONCAT(p.firstname, ' ', p.lastname)),
    phone = p.primary_phone
FROM person p
WHERE registration.physical_person_id = p.id
  AND registration.type = 'PHYSICAL_PERSON'
  AND registration.name IS NULL;

-- 2. Groupes informels
UPDATE registration
SET name = ig.group_name,
    phone = ig.phone_number
FROM informal_group ig
WHERE registration.informal_group_id = ig.id
  AND registration.type = 'INFORMAL_GROUP'
  AND registration.name IS NULL;

-- 3. Entités légales privées
UPDATE registration
SET name = ple.company_name,
    phone = ple.phone_number
FROM private_legal_entity ple
WHERE registration.private_legal_entity_id = ple.id
  AND registration.type = 'PRIVATE_LEGAL_ENTITY'
  AND registration.name IS NULL;

-- 4. Entités légales publiques
UPDATE registration
SET name = pub.name,
    phone = pub.phone_number
FROM public_legal_entity pub
WHERE registration.public_legal_entity_id = pub.id
  AND registration.type = 'PUBLIC_LEGAL_ENTITY'
  AND registration.name IS NULL;
