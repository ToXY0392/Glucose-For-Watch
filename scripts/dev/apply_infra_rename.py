#!/usr/bin/env python3
"""Apply glucose-for-watch naming and branch model (infra paths only)."""
from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

REPLACEMENTS = [
    ("glucose-for-watch-sandbox-guard", "glucose-for-watch-sandbox-guard"),
    ("glucose-for-watch-ui-ux-kit-scope", "glucose-for-watch-ui-ux-kit-scope"),
    ("@glucose-for-watch-", "@glucose-for-watch-"),
    ("glucose-for-watch-", "glucose-for-watch-"),
    ("sandbox/mobile-app", "sandbox/mobile-app"),
    ("sandbox/wear-app", "sandbox/wear-app"),
    ("sandbox/ui-ux-kit", "sandbox/ui-ux-kit"),
    ("sandbox/qa-hardware", "sandbox/qa-hardware"),
    ("sandbox/*", "sandbox/*"),
    ("origin/develop/integration", "origin/develop/integration"),
    ("→ `develop/integration`", "→ `develop/integration`"),
    (" to develop/integration", " to develop/integration"),
    (" to `develop/integration`", " to `develop/integration`"),
    ("`develop/integration`", "`develop/integration`"),
    ("branch develop/integration", "branch develop/integration"),
    ("from develop/integration", "from develop/integration"),
    ("on develop/integration", "on develop/integration"),
    ("- develop/integration", "- develop/integration"),
    ("'sandbox/**'", "'sandbox/**'"),
    ("[gfw]", "[gfw]"),
    ("# Agent guide — Glucose For Watch", "# Agent guide — Glucose For Watch"),
    ("blob/develop/integration/", "blob/develop/integration/"),
    ("default: develop/integration", "default: develop/integration"),
    ("Name: **`Glucose For Watch — post-v0.6`**", "Name: **`Glucose For Watch — post-v0.6`**"),
    ("Link to repo `Glucose-For-Watch`", "Link to repo `Glucose-For-Watch`"),
    ("glucose-for-watch-architecture", "glucose-for-watch-architecture"),
    ("Glucose-For-Watch` locally", "Glucose-For-Watch` locally"),
]

SKIP_SUBSTR = (
    "docs/qa/sessions/",
    "docs/qa/soak-runs/",
    "docs/qa/incidents/",
    "docs/qa/2026",
    "docs/qa/bloc-c",
    "docs/qa/G-F3",
)

ROOT_FILES = ["AGENTS.md", "CONTRIBUTING.md", "README.md", "CHANGELOG.md"]


def process(path: Path) -> bool:
    rel = str(path.relative_to(ROOT)).replace("\\", "/")
    if any(s in rel for s in SKIP_SUBSTR):
        return False
    if path.suffix not in {".md", ".mdc", ".yml", ".yaml", ".sh", ".ps1", ".py", ".json", ".txt"}:
        return False
    text = path.read_text(encoding="utf-8")
    orig = text
    for old, new in REPLACEMENTS:
        text = text.replace(old, new)
    if text != orig:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    dirs = [ROOT / ".cursor", ROOT / "docs", ROOT / ".github", ROOT / "scripts" / "dev", ROOT / "scripts" / "qa", ROOT / "scripts" / "assets"]
    changed: list[str] = []
    for base in dirs:
        if not base.exists():
            continue
        for path in base.rglob("*"):
            if path.is_file() and process(path):
                changed.append(str(path.relative_to(ROOT)))
    for name in ROOT_FILES:
        p = ROOT / name
        if p.exists() and process(p):
            changed.append(name)
    print(f"Updated {len(changed)} files")


if __name__ == "__main__":
    main()
