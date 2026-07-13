# LandReg Mobile

Application Android utilisée sur le terrain pour l'enregistrement des acteurs et la synchronisation offline.

## API d'enregistrement acteur (v1 / v2)

La création d'acteurs lors de la synchronisation peut utiliser deux endpoints backend :

| Version | Endpoint | Format |
|---------|----------|--------|
| **v1** (défaut) | `POST /land-reg/api/v1/actors` | JSON avec empreintes en base64 |
| **v2** | `POST /land-reg/api/v2/actors` | Multipart : métadonnées JSON + fichiers binaires |

La bascule est gérée par `ActorRegistrationFacade`, qui lit la configuration via `ActorRegistrationConfig`.

> **Note :** la mise à jour d'un acteur existant (`PUT /land-reg/api/v1/actors/{id}`) reste sur **v1** tant que le backend n'expose pas d'équivalent v2.

## Activer l'API v2

Trois mécanismes sont disponibles, par ordre de priorité :

1. **SharedPreferences** (runtime, prioritaire)
2. **Fichier assets** embarqué dans l'APK
3. **BuildConfig** (compilation)

### 1. Runtime — SharedPreferences

```java
import com.lesadrax.registrationclient.data.config.ActorRegistrationConfig;

// Activer v2
ActorRegistrationConfig.setV2Enabled(context, true);

// Revenir à v1
ActorRegistrationConfig.setV2Enabled(context, false);
```

Clé utilisée : `actor_registration_use_v2` (fichier prefs `registration_config`).

### 2. Assets — configuration embarquée

Fichier : `app/src/main/assets/registration_config.properties`

```properties
# false = v1 (défaut), true = v2 multipart
actor.registration.v2.enabled=false
```

Modifier cette valeur puis reconstruire l'APK. Surchargée si une valeur est déjà présente dans SharedPreferences.

### 3. Build — BuildConfig

Fichier : `app/build.gradle.kts`

```kotlin
defaultConfig {
    buildConfigField("boolean", "USE_ACTOR_REGISTRATION_V2", "true")
}
```

Utilisé comme valeur par défaut si le fichier assets est absent ou illisible, et si SharedPreferences n'a pas encore de valeur explicite.

## Vérifier la version active

```java
ActorRegistrationFacade facade = new ActorRegistrationFacade(context);
boolean v2Active = facade.isV2Enabled();
```

## Prérequis backend pour v2

Avant d'activer v2 côté mobile, le backend doit exposer :

- `POST /land-reg/api/v2/actors` (multipart)
- MinIO / claim-check configuré si le pipeline AFIS v2 est déployé

Voir la documentation projet : [Integration Architecture](../docs/integration-architecture.md).

## Fichiers concernés

| Fichier | Rôle |
|---------|------|
| `data/config/ActorRegistrationConfig.java` | Lecture de la configuration |
| `data/network/ActorRegistrationFacade.java` | Bascule v1 / v2 à l'appel API |
| `data/mapper/ActorRegistrationMapper.java` | Construction des DTOs depuis le formulaire local |
| `data/model/dto/` | DTOs typés (`ActorRegistrationRequest`, `ActorRegistrationRequestV2`, …) |
