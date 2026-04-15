# Integration Architecture

## Overview
This document outlines how the various parts of the LandReg project communicate.

## Integration Points
1. **Mobile to LandReg APIs**: The Android mobile app sends batched payload requests to `optimize-land-reg` API to sync actor data and fingerprint captures.
2. **LandReg to AFIS**: When `optimize-land-reg` needs to verify or extract templates for fingerprints, it requests verification from `afis-service` (or `afis-master`).
3. **Kafka Event Bus**: Async communication between microservices handles large bulk jobs (e.g. fingerprint matching history).
