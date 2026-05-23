#!/usr/bin/env python3
"""
Export ToXY UX Kit tokens to Tokens Studio for Figma (two separate sets).

Source: toxy-ux-kit/tokens/*.json
Output: toxy-ux-kit/figma/tokens-studio/

Usage:
  python toxy-ux-kit/tools/export-figma-tokens.py

Import in Figma via Tokens Studio plugin → sync folder or paste JSON.
Keep ToXY Chrome and AGP Medical as separate token sets.
"""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any

KIT_ROOT = Path(__file__).resolve().parents[1]
TOKENS_DIR = KIT_ROOT / "tokens"
OUTPUT_DIR = KIT_ROOT / "figma" / "tokens-studio"


def load_json(name: str) -> dict:
    with open(TOKENS_DIR / name, encoding="utf-8") as f:
        return json.load(f)


def dtcg_to_tokens_studio(node: dict) -> dict[str, Any]:
    """Convert DTCG-style token tree to Tokens Studio shape (type + value)."""
    result: dict[str, Any] = {}
    for key, value in node.items():
        if not isinstance(value, dict):
            continue
        if "$value" in value:
            token_type = value.get("$type", "other")
            entry: dict[str, Any] = {"value": value["$value"], "type": token_type}
            if "$description" in value:
                entry["description"] = value["$description"]
            result[key] = entry
        else:
            nested = dtcg_to_tokens_studio(value)
            if nested:
                result[key] = nested
    return result


def write_set(filename: str, token_set: dict[str, Any]) -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    out = OUTPUT_DIR / filename
    out.write_text(json.dumps(token_set, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Wrote {out.relative_to(KIT_ROOT.parent)}")


def main() -> None:
    toxy = load_json("toxy.color.json")
    agp = load_json("agp.glucose.json")
    spacing = load_json("toxy.spacing.json")
    typography = load_json("toxy.typography.json")
    shape = load_json("toxy.shape.json")
    motion = load_json("toxy.motion.json")

    chrome_colors = dtcg_to_tokens_studio(toxy["toxy"]["color"])
    medical_colors = dtcg_to_tokens_studio(agp["agp"]["glucose"])
    medical_thresholds = dtcg_to_tokens_studio(agp["agp"]["thresholds"])

    write_set(
        "toxy-chrome.tokens.json",
        {
            "color": chrome_colors,
            "spacing": dtcg_to_tokens_studio(spacing["toxy"]["spacing"]),
            "typography": dtcg_to_tokens_studio(typography["toxy"]["typography"]),
            "shape": dtcg_to_tokens_studio(shape["toxy"]["shape"]),
            "motion": dtcg_to_tokens_studio(motion["toxy"]["motion"]),
        },
    )
    write_set(
        "agp-medical.tokens.json",
        {
            "glucose": medical_colors,
            "thresholds": medical_thresholds,
        },
    )
    write_set(
        "$metadata.json",
        {
            "tokenSetOrder": ["toxy-chrome", "agp-medical"],
            "description": "ToXY UX Kit v0.1 — import both sets; never merge glucose into chrome.",
        },
    )

    print("\nImport in Figma: Tokens Studio -> Settings -> Sync -> folder:", OUTPUT_DIR)
    print("See toxy-ux-kit/figma/FIGMA-HANDOFF.md for frame checklist.")


if __name__ == "__main__":
    main()
