#!/bin/bash

# Vérification de l'argument
if [ -z "$1" ]; then
    echo "Erreur : ID du run manquant."
    echo "Usage : ./deploy.sh ID_DU_RUN"
    exit 1
fi

RUN_ID=$1

echo "--- Démarrage du déploiement (Run ID: $RUN_ID) ---"

echo "[1/4] Nettoyage des anciens dossiers et JARs..."
rm -rf optimize-land-reg-jar/
rm optimize-land-reg-0.0.1-SNAPSHOT.jar
rm LangRegDeployments/jar/optimize-land-reg.jar

echo "[2/4] Téléchargement des nouveaux artefacts via GitHub..."
gh run download "$RUN_ID" -n "optimize-land-reg-jar" -R AQUILA04/LandReg

echo "[3/4] Copie des fichiers JAR vers le dossier de déploiement..."
cp optimize-land-reg-0.0.1-SNAPSHOT.jar LangRegDeployments/jar/optimize-land-reg.jar

echo "[4/4] Redémarrage des services système..."
sudo systemctl restart lang-reg.service

echo "--- Déploiement terminé avec succès pour le Run $RUN_ID ! ---"