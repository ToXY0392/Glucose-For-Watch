#!/usr/bin/env python3
"""Validate toxy-ux-kit token JSON files have required structure."""

from __future__ import annotations

import json
import sys
from pathlib import Path

TOKENS = Path(__file__).resolve().parents[1] / "tokens"

REQUIRED = {
    "toxy.color.json": ["toxy", "color", "sync", "okSoft"],
    "agp.glucose.json": ["agp", "glucose", "inRange"],
    "toxy.spacing.json": ["toxy", "spacing", "touchMin"],
    "meta.json": ["version"],
}


def get_nested(data: dict, keys: list[str]) -> bool:
    node = data
    for key in keys:
        if not isinstance(node, dict) or key not in node:
            return False
        node = node[key]
    return True


def main() -> int:
    errors: list[str] = []
    for filename, path_keys in REQUIRED.items():
        path = TOKENS / filename
        if not path.is_file():
            errors.append(f"Missing {filename}")
            continue
        data = json.loads(path.read_text(encoding="utf-8"))
        if not get_nested(data, path_keys):
            errors.append(f"{filename}: expected path {'/'.join(path_keys)}")

    if errors:
        print("Token validation FAILED:", file=sys.stderr)
        for e in errors:
            print(f"  - {e}", file=sys.stderr)
        return 1

    print("Token validation OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
