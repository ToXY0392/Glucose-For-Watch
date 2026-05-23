#!/usr/bin/env python3
"""
Export ToXY UX Kit color tokens to Android colors.xml fragments.

Source of truth: toxy-ux-kit/tokens/*.json
Output: toxy-ux-kit/tools/export/output/

Usage:
  python toxy-ux-kit/tools/export-android-colors.py

Copy generated files into mobile/src/main/res/values/ when approved.
Do NOT run from CI without review — generated output is staged for human merge.
"""

from __future__ import annotations

import json
import re
from pathlib import Path

KIT_ROOT = Path(__file__).resolve().parents[1]
TOKENS_DIR = KIT_ROOT / "tokens"
OUTPUT_DIR = Path(__file__).resolve().parent / "export" / "output"


def load_json(name: str) -> dict:
    with open(TOKENS_DIR / name, encoding="utf-8") as f:
        return json.load(f)


def flatten_colors(prefix: str, node: dict, path: list[str]) -> list[tuple[str, str, str]]:
    """Return (xml_name, hex, description) tuples."""
    results: list[tuple[str, str, str]] = []
    for key, value in node.items():
        current = path + [key]
        if isinstance(value, dict):
            if "$value" in value and value.get("$type") == "color":
                hex_val = value["$value"].upper()
                if not hex_val.startswith("#"):
                    hex_val = f"#{hex_val}"
                xml_name = prefix + "_" + "_".join(current)
                desc = value.get("$description", "")
                results.append((xml_name, hex_val, desc))
            else:
                results.extend(flatten_colors(prefix, value, current))
    return results


def to_xml_resource(name: str, hex_color: str, comment: str = "") -> str:
    line = f'    <color name="{name}">{hex_color}</color>'
    if comment:
        return f"    <!-- {comment} -->\n{line}"
    return line


def write_colors_xml(filename: str, header_comment: str, entries: list[tuple[str, str, str]]) -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        '<?xml version="1.0" encoding="utf-8"?>',
        "<resources>",
        f"    <!-- Generated from toxy-ux-kit/tokens/ — {header_comment} -->",
        f"    <!-- Run: python toxy-ux-kit/tools/export-android-colors.py -->",
        "",
    ]
    for name, hex_color, desc in entries:
        lines.append(to_xml_resource(name, hex_color, desc))
    lines.append("</resources>")
    lines.append("")
    out = OUTPUT_DIR / filename
    out.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {out.relative_to(KIT_ROOT.parent)}")


def main() -> None:
    toxy = load_json("toxy.color.json")
    agp = load_json("agp.glucose.json")

    toxy_entries = flatten_colors("toxy", toxy.get("toxy", toxy), ["color"] if "toxy" not in toxy else [])
    # flatten starts from color subtree
    toxy_entries = flatten_colors("toxy", toxy["toxy"]["color"], [])

    agp_entries = flatten_colors("agp", agp["agp"]["glucose"], [])

    write_colors_xml(
        "toxy_colors.xml",
        "ToXY chrome layer — NOT for glucose values",
        toxy_entries,
    )
    write_colors_xml(
        "agp_glucose_colors.xml",
        "AGP medical layer — glucose range colors only",
        agp_entries,
    )

    print("\nReview output in toxy-ux-kit/tools/export/output/")
    print("Integrate into mobile/src/main/res/values/ when ready.")


if __name__ == "__main__":
    main()
