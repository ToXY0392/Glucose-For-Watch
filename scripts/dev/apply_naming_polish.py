#!/usr/bin/env python3
"""Final prose polish: Glucose For Watch -> Glucose For Watch (agent/docs/scripts)."""
from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

TEXT_FILES = [
    *ROOT.glob(".cursor/skills/**/SKILL.md"),
    *ROOT.glob(".cursor/rules/*.mdc"),
    ROOT / "docs/plan/GITHUB-SETUP.md",
    ROOT / "toxy-ux-kit/README.md",
    ROOT / "toxy-ux-kit/CHANGELOG.md",
    ROOT / "scripts/qa/stability-gate.ps1",
    ROOT / "scripts/qa/generate_preview_gallery.py",
    ROOT / "toxy-ux-kit/tools/lint-agp-colors.py",
    *ROOT.glob("scripts/release/*.sh"),
]

REPLACEMENTS = [
    ("[glucose-for-watch]", "[gfw]"),
    ("glucose-for-watch-agp-color-guard", "glucose-for-watch-agp-color-guard"),
    ("Glucose For Watch", "Glucose For Watch"),
    ("v0.5.0 or v0.6.0", "v0.7.0 post-v0.6"),
    (
        "- Local folder: **`Glucose For Watch`** (unchanged)",
        "- Local folder: **`Glucose-For-Watch`**",
    ),
]


def main() -> None:
    changed = 0
    for path in TEXT_FILES:
        if not path.is_file():
            continue
        text = path.read_text(encoding="utf-8")
        original = text
        for old, new in REPLACEMENTS:
            text = text.replace(old, new)
        if text != original:
            path.write_text(text, encoding="utf-8", newline="\n")
            changed += 1
            print(f"updated {path.relative_to(ROOT)}")
    print(f"done: {changed} files")


if __name__ == "__main__":
    main()
