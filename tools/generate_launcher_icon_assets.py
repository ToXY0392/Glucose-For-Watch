#!/usr/bin/env python3
"""Regénère ic_widget_g7_drop_logo.png et ic_launcher_monochrome.png (432×432).

MASK_RADIUS_FRAC : rayon max des pixels opaques = (CANVAS/2) * frac.
  défaut 0.56 = équilibre capture de référence (petite goutte, marge blanche). L’APK ne règle pas la grille du tiroir système.
"""
from __future__ import annotations

import argparse
import math
from pathlib import Path

from PIL import Image

REPO = Path(__file__).resolve().parents[1]
NODPI = REPO / "mobile" / "src" / "main" / "res" / "drawable-nodpi"
OFF = NODPI / "logo_widget_g7_official.png"
OUT_FG = NODPI / "ic_widget_g7_drop_logo.png"
OUT_MO = NODPI / "ic_launcher_monochrome.png"

CANVAS = 432
CX = CY = CANVAS // 2
ALPHA_TH = 28


def extract_drop(im: Image.Image) -> Image.Image:
    im = im.convert("RGBA")
    w, h = im.size
    px = im.load()
    ylim = min(h, int(h * 0.72))
    xs: list[int] = []
    ys: list[int] = []
    for y in range(ylim):
        for x in range(w):
            if px[x, y][3] >= 30:
                xs.append(x)
                ys.append(y)
    x0, x1, y0, y1 = min(xs), max(xs), min(ys), max(ys)
    return im.crop((x0, y0, x1 + 1, y1 + 1))


def max_radius_rgba(patch: Image.Image, ox: int, oy: int) -> float:
    w, h = patch.size
    px = patch.load()
    m = 0.0
    for y in range(h):
        for x in range(w):
            if px[x, y][3] < ALPHA_TH:
                continue
            dx = ox + x - CX + 0.5
            dy = oy + y - CY + 0.5
            m = max(m, math.hypot(dx, dy))
    return m


def main() -> None:
    p = argparse.ArgumentParser()
    p.add_argument(
        "--radius-frac",
        type=float,
        default=0.56,
        help="fraction du demi-côté (216) pour le rayon max des pixels opaques",
    )
    args = p.parse_args()
    r_mask = CX * args.radius_frac

    drop = extract_drop(Image.open(OFF))
    dw, dh = drop.size
    s_max = min(CANVAS / dw, CANVAS / dh)
    low, high = 0.2, s_max
    best = low
    for _ in range(32):
        mid = (low + high) * 0.5
        nw = max(1, int(round(dw * mid)))
        nh = max(1, int(round(dh * mid)))
        ds = drop.resize((nw, nh), Image.Resampling.LANCZOS)
        ox = (CANVAS - nw) // 2
        oy = (CANVAS - nh) // 2
        rad = max_radius_rgba(ds, ox, oy)
        if rad <= r_mask:
            best = mid
            low = mid
        else:
            high = mid

    nw = max(1, int(round(dw * best)))
    nh = max(1, int(round(dh * best)))
    drop_s = drop.resize((nw, nh), Image.Resampling.LANCZOS)
    ox = (CANVAS - nw) // 2
    oy = (CANVAS - nh) // 2

    fg = Image.new("RGBA", (CANVAS, CANVAS), (0, 0, 0, 0))
    fg.paste(drop_s, (ox, oy), drop_s)
    fg.save(OUT_FG, optimize=True)

    a = drop_s.split()[3]
    rw = Image.new("RGBA", drop_s.size, (255, 255, 255, 0))
    rp = rw.load()
    ap = a.load()
    for yy in range(nh):
        for xx in range(nw):
            rp[xx, yy] = (255, 255, 255, ap[xx, yy])
    mono = Image.new("RGBA", (CANVAS, CANVAS), (0, 0, 0, 0))
    mono.paste(rw, (ox, oy), rw)
    mono.save(OUT_MO, optimize=True)

    rmax = max_radius_rgba(drop_s, ox, oy)
    print(
        f"radius_frac={args.radius_frac} R_MASK={r_mask:.2f} r_max={rmax:.2f} "
        f"patch={nw}x{nh}"
    )


if __name__ == "__main__":
    main()
