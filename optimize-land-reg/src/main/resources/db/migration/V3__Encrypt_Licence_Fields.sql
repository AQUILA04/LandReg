-- Modification des colonnes pour supporter le texte crypté (Base64 peut dépasser 255 caractères)
-- et pour permettre à expiration_date (précédemment de type DATE) de stocker des chaînes de caractères

ALTER TABLE licence ALTER COLUMN activation_code TYPE TEXT;

-- Ajout d'une clause USING pour forcer le cast de la date existante en texte si la colonne contient déjà des données
ALTER TABLE licence ALTER COLUMN expiration_date TYPE TEXT USING expiration_date::TEXT;

ALTER TABLE deployment ALTER COLUMN activation_code TYPE TEXT;
