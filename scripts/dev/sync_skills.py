#!/usr/bin/env python3
"""Keep agent skills in one place and mirror them for Cursor.

Canonical source: .claude/skills/   (read by Claude Code)
Generated mirror: .cursor/skills/   (read by Cursor) - never edit by hand

Usage:
  python3 scripts/dev/sync_skills.py           # mirror .claude/skills -> .cursor/skills
  python3 scripts/dev/sync_skills.py --check   # exit 1 if the mirror is stale (CI)
  python3 scripts/dev/sync_skills.py --lint    # validate skill frontmatter and links (CI)
"""

from __future__ import annotations

import argparse
import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / ".claude" / "skills"
DST = ROOT / ".cursor" / "skills"
PROJECT_PREFIX = "glucose-for-watch-"
MAX_DESCRIPTION = 1024
LINK = re.compile(r"\[[^\]]*\]\(([^)\s]+)\)")
NAME_RE = re.compile(r"^[a-z0-9]+(-[a-z0-9]+)*$")


def files_under(base: Path) -> dict[str, bytes]:
    out: dict[str, bytes] = {}
    if not base.exists():
        return out
    for path in sorted(base.rglob("*")):
        if path.is_file():
            # Normalise line endings so a Windows (CRLF) checkout and WSL agree.
            out[path.relative_to(base).as_posix()] = path.read_bytes().replace(b"\r\n", b"\n")
    return out


def sync() -> int:
    if DST.exists():
        shutil.rmtree(DST)
    shutil.copytree(SRC, DST)
    print(f"Mirrored {len(list(SRC.iterdir()))} skills: {SRC.relative_to(ROOT)} -> {DST.relative_to(ROOT)}")
    return 0


def check() -> int:
    src, dst = files_under(SRC), files_under(DST)
    stale = sorted(
        [f"missing in mirror: {p}" for p in src.keys() - dst.keys()]
        + [f"extra in mirror: {p}" for p in dst.keys() - src.keys()]
        + [f"differs: {p}" for p in src.keys() & dst.keys() if src[p] != dst[p]]
    )
    if stale:
        print(".cursor/skills is out of sync with .claude/skills:")
        print("\n".join(f"  {line}" for line in stale))
        print("Fix: python3 scripts/dev/sync_skills.py")
        return 1
    print(f"Skill mirror in sync ({len(src)} files).")
    return 0


def parse_frontmatter(text: str) -> dict[str, str] | None:
    lines = text.replace("\r\n", "\n").split("\n")
    if not lines or lines[0].strip() != "---":
        return None
    try:
        end = lines.index("---", 1)
    except ValueError:
        return None
    meta: dict[str, str] = {}
    key = None
    for line in lines[1:end]:
        match = re.match(r"^([A-Za-z0-9_-]+):\s*(.*)$", line)
        if match:
            key, value = match.group(1), match.group(2).strip()
            meta[key] = "" if value in (">-", ">", "|", "|-") else value.strip("'\"")
        elif key and line.startswith((" ", "\t")):
            meta[key] = (meta[key] + " " + line.strip()).strip()
    return meta


def lint() -> int:
    errors: list[str] = []
    skills = sorted(p for p in SRC.iterdir() if p.is_dir())
    for skill in skills:
        rel = skill.relative_to(ROOT).as_posix()
        skill_md = skill / "SKILL.md"
        if not skill_md.exists():
            errors.append(f"{rel}: missing SKILL.md")
            continue
        if b"\r\n" in skill_md.read_bytes():
            errors.append(
                f"{rel}/SKILL.md: CRLF line endings — Claude Code cannot parse the frontmatter "
                "(see .gitattributes; re-checkout the file)"
            )
        text = skill_md.read_text(encoding="utf-8")
        meta = parse_frontmatter(text)
        if meta is None:
            errors.append(f"{rel}/SKILL.md: missing or unterminated frontmatter")
            continue
        name, desc = meta.get("name", ""), meta.get("description", "")
        if name != skill.name:
            errors.append(f"{rel}: frontmatter name '{name}' != folder '{skill.name}'")
        if not NAME_RE.match(skill.name):
            errors.append(f"{rel}: folder name must be lowercase kebab-case")
        if not desc:
            errors.append(f"{rel}: empty description")
        elif len(desc) > MAX_DESCRIPTION:
            errors.append(f"{rel}: description is {len(desc)} chars (max {MAX_DESCRIPTION})")
        if skill.name.startswith(PROJECT_PREFIX):
            if "use when" not in desc.lower():
                errors.append(f"{rel}: project skill description must say 'Use when ...'")
        else:
            # Third-party skill: provenance and licence must travel with it.
            for required in ("SOURCE.md",):
                if not (skill / required).exists():
                    errors.append(f"{rel}: third-party skill missing {required}")
            if not any(skill.glob("LICENSE*")):
                errors.append(f"{rel}: third-party skill missing LICENSE file")
        if skill.name.startswith(PROJECT_PREFIX):
            for md in sorted(skill.rglob("*.md")):
                for match in LINK.finditer(md.read_text(encoding="utf-8")):
                    target = match.group(1)
                    if target.startswith(("http://", "https://", "mailto:", "#")):
                        continue
                    path_part = target.split("#", 1)[0]
                    if path_part and not (md.parent / path_part).resolve().exists():
                        errors.append(f"{md.relative_to(ROOT).as_posix()}: broken link ({target})")
    if errors:
        print("Skill lint failed:")
        print("\n".join(f"  {e}" for e in errors))
        return 1
    print(f"Skill lint OK ({len(skills)} skills).")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    group = parser.add_mutually_exclusive_group()
    group.add_argument("--check", action="store_true", help="fail if .cursor/skills is stale")
    group.add_argument("--lint", action="store_true", help="validate skills")
    args = parser.parse_args()
    if not SRC.is_dir():
        print(f"Missing {SRC.relative_to(ROOT)}")
        return 1
    if args.check:
        return check()
    if args.lint:
        return lint()
    return sync()


if __name__ == "__main__":
    sys.exit(main())
