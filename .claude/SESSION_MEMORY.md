# Session memory
_Màj : 2026-11-27 · branche : chore/bloc-s-audit-optimisations_

## En cours
- 2026-11-27 Audit complet appliqué sur `chore/bloc-s-audit-optimisations` (6 commits, PR vers `develop/integration`). Rien installé sur appareil.

## Décisions
- 2026-11-26 **Bluetooth / G7 direct en pause** — pourquoi : Phase 0 bloquée (pas de capteur G7 de test isolé). Ne rien proposer sur `wear/.../direct/`, l'arbitre ou l'enveloppe source sans reprise explicite.
- 2026-11-26 Skills : source unique `.claude/skills/`, miroir généré `.cursor/skills/` (`scripts/dev/sync_skills.py`), CI « Verify agent skills » — PR #125, #126. Détails : `docs/plan/SKILLS-PLAN.md`.
- 2026-11-26 Hook PreToolUse : confirmation avant `installGlucoseForWatchDebug` / `adb install` / `adb uninstall` (`.claude/settings.json`).
- 2026-11-26 Plugins caveman / claude-mem non installés — pourquoi : absents du marketplace officiel ; claude-mem tiers exige worker + DB externe.
- 2026-11-26 Rapport consultant : https://claude.ai/artifact/2RjEK1kUCdnG2GEX4pmmfT
- 2026-11-26 Back-merge `main` → `develop/integration` fait (PR #127) ; plus de dérive.
- 2026-11-26 Build `:mobile` réparé : caches `.gradle/`, `.gradle-user*` et `build/` supprimés (référençaient l'ancien chemin `Desktop\THP\Projects`). `:mobile:assembleDebug :wear:assembleDebug` OK.
- 2026-11-26 Allowlist Claude Code (`.claude/settings.json`) : git/gh lecture seule + `:<module>:testDebugUnitTest` — PR #129. `git branch` exclu (permet `-D`).
- 2026-11-26 Tendance Dexcom absente / NotComputable / RateOutOfRange / inconnue → `NONE` (pas de flèche montre, « tendance indisponible » téléphone), plus jamais `FLAT` — pourquoi : fausse flèche « stable ». PR #130.
- 2026-11-26 Dependabot : #114 (constraintlayout 2.2.2) fusionnée ; #115 (kapt 2.4.21) fermée — plugin kapt déclaré `apply false`, jamais appliqué ; sa version doit suivre Kotlin.

- 2026-11-26 Déclaration kapt inutilisée retirée de `build.gradle.kts` ; Dependabot cible `develop/integration` (`target-branch`) — pourquoi : plus de PR Dependabot sur `main` ni de back-merge après chaque bump.
- 2026-11-26 Build : AGP déclaré 9.4.1 partout, Robolectric 4.17 (mobile+wear), ktlint plugin 14.2.0 / ktlint 1.8.0 (n'analyse que les `.kts` racine), configuration cache activé — PR #133. 4 opt-outs AGP 9 retirés — PR #136.
- 2026-11-26 Gardés exprès : `android.newDsl=false` + `builtInKotlin=false` (le nouveau DSL refuse `org.jetbrains.kotlin.android` ; passer au Kotlin intégré = retirer ce plugin des 8 modules) ; options `android.r8.*` (release seul, régression visible seulement sur appareil).
- 2026-11-26 Tests ajoutés : parseur OCR `watch-install`, valeurs du contrat Data Layer figées (changer une clé casse une montre déjà installée) ; CI les lance — PR #134.
- 2026-11-26 `AppSettingsStore` : prefs chiffrées créées une fois par processus (≈10 appelants) — PR #137. Migration hors `security-crypto` (déprécié) **reportée** — pourquoi : pas de faille connue, Keystore non testable en Robolectric, install interdite pendant le soak, échec = identifiants Dexcom perdus.

- 2026-11-27 **toxy-ux-kit supprimé** — pourquoi : aucun lien de build, valeurs divergentes partout. Règles AGP + surfaces dans `docs/design/`. Source de vérité couleurs : `AgpGlucoseColors` ; lint `scripts/dev/lint_agp_colors.py`.
- 2026-11-27 Polling Dexcom sur cadence 5 min (`GlucosePollScheduler`), seuil périmé unique 15 min (`GlucoseFreshness`), alarme 5 min quand le FGS tourne, tuile 60 s.
- 2026-11-27 ML Kit OCR non embarqué (Play Services) : APK release 50 → 5,6 Mo. kadb garde sa règle `-keep` (BouncyCastle, non testable sans appareil).
- 2026-11-27 JitPack nécessaire (kadb → `com.github.Flyfish233:spake2-java`), restreint à ce groupe. Dossiers `mipmap-anydpi-v26` à garder (sans `-v26`, AAPT ne trouve plus les icônes).
- 2026-11-27 Non fait exprès : plugin de convention pour les 8 blocs `android {}` (sur-ingénierie), ktlint sur le Kotlin des modules (reformatage massif).

## Prochaines étapes
1. Avant release, sur appareil : réglages Dexcom rechargés après mise à jour et reboot (#137) ; OCR montre après téléchargement du modèle Play Services ; thème fenêtre framework (barre d'état, dialogue OCR) ; cadran accueil AGP (jaune sur fond clair) ; logs `poll` sur 1 h ; arrêt FGS 6 h Android 15 puis reprise alarme/WorkManager.
2. Chantier séparé : Kotlin intégré AGP 9 (`builtInKotlin` + `newDsl`), puis options R8 avec test release sur appareil.
3. Branches locales `docs`, `backup/*` : contenu unique non retrouvé ailleurs — décision utilisateur avant suppression.

## Pièges connus
- `main` protégée : PR obligatoire, push direct refusé. Ce qui est fusionné sur `main` doit revenir sur `develop/integration` (back-merge), sinon dérive.
- `core.autocrlf=true` : `.gitattributes` force LF pour `.claude/**` — un SKILL.md en CRLF n'est pas lu par Claude Code (`--lint` le signale).
- PowerShell 5.1 + `git commit -m` / `gh --body` avec guillemets internes : arguments cassés → passer par `-F` / `--body-file` (fichier UTF-8 **sans BOM**, sinon BOM dans le titre du commit).
- Chemins longs Windows dans les caches Gradle : `Remove-Item` échoue → `[System.IO.Directory]::Delete("\\?\<chemin>", $true)`.