#!/usr/bin/env python3
"""
AGP color guard — fail CI if glucose UI uses ToXY mint instead of medical colors.

Usage:
  python toxy-ux-kit/tools/lint-agp-colors.py

See .cursor/skills/widget-g7-agp-color-guard/SKILL.md
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

GLUCOSE_KT_GLOBS = [
    "mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt",
    "wear/src/main/java/com/widgetg7/wear/tile/ToxyTileTheme.kt",
    "wear/src/main/java/com/widgetg7/wear/tile/GlucoseSimpleTileService.kt",
    "wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt",
    "wear/src/main/java/com/widgetg7/wear/display/WearGlucoseSurfaceModel.kt",
    "wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt",
    "wear/src/main/java/com/widgetg7/wear/ui/WearStatusScreen.kt",
    "wear/src/main/java/com/widgetg7/wear/ui/WearStatusUiModel.kt",
]

REQUIRED_RESOLVER = {
    ROOT / "mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt": "GlucoseRangeResolver",
    ROOT / "wear/src/main/java/com/widgetg7/wear/tile/ToxyTileTheme.kt": "GlucoseRangeResolver",
    ROOT / "wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt": "GlucoseRangeResolver",
}

HERO_ACCENT_PATTERN = re.compile(
    r"homeReadingPrimary\.setTextColor\([^)]*(wg7_accent|toxy_accent)",
    re.IGNORECASE,
)

MINT_ON_VALUE_PATTERN = re.compile(
    r"(valueText|valueColor|heroReading|displayValue).*?(0xFF34D399|#34D399|wg7_accent|toxy_accent)",
    re.IGNORECASE,
)

# Lines allowed to reference mint in glucose-adjacent files (chrome/sync only).
MINT_ALLOWLIST_SUBSTRINGS = (
    "SYNC_ACCENT",
    "SYNC_BG",
    "sync button",
    "Sync action",
    "primary = Color(0xFF34D399",  # Wear M3 chrome primary
    "never mint",
    "NOT for glucose",
)


def lint_file(path: Path) -> list[str]:
    errors: list[str] = []
    rel = path.relative_to(ROOT)
    text = path.read_text(encoding="utf-8")

    required = REQUIRED_RESOLVER.get(path)
    if required and required not in text:
        errors.append(f"{rel}: missing {required} for glucose coloring")

    for i, line in enumerate(text.splitlines(), start=1):
        if HERO_ACCENT_PATTERN.search(line):
            errors.append(f"{rel}:{i}: hero glucose must not use accent color")

        if MINT_ON_VALUE_PATTERN.search(line):
            if not any(s in line or s in text.splitlines()[max(0, i - 3) : i + 2] for s in MINT_ALLOWLIST_SUBSTRINGS):
                errors.append(f"{rel}:{i}: possible mint/accent on glucose value")

        if "0xFF34D399" in line or "#34D399" in line.upper():
            context = "\n".join(text.splitlines()[max(0, i - 2) : i + 1])
            if not any(s in context for s in MINT_ALLOWLIST_SUBSTRINGS):
                if "Theme" in path.name or "TileTheme" in path.name:
                    if "valueColor" in line.lower() or "valueText" in line.lower():
                        errors.append(f"{rel}:{i}: mint must not color glucose value")

    return errors


def main() -> int:
    errors: list[str] = []
    for rel in GLUCOSE_KT_GLOBS:
        path = ROOT / rel
        if not path.is_file():
            errors.append(f"Missing glucose surface file: {rel}")
            continue
        errors.extend(lint_file(path))

    if errors:
        print("AGP color lint FAILED:\n", file=sys.stderr)
        for err in errors:
            print(f"  - {err}", file=sys.stderr)
        return 1

    print("AGP color lint OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
