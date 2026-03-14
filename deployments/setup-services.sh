#!/bin/bash

# Définir le mot de passe sudo
APP_SUDO=' '

# Fonction pour exécuter des commandes avec sudo
sudo_cmd() {
  echo "$APP_SUDO" | sudo -S $@
}

# Correction des permissions sur le dossier de travail
echo "Correction des permissions sur le dossier de travail..."
sudo_cmd chmod -R 777 /home/landreg/LangRegDeployments

# Étape 1: Installer Docker et Docker Compose si non installés

echo "**********************************************************************"
echo "*                                                                    *"
echo "*  1: Vérification et installation de Docker et Docker Compose...    *"
echo "*                                                                    *"
echo "**********************************************************************"
if ! command -v docker &> /dev/null; then
  sudo_cmd apt update
  sudo_cmd apt install -y docker.io
  sudo_cmd groupadd docker
  sudo_cmd usermod -aG docker landreg
  sudo_cmd systemctl start docker
  sudo_cmd systemctl enable docker
fi

if ! command -v docker-compose &> /dev/null; then
  sudo_cmd curl -L "https://github.com/docker/compose/releases/download/$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4)/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo_cmd chmod +x /usr/local/bin/docker-compose
fi

# Étape 2: Démarrer les services avec Docker Compose
echo "********************************************"
echo "2: Démarrage des services avec Docker Compose..."
echo "********************************************"

echo "**********************************************************************"
echo "*                                                                    *"
echo "*           2: Démarrage des services avec Docker Compose..          *"
echo "*                                                                    *"
echo "**********************************************************************"
docker-compose -f ./docker/services.yml up -d

# Étape 3: Installer PostgreSQL si non installé
#echo "3: Vérification et installation de PostgreSQL..."
#if ! command -v psql &> /dev/null; then
#  sudo_cmd apt install -y postgresql postgresql-contrib
#fi

# Étape 4: Configurer PostgreSQL avant la création de la base de données
#echo "4: Configuration de PostgreSQL..."
#DB_IP=$(grep "server.ip" ./db/db.conf | cut -d '=' -f 2 | tr -d ' ')
#PG_HBA="$(sudo_cmd find /etc -name pg_hba.conf)"
#PG_CONF="$(sudo_cmd find /etc -name postgresql.conf)"
# Ajouter une règle pour toutes les connexions locales
#sudo_cmd sed -i "1ihost    all             all             127.0.0.1/32            md5" $PG_HBA
#sudo_cmd sed -i "1ilocal   all             all                                 md5" $PG_HBA
# Configurer listen_addresses
#sudo_cmd sed -i "s/^#listen_addresses = 'localhost'/listen_addresses = '*'" $PG_CONF
#sudo_cmd systemctl restart postgresql

# Étape 5: Créer la base de données et l'utilisateur
#echo "5: Création de la base de données et de l'utilisateur..."
#DB_NAME="olr_recette_db"
#DB_USER="app"
#DB_PASSWORD="L@ndRegAPP"
#sudo_cmd -u postgres psql -c "CREATE DATABASE $DB_NAME;" || echo "La base de données $DB_NAME existe déjà."
#sudo_cmd -u postgres psql -c "DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '$DB_USER') THEN CREATE USER $DB_USER WITH ENCRYPTED PASSWORD '$DB_PASSWORD'; END IF; END $$;"
#sudo_cmd -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"

# Étape 6: Configurer les services Linux pour les fichiers JAR


echo "**********************************************************************"
echo "*                                                                    *"
echo "*  3: Configuration des services Linux pour les fichiers JAR...      *"
echo "*                                                                    *"
echo "**********************************************************************"
if ! command -v java &> /dev/null; then
  sudo_cmd apt update
  sudo_cmd apt install -y openjdk-17-jdk
fi
SERVICES=(
  "./service/land-reg.service"
  "./service/afis-master.service"
  "./service/afis-service.service"
)
for SERVICE in "${SERVICES[@]}"; do
  sudo_cmd cp $SERVICE /etc/systemd/system/
  sudo_cmd systemctl daemon-reload
  sudo_cmd systemctl enable $(basename $SERVICE)
  sudo_cmd systemctl restart $(basename $SERVICE)
  sleep 10
done

# Étape 7: Vérification des services

echo "**********************************************************************"
echo "*                                                                    *"
echo "*                 4: Vérification des services...                    *"
echo "*                                                                    *"
echo "**********************************************************************"

HEALTH_ENDPOINTS=(
  "http://localhost:8081/actuator/health"
  "http://localhost:8082/management/health"
  "http://localhost:8083/management/health"
)
ALL_SERVICES_UP=true
for ENDPOINT in "${HEALTH_ENDPOINTS[@]}"; do
  RESPONSE=$(curl -s $ENDPOINT | grep '"status":"UP"')
  if [ -z "$RESPONSE" ]; then
    ALL_SERVICES_UP=false
    echo "===> Service à l'endpoint $ENDPOINT n'est pas UP."
  else
    echo "===> Service à l'endpoint $ENDPOINT est UP."
  fi
done

# Étape 8: Afficher le statut final
if $ALL_SERVICES_UP; then
  echo "===> Tous les services sont opérationnels."
else
  echo "===> Une ou plusieurs erreurs détectées dans les services."
fi

echo "**********************************************************************"
echo "*                                                                    *"
echo "*                        FIN INSTALLATION                            *"
echo "*                                                                    *"
echo "**********************************************************************"
