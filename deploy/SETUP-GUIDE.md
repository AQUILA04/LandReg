# Guide de Configuration CI/CD — LandReg

Ce guide décrit les étapes de configuration initiale à effectuer **une seule fois** pour activer le pipeline CI/CD automatisé.

## Étape 1 : Pousser les workflows GitHub Actions

Les fichiers `.github/workflows/ci.yml` et `.github/workflows/cd.yml` doivent être poussés manuellement car ils requièrent la permission `workflows` sur le token GitHub.

```bash
# Depuis la racine du dépôt LandReg
git add .github/workflows/ci.yml .github/workflows/cd.yml
git commit -m "ci: add GitHub Actions CI/CD workflows"
git push origin main
```

## Étape 2 : Créer les environnements GitHub

Dans **Settings → Environments** du dépôt GitHub :

1. Créer l'environnement `test` (aucune protection requise)
2. Créer l'environnement `prod` (ajouter un reviewer requis pour la sécurité)

## Étape 3 : Configurer les secrets GitHub

Dans **Settings → Secrets and Variables → Actions** :

### Secrets au niveau du dépôt
| Secret | Valeur |
|---|---|
| `SSH_PRIVATE_KEY` | Contenu de la clé privée SSH ed25519 |
| `DB_USER` | Utilisateur PostgreSQL (ex: `landreg`) |
| `MONGO_USER` | Utilisateur MongoDB (ex: `root`) |

### Secrets de l'environnement `test`
| Secret | Exemple |
|---|---|
| `TEST_SERVER_HOST` | `192.168.1.100` |
| `TEST_SERVER_USER` | `ubuntu` |
| `TEST_DB_PASSWORD` | `<mot_de_passe_fort>` |
| `TEST_DB_NAME` | `landreg_test` |
| `TEST_MONGO_PASSWORD` | `<mot_de_passe_fort>` |
| `TEST_KEYCLOAK_ADMIN_PASSWORD` | `<mot_de_passe_fort>` |
| `TEST_JWT_SECRET` | `<64_caracteres_aleatoires>` |
| `TEST_APP_HOSTNAME` | `test.landreg.example.com` |
| `TEST_KEYCLOAK_HOSTNAME` | `auth.test.landreg.example.com` |

### Secrets de l'environnement `prod`
| Secret | Exemple |
|---|---|
| `PROD_SERVER_HOST` | `203.0.113.10` |
| `PROD_SERVER_USER` | `ubuntu` |
| `PROD_DB_PASSWORD` | `<mot_de_passe_fort>` |
| `PROD_DB_NAME` | `landreg_prod` |
| `PROD_MONGO_PASSWORD` | `<mot_de_passe_fort>` |
| `PROD_KEYCLOAK_ADMIN_PASSWORD` | `<mot_de_passe_fort>` |
| `PROD_JWT_SECRET` | `<64_caracteres_aleatoires>` |
| `PROD_APP_HOSTNAME` | `landreg.example.com` |
| `PROD_KEYCLOAK_HOSTNAME` | `auth.landreg.example.com` |

## Étape 4 : Préparer les VPS

Sur chaque VPS (test et prod), s'assurer que :

```bash
# L'utilisateur SSH peut exécuter sudo sans mot de passe
echo "ubuntu ALL=(ALL) NOPASSWD:ALL" | sudo tee /etc/sudoers.d/ubuntu

# Le répertoire de base existe
sudo mkdir -p /opt/landreg
sudo chown ubuntu:ubuntu /opt/landreg
```

## Étape 5 : Générer la paire de clés SSH

```bash
# Générer la clé
ssh-keygen -t ed25519 -C "landreg-cicd" -f ~/.ssh/landreg_cicd

# Afficher la clé publique (à ajouter sur les VPS)
cat ~/.ssh/landreg_cicd.pub

# Afficher la clé privée (à mettre dans le secret SSH_PRIVATE_KEY)
cat ~/.ssh/landreg_cicd

# Sur chaque VPS, ajouter la clé publique
ssh-copy-id -i ~/.ssh/landreg_cicd.pub ubuntu@<VPS_IP>
```

## Étape 6 : Premier déploiement

Une fois tout configuré, pousser sur `main` déclenche automatiquement :

1. **CI** : Build des 4 images Docker et push sur GHCR
2. **CD** : SSH sur le VPS test → bootstrap init.sh → setup-server.sh → deploy.sh

Le premier déploiement installe Docker, configure Traefik, crée les réseaux Docker et génère les fichiers `.env` avec les secrets injectés.

## Flux de déploiement en production

```
Développement → PR vers main → merge → CI build → CD deploy TEST
                                                         ↓
                                              Validation manuelle
                                                         ↓
                              git push prod/v1.0 → CI build → CD deploy PROD
                              # ou
                              GitHub Actions → Run workflow → promote (TEST→PROD)
```
