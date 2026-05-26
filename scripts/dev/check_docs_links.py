#!/usr/bin/env python3
"""Validate relative markdown links under docs/ (AUTO-4 lite)."""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DOCS = ROOT / "docs"
LINK = re.compile(r"\[[^\]]*\]\(([^)]+)\)")


def is_external(target: str) -> bool:
    lowered = target.lower()
    return lowered.startswith(("http://", "https://", "mailto:"))


def resolve(from_file: Path, target: str) -> Path:
    path_part = target.split("#", 1)[0]
    if not path_part:
        return from_file
    return (from_file.parent / path_part).resolve()


def main() -> int:
    broken: list[str] = []
    for md in sorted(DOCS.rglob("*.md")):
        text = md.read_text(encoding="utf-8")
        for match in LINK.finditer(text):
            target = match.group(1).strip()
            if not target or is_external(target):
                continue
            resolved = resolve(md, target)
            if not resolved.exists():
                rel = md.relative_to(ROOT)
                broken.append(f"{rel}: ({target})")

    if broken:
        print("[check_docs_links] Broken relative links:", file=sys.stderr)
        for line in broken:
            print(f"  {line}", file=sys.stderr)
        return 1

    print(f"[check_docs_links] OK — scanned {len(list(DOCS.rglob('*.md')))} files under docs/")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
