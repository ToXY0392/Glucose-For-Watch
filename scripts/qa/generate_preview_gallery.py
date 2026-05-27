#!/usr/bin/env python3
"""
Build a browsable HTML gallery from AppPreviewExporterTest PNGs (AUTO-2).

Reads:  mobile/build/app-previews/*.png
Writes: mobile/build/preview-gallery/index.html (+ copies PNGs alongside)

Usage:
  py -3 scripts/qa/generate_preview_gallery.py
  bash scripts/qa/export_app_previews.sh
"""

from __future__ import annotations

import shutil
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PREVIEW_SRC = ROOT / "mobile" / "build" / "app-previews"
GALLERY_DIR = ROOT / "mobile" / "build" / "preview-gallery"
DESIGN_REF = ROOT / "toxy-ux-kit" / "design-reference" / "index.html"

TITLES: dict[str, str] = {
    "mobile-home-connected": "Connected · 120 mg/dL · watch ack OK",
    "mobile-home-dexcom-off": "Dexcom off · credentials missing",
    "mobile-home-waiting": "Waiting · no reading yet",
    "mobile-home-push-pending": "Push pending · unacked watch sync",
    "mobile-home-sync-error": "Sync error · Dexcom session",
    "mobile-home-watch-offline": "Watch offline · phone has reading",
}


def title_for(stem: str) -> str:
    return TITLES.get(stem, stem.replace("-", " ").title())


def card_html(filename: str, label: str) -> str:
    return f"""      <figure class="card">
        <img src="{filename}" alt="{label}" loading="lazy" />
        <figcaption><strong>{label}</strong><span>{filename}</span></figcaption>
      </figure>"""


def main() -> None:
    pngs = sorted(PREVIEW_SRC.glob("*.png"))
    if not pngs:
        raise SystemExit(f"No PNG files in {PREVIEW_SRC} — run AppPreviewExporterTest first.")

    GALLERY_DIR.mkdir(parents=True, exist_ok=True)
    cards: list[str] = []
    for src in pngs:
        dest = GALLERY_DIR / src.name
        shutil.copy2(src, dest)
        cards.append(card_html(src.name, title_for(src.stem)))

    generated = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    design_link = (
        '<p class="refs">ToXY token reference: '
        '<a href="../../../toxy-ux-kit/design-reference/index.html">design-reference/index.html</a> '
        "(run <code>py -3 toxy-ux-kit/tools/export-design-reference.py</code> locally)</p>"
        if DESIGN_REF.is_file()
        else ""
    )

    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Glucose For Watch — Mobile home preview gallery</title>
  <style>
    :root {{
      --bg: #0f172a;
      --surface: #1e293b;
      --text: #f1f5f9;
      --muted: #94a3b8;
      --accent: #34d399;
      --border: #334155;
    }}
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{
      font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
      background: var(--bg);
      color: var(--text);
      line-height: 1.5;
      min-height: 100vh;
    }}
    .wrap {{ max-width: 1200px; margin: 0 auto; padding: 2rem 1.25rem 3rem; }}
    header {{ margin-bottom: 1.5rem; }}
    header h1 {{ font-size: 1.5rem; font-weight: 700; }}
    header p {{ color: var(--muted); margin-top: 0.5rem; max-width: 48rem; }}
    .meta {{ font-size: 0.8rem; color: var(--muted); margin-top: 0.75rem; }}
    .grid {{
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 1.25rem;
      margin-top: 1.5rem;
    }}
    .card {{
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 12px;
      overflow: hidden;
    }}
    .card img {{
      display: block;
      width: 100%;
      height: auto;
      background: #000;
    }}
    figcaption {{
      padding: 0.75rem 1rem 1rem;
      font-size: 0.85rem;
    }}
    figcaption strong {{ display: block; color: var(--text); }}
    figcaption span {{ color: var(--muted); font-size: 0.75rem; }}
    .refs {{ margin-top: 2rem; font-size: 0.85rem; color: var(--muted); }}
    .refs a {{ color: var(--accent); }}
    footer {{ margin-top: 2rem; font-size: 0.75rem; color: var(--muted); }}
  </style>
</head>
<body>
  <div class="wrap">
    <header>
      <h1>Mobile home — Robolectric preview gallery</h1>
      <p>Generated from <code>AppPreviewExporterTest</code> (XML fixture bound via <code>HomeUiBinder</code>).
         Open offline after <code>export_app_previews.sh</code> or download the CI artifact.</p>
      <p class="meta">Generated {generated} · {len(pngs)} state(s)</p>
    </header>
    <div class="grid">
{chr(10).join(cards)}
    </div>
    {design_link}
    <footer>Auto-generated — do not edit. Source: scripts/qa/generate_preview_gallery.py</footer>
  </div>
</body>
</html>
"""
    out = GALLERY_DIR / "index.html"
    out.write_text(html, encoding="utf-8")
    print(f"Wrote {out.relative_to(ROOT)} ({len(pngs)} PNGs)")


if __name__ == "__main__":
    main()
