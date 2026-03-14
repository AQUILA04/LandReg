-- Création d'un autre utilisateur
CREATE USER jfassinou WITH ENCRYPTED PASSWORD 'LangReg2025';
CREATE USER lrap WITH ENCRYPTED PASSWORD 'Lrap2025';

-- Création d'une autre base de données
-- CREATE DATABASE another_db;

-- Donner les droits à l'utilisateur sur cette base
GRANT ALL PRIVILEGES ON DATABASE olr_recette_db TO jfassinou;