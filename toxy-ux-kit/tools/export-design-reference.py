#!/usr/bin/env python3
"""
Generate standalone HTML design reference from toxy-ux-kit tokens.

Replaces optional Figma file for dev handoff — open in any browser.

Usage:
  py -3 toxy-ux-kit/tools/export-design-reference.py
"""

from __future__ import annotations

import json
from pathlib import Path

KIT = Path(__file__).resolve().parents[1]
TOKENS = KIT / "tokens"
OUT = KIT / "design-reference" / "index.html"


def load(name: str) -> dict:
    return json.loads((TOKENS / name).read_text(encoding="utf-8"))


def c(node: dict) -> str:
    return node["$value"]


def main() -> None:
    toxy = load("toxy.color.json")["toxy"]["color"]
    agp = load("agp.glucose.json")["agp"]["glucose"]
    meta = load("meta.json")

    t = lambda *keys: c(toxy if len(keys) == 1 else _dig(toxy, keys))
    # helper for nested toxy paths
    def _dig(d: dict, keys: tuple[str, ...]) -> dict:
        for k in keys:
            d = d[k]
        return d

    bg_top = c(toxy["background"]["top"])
    bg_bottom = c(toxy["background"]["bottom"])
    surface = c(toxy["surface"]["default"])
    surface_alt = c(toxy["surface"]["alt"])
    text_pri = c(toxy["text"]["primary"])
    text_sec = c(toxy["text"]["secondary"])
    accent = c(toxy["accent"]["default"])
    accent_on = c(toxy["accent"]["on"])
    accent_soft = c(toxy["accent"]["soft"])
    sync_ok = c(toxy["sync"]["ok"])
    sync_warn = c(toxy["sync"]["warn"])
    sync_err = c(toxy["sync"]["error"])
    sync_ok_soft = c(toxy["sync"]["okSoft"])
    sync_warn_soft = c(toxy["sync"]["warnSoft"])
    sync_err_soft = c(toxy["sync"]["errorSoft"])

    agp_rows = [
        ("veryLow", agp["veryLow"], "< 54 mg/dL"),
        ("low", agp["low"], "54–69"),
        ("inRange", agp["inRange"], "70–180"),
        ("high", agp["high"], "181–250"),
        ("veryHigh", agp["veryHigh"], "> 250"),
        ("unknown", agp["unknown"], "stale / no data"),
    ]

    agp_swatches = "\n".join(
        f"""        <div class="swatch">
          <div class="swatch-color" style="background:{c(info)}"></div>
          <div class="swatch-meta"><strong>agp.glucose.{name}</strong><span>{c(info)} · {label}</span></div>
        </div>"""
        for name, info, label in agp_rows
    )

    chrome_swatches = [
        ("background.top", bg_top, "Canvas"),
        ("surface.default", surface, "Cards"),
        ("text.primary", text_pri, "Primary text"),
        ("text.secondary", text_sec, "Secondary text"),
        ("accent.default", accent, "Brand / sync chrome"),
        ("sync.ok", sync_ok, "Sync OK"),
        ("sync.warn", sync_warn, "Sync warn"),
        ("sync.error", sync_err, "Sync error"),
    ]
    chrome_html = "\n".join(
        f"""        <div class="swatch">
          <div class="swatch-color" style="background:{hexv}"></div>
          <div class="swatch-meta"><strong>toxy.color.{tok}</strong><span>{hexv} · {desc}</span></div>
        </div>"""
        for tok, hexv, desc in chrome_swatches
    )

    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>ToXY UX Kit — Design Reference v{meta["version"]}</title>
  <style>
    :root {{
      --bg-top: {bg_top};
      --bg-bottom: {bg_bottom};
      --surface: {surface};
      --surface-alt: {surface_alt};
      --text: {text_pri};
      --text-sec: {text_sec};
      --accent: {accent};
      --accent-on: {accent_on};
      --accent-soft: {accent_soft};
      --agp-in: {c(agp["inRange"])};
      --agp-high: {c(agp["high"])};
      --agp-low: {c(agp["low"])};
      --sync-ok-soft: {sync_ok_soft};
      --sync-warn-soft: {sync_warn_soft};
      --sync-err-soft: {sync_err_soft};
    }}
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{
      font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
      background: linear-gradient(180deg, var(--bg-top), var(--bg-bottom));
      color: var(--text);
      min-height: 100vh;
      line-height: 1.5;
    }}
    .wrap {{ max-width: 1100px; margin: 0 auto; padding: 2rem 1.25rem 4rem; }}
    header {{ margin-bottom: 2rem; }}
    header h1 {{ font-size: 1.75rem; font-weight: 700; }}
    header p {{ color: var(--text-sec); margin-top: 0.5rem; max-width: 52rem; }}
    .rule {{
      background: {sync_err_soft};
      border: 1px solid {sync_err};
      border-radius: 12px;
      padding: 0.75rem 1rem;
      margin: 1rem 0 2rem;
      font-size: 0.9rem;
    }}
    .rule strong {{ color: {sync_err}; }}
    h2 {{ font-size: 1.15rem; margin: 2rem 0 1rem; color: var(--text-sec); text-transform: uppercase; letter-spacing: 0.06em; }}
    .grid-2 {{ display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }}
    @media (max-width: 800px) {{ .grid-2 {{ grid-template-columns: 1fr; }} }}
    .panel {{
      background: var(--surface);
      border: 1px solid #334155;
      border-radius: 16px;
      padding: 1.25rem;
    }}
    .swatch {{ display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.65rem; }}
    .swatch-color {{ width: 40px; height: 40px; border-radius: 8px; border: 1px solid #334155; flex-shrink: 0; }}
    .swatch-meta {{ font-size: 0.8rem; }}
    .swatch-meta strong {{ display: block; color: var(--text); }}
    .swatch-meta span {{ color: var(--text-sec); }}
    .previews {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1.5rem; margin-top: 1rem; }}
    .watch {{
      width: 220px; height: 220px; border-radius: 50%;
      background: var(--bg-top);
      border: 3px solid #334155;
      margin: 0 auto;
      display: flex; flex-direction: column;
      align-items: center; justify-content: center;
      padding: 1rem;
    }}
    .watch .value {{ font-size: 2.5rem; font-weight: 800; color: var(--agp-in); line-height: 1; }}
    .watch .meta {{ color: var(--text-sec); font-size: 0.85rem; margin-top: 0.25rem; }}
    .watch .sync {{
      margin-top: 0.75rem;
      background: var(--accent-soft);
      color: var(--accent);
      border: none;
      border-radius: 999px;
      padding: 0.5rem 1.25rem;
      font-weight: 600;
      font-size: 0.85rem;
      min-height: 48px;
      min-width: 48px;
    }}
    .phone {{
      background: var(--surface);
      border-radius: 20px;
      padding: 1.25rem;
      border: 1px solid #334155;
    }}
    .hero-value {{ font-size: 3rem; font-weight: 800; color: var(--agp-in); }}
    .hero-sub {{ color: var(--text-sec); font-size: 0.9rem; margin-top: 0.25rem; }}
    .pill {{
      display: inline-block;
      padding: 0.35rem 0.75rem;
      border-radius: 999px;
      font-size: 0.75rem;
      margin: 0.25rem 0.25rem 0 0;
      border: 1px solid transparent;
    }}
    .pill-ok {{ background: var(--sync-ok-soft); color: {sync_ok}; border-color: {c(toxy["sync"]["okStroke"])}; }}
    .pill-warn {{ background: var(--sync-warn-soft); color: {sync_warn}; }}
    .pill-err {{ background: var(--sync-err-soft); color: {sync_err}; }}
    .refs {{ margin-top: 2rem; font-size: 0.85rem; color: var(--text-sec); }}
    .refs a {{ color: var(--accent); }}
    footer {{ margin-top: 3rem; font-size: 0.75rem; color: var(--text-sec); }}
  </style>
</head>
<body>
  <div class="wrap">
    <header>
      <h1>ToXY UX Kit — Design Reference</h1>
      <p>Generated from <code>toxy-ux-kit/tokens/</code> v{meta["version"]}. Standalone handoff — Figma optional.
         Chrome (ToXY) and medical (AGP) layers are separate by design.</p>
    </header>

    <div class="rule">
      <strong>Medical rule:</strong> Never use mint <code>{accent}</code> (toxy.accent) on glucose numeric values.
      Use <code>agp.glucose.*</code> only.
    </div>

    <div class="grid-2">
      <section class="panel">
        <h2>ToXY Chrome</h2>
{chrome_html}
      </section>
      <section class="panel">
        <h2>AGP Medical</h2>
{agp_swatches}
      </section>
    </div>

    <h2>Component previews</h2>
    <div class="previews">
      <div class="panel">
        <p style="color:var(--text-sec);font-size:0.8rem;margin-bottom:0.75rem">Wear tile · 450×450 ref</p>
        <div class="watch">
          <div class="value">120</div>
          <div class="meta">mg/dL ↗</div>
          <button type="button" class="sync">↻ Sync</button>
        </div>
      </div>
      <div class="panel">
        <p style="color:var(--text-sec);font-size:0.8rem;margin-bottom:0.75rem">Wear status · AGP hero</p>
        <div class="watch">
          <div class="value" style="color:var(--agp-high)">200</div>
          <div class="meta">mg/dL → · Up to date</div>
          <button type="button" class="sync">↻ Sync</button>
        </div>
      </div>
      <div class="panel phone">
        <p style="color:var(--text-sec);font-size:0.8rem;margin-bottom:0.75rem">Mobile home hero</p>
        <div class="hero-value">120</div>
        <div class="hero-sub">mg/dL ↗ · synced 2 min ago</div>
        <div style="margin-top:1rem">
          <span class="pill pill-ok">Watch confirmed</span>
          <span class="pill pill-warn">Sync pending</span>
          <span class="pill pill-err">Dexcom error</span>
        </div>
      </div>
    </div>

    <div class="refs">
      <p>PNG references: <a href="../assets/references/tile_dial_reference.png">tile</a> ·
         <a href="../assets/references/tile_reading_preview.png">reading</a> ·
         <a href="../assets/references/ic_tile_refresh_button_reference.png">sync button</a></p>
      <p>Specs: <code>spec/components/</code> · Regenerate: <code>py -3 tools/export-design-reference.py</code></p>
    </div>

    <footer>Auto-generated — do not edit by hand. Source: toxy-ux-kit/tools/export-design-reference.py</footer>
  </div>
</body>
</html>
"""
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(html, encoding="utf-8")
    print(f"Wrote {OUT.relative_to(KIT.parent)}")
    print("Open in browser: file:///" + str(OUT.resolve()).replace(chr(92), "/"))


if __name__ == "__main__":
    main()
