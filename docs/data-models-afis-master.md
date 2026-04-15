# Data Models: AFIS Master

## Database Schema Overview
The `afis-master` module uses MongoDB (`@Document`).

## Entites
- **ProcessingFingerprint**: Stores fingerprints undergoing processing.
- **MatcherJobHistory**: Tracks fingerprint matching jobs.
- **FingerprintStore**: Core storage for fingerprints.
- **Authority**: User roles/authorities.
