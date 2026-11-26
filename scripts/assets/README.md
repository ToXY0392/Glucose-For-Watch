# Assets scripts

Utilities to generate or update project assets.

## Architecture diagram

Source: [docs/assets/glucose-for-watch-architecture.svg](../../docs/assets/glucose-for-watch-architecture.svg)  
Rendered PNG (README + docs): `docs/assets/glucose-for-watch-architecture.png`

```bash
# WSL / Linux (needs fonts-dejavu-core) — same render as CI
bash scripts/assets/export-architecture-diagram.sh
```

Windows: `.\scripts\assets\export-architecture-diagram.ps1` with `$env:GFW_DIAGRAM_FONT_DIR` pointing to the DejaVu fonts.

The render uses pinned DejaVu fonts only (no system fonts), so the PNG is identical on every machine. If CI reports drift, the failed run uploads the regenerated PNG as the `glucose-for-watch-architecture-png` artifact: download it and commit it.

Edit the SVG when sync classes or Data Layer paths change, then re-run the script.
