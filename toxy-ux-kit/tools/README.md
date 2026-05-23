# Export tools

## export-android-colors.py

Generates Android `colors.xml` fragments from JSON tokens.

```bash
python toxy-ux-kit/tools/export-android-colors.py
```

Output:

```
tools/export/output/
├── toxy_colors.xml      # Brand chrome
└── agp_glucose_colors.xml   # Medical glucose ranges
```

### Workflow

1. Designer / dev updates `tokens/*.json`
2. Run export script
3. Diff output against `mobile/src/main/res/values/colors.xml`
4. Merge approved changes into app module

**The app does not auto-sync** — human review required.

## Future tools (backlog)

| Tool | Purpose |
|------|---------|
| `export-wear-tile-theme.kt` | Protolayout constants from tokens |
| `lint-agp-colors.py` | Fail CI if glucose TextView uses `wg7_accent` |
| `tokens-validate.py` | JSON schema validation |
