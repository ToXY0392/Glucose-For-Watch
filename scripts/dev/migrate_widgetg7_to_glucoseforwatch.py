#!/usr/bin/env python3
"""Remove Glucose For Watch identifiers: package com.glucoseforwatch, Gradle GlucoseForWatch, gfw.* props."""
from __future__ import annotations

import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

SKIP_DIR_NAMES = {
    ".git",
    ".gradle",
    "build",
    ".idea",
    "node_modules",
}

TEXT_SUFFIXES = {
    ".kt",
    ".kts",
    ".java",
    ".xml",
    ".gradle",
    ".properties",
    ".md",
    ".mdc",
    ".sh",
    ".ps1",
    ".py",
    ".json",
    ".txt",
    ".pro",
    ".yml",
    ".yaml",
    ".html",
}

FILE_RENAMES = {
    "GlucoseForWatchWearApplication.kt": "GlucoseForWatchWearApplication.kt",
    "GlucoseForWatchTheme.kt": "GlucoseForWatchTheme.kt",
    "GlucoseForWatchWearTheme.kt": "GlucoseForWatchWearTheme.kt",
    "GlucoseForWatchShowkaseRoot.kt": "GlucoseForWatchShowkaseRoot.kt",
    "GlucoseForWatchThemePreview.kt": "GlucoseForWatchThemePreview.kt",
}

# Longest / most specific first
REPLACEMENTS = [
    ("com.glucoseforwatch", "com.glucoseforwatch"),
    ("com/glucoseforwatch", "com/glucoseforwatch"),
    ("GlucoseForWatchWearApplication", "GlucoseForWatchWearApplication"),
    ("GlucoseForWatchShowkaseRoot", "GlucoseForWatchShowkaseRoot"),
    ("GlucoseForWatchThemePreview", "GlucoseForWatchThemePreview"),
    ("GlucoseForWatchWearTheme", "GlucoseForWatchWearTheme"),
    ("GlucoseForWatchTheme", "GlucoseForWatchTheme"),
    ("installGlucoseForWatchDebug", "installGlucoseForWatchDebug"),
    ("AnimationGlucoseForWatchNoAnimation", "AnimationGlucoseForWatchNoAnimation"),
    ("Theme.GlucoseForWatch", "Theme.GlucoseForWatch"),
    ("ShapeAppearance.GlucoseForWatch", "ShapeAppearance.GlucoseForWatch"),
    ("Widget.GlucoseForWatch", "Widget.GlucoseForWatch"),
    ("gfw_phone_sync_state", "gfw_phone_sync_state"),
    ("glucose-for-watch-wear-share.apk", "glucose-for-watch-wear-share.apk"),
    ("glucose-for-watch-wear.apk", "glucose-for-watch-wear.apk"),
    ("gfw.adb", "gfw.adb"),
    ("GFW_", "GFW_"),
    ("GlucoseForWatch-import-script", "GlucoseForWatch-import-script"),
    ('rootProject.name = "GlucoseForWatch"', 'rootProject.name = "GlucoseForWatch"'),
    ("Glucose For Watch", "Glucose For Watch"),
    ("Glucose-For-Watch", "Glucose-For-Watch"),
    ("glucose-for-watch", "glucose-for-watch"),
    ("glucose for watch", "glucose for watch"),
    ("GlucoseForWatch", "GlucoseForWatch"),
    ("gfw", "gfw"),
]


def should_skip_dir(path: Path) -> bool:
    return any(part in SKIP_DIR_NAMES for part in path.parts)


def move_package_dirs() -> None:
    for widget_dir in ROOT.rglob("com/glucoseforwatch"):
        if should_skip_dir(widget_dir):
            continue
        target = widget_dir.parent / "glucoseforwatch"
        if target.exists():
            raise SystemExit(f"Target already exists: {target}")
        print(f"move {widget_dir.relative_to(ROOT)} -> {target.relative_to(ROOT)}")
        widget_dir.rename(target)


def rename_symbol_files() -> None:
    for old, new in FILE_RENAMES.items():
        for path in ROOT.rglob(old):
            if should_skip_dir(path):
                continue
            dest = path.with_name(new)
            print(f"rename {path.relative_to(ROOT)} -> {dest.name}")
            path.rename(dest)


def iter_text_files() -> list[Path]:
    files: list[Path] = []
    for path in ROOT.rglob("*"):
        if not path.is_file():
            continue
        if should_skip_dir(path):
            continue
        if path.suffix.lower() in TEXT_SUFFIXES:
            files.append(path)
    return files


def apply_replacements() -> int:
    changed = 0
    for path in iter_text_files():
        text = path.read_text(encoding="utf-8")
        original = text
        for old, new in REPLACEMENTS:
            text = text.replace(old, new)
        if text != original:
            path.write_text(text, encoding="utf-8", newline="\n")
            changed += 1
            print(f"updated {path.relative_to(ROOT)}")
    return changed


def main() -> None:
    move_package_dirs()
    rename_symbol_files()
    n = apply_replacements()
    print(f"done: {n} text files updated")


if __name__ == "__main__":
    main()
