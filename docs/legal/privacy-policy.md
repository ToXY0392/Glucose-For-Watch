# Privacy policy

## Data processed

- Dexcom Share credentials (stored locally, encrypted)
- Glucose readings (cached locally on phone and watch)
- Sync state metadata (timestamps, ack sequence IDs, battery status)

## Principles

- No intentional public sharing of health data
- Minimize sensitive data in logs
- Protect device access and backups

## Local storage

- Phone: `EncryptedSharedPreferences` for Dexcom credentials
- Watch: `SharedPreferences` for glucose cache
- No cloud backend operated by this project (unless you configure an optional relay — not default)

## Developer practices

- Never commit credentials or real glucose values to git
- Do not publish screenshots containing real health data
- Redact serial numbers and account identifiers in bug reports

## Your responsibilities

- Secure your phone and watch with screen locks
- Revoke Share access if you uninstall or stop using the app
- Review Dexcom's own privacy policies for Share data handling
