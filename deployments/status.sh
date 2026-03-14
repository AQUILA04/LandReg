#!/bin/bash

# Définir le mot de passe sudo
APP_SUDO=' '

# Fonction pour exécuter des commandes avec sudo
sudo_cmd() {
  echo "$APP_SUDO" | sudo -S $@
}

# Étape 1: Arrêter les services Docker et les services Linux


echo "***********************************************************************"
echo "*                                                                     *"
echo "*              STATUS DES SERVICES DE LangReg@1.0.0                   *"
echo "*                                                                     *"
echo "***********************************************************************"

docker ps

SERVICES=(
  "./service/land-reg.service"
  "./service/afis-master.service"
  "./service/afis-service.service"
)
for SERVICE in "${SERVICES[@]}"; do
  sudo_cmd systemctl status $SERVICE 
done

echo "**********************************************************************"
echo "*                                                                    *"
echo "*                                 FIN                                *"
echo "*                                                                    *"
echo "**********************************************************************"
