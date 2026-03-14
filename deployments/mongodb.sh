#!/bin/bash

# Vérification de l'argument
if [ -z "$1" ]; then
    echo "Erreur : ID ou le NOM du conteneur manquant."
    echo "Usage : ./mongodb.sh ID_CONTAINER"
    exit 1
fi

RUN_ID=$1

docker exec -it f7a0b3d966c6 mongosh