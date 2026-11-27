#!/usr/bin/env python3
"""AGP medical color guard for Glucose For Watch.

Fails when glucose coloring bypasses the single source of truth,
`AgpGlucoseColors` / `GlucoseRangeResolver` in core/model/GlucoseRange.kt:

1. a glucose band name next to a hard-coded color literal in app Kotlin sources;
2. an `agp_*` color redefined in Android XML resources;
3. a known glucose surface that no longer goes through the resolver;
4. a chrome accent color used on a glucose value.

Usage: python3 scripts/dev/lint_agp_colors.py
Rules: .claude/skills/glucose-for-watch-agp-color-guard/SKILL.md
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SOURCE_OF_TRUTH = ROOT / "core/model/src/main/java/com/glucoseforwatch/core/model/GlucoseRange.kt"
APP_SOURCE_DIRS = ["mobile/src/main", "wear/src/main", "feature", "core"]

BAND = re.compile(r"\b(VeryLow|InRange|VeryHigh|Low|High|VERY_LOW|IN_RANGE|VERY_HIGH|LOW|HIGH|veryLow|inRange|veryHigh)\b")
COLOR_LITERAL = re.compile(r"0x[fF]{2}[0-9a-fA-F]{6}|Color\(0x|#[0-9a-fA-F]{6}\b")
ACCENT_ON_VALUE = re.compile(
    r"(glucose|valueColor|valueText|heroReading|displayValue).*(wg7_accent|toxy_accent|gfw_chrome_accent|colorScheme\.primary\b|GlassPalette\.Accent\b)",
    re.IGNORECASE,
)

GLUCOSE_SURFACES = [
    "mobile/src/main/java/com/glucoseforwatch/mobile/ui/HomeStateMapper.kt",
    "mobile/src/main/java/com/glucoseforwatch/mobile/ui/compose/HomeGlucoseColor.kt",
    "wear/src/main/java/com/glucoseforwatch/wear/tile/ToxyTileTheme.kt",
    "wear/src/main/java/com/glucoseforwatch/wear/tile/GlucoseTileChrome.kt",
    "wear/src/main/java/com/glucoseforwatch/wear/data/GlucoseCache.kt",
    "wear/src/main/java/com/glucoseforwatch/wear/complication/AgpComplicationColorRamp.kt",
]


def kotlin_files() -> list[Path]:
    files: list[Path] = []
    for rel in APP_SOURCE_DIRS:
        files.extend(p for p in (ROOT / rel).rglob("*.kt") if "/src/main/" in p.as_posix())
    return [p for p in files if p != SOURCE_OF_TRUTH]


def main() -> int:
    errors: list[str] = []

    for path in kotlin_files():
        rel = path.relative_to(ROOT).as_posix()
        for i, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
            code = line.split("//", 1)[0]
            if BAND.search(code) and COLOR_LITERAL.search(code):
                errors.append(f"{rel}:{i}: hard-coded glucose band color, use GlucoseRangeResolver")
            if ACCENT_ON_VALUE.search(code):
                errors.append(f"{rel}:{i}: chrome accent on a glucose value")

    for path in ROOT.glob("*/src/*/res/values*/*.xml"):
        text = path.read_text(encoding="utf-8")
        if re.search(r'<color\s+name="agp_', text):
            errors.append(f"{path.relative_to(ROOT).as_posix()}: agp_* color redefined in XML, keep AGP in AgpGlucoseColors")

    for rel in GLUCOSE_SURFACES:
        path = ROOT / rel
        if not path.is_file():
            errors.append(f"{rel}: glucose surface missing, update GLUCOSE_SURFACES")
        elif not re.search(r"GlucoseRangeResolver|AgpGlucoseColors", path.read_text(encoding="utf-8")):
            errors.append(f"{rel}: glucose surface does not use GlucoseRangeResolver / AgpGlucoseColors")

    if errors:
        print("AGP color lint FAILED:", file=sys.stderr)
        for err in errors:
            print(f"  - {err}", file=sys.stderr)
        return 1
    print("AGP color lint OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
