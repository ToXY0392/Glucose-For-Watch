#!/usr/bin/env bash
# Rebuild the docs-only branch from the current checkout (or SOURCE_SHA).
# Used locally and in .github/workflows/sync-docs-branch.yml
#
# Env:
#   DOCS_BRANCH     target branch name (default: docs)
#   SOURCE_REF      source branch label for README (default: current branch)
#   SOURCE_SHA      source commit (default: HEAD)
#   DRY_RUN         1 = no commit/push
#   SKIP_PUSH       1 = commit locally only

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

DOCS_BRANCH="${DOCS_BRANCH:-docs}"
SOURCE_SHA="${SOURCE_SHA:-$(git rev-parse HEAD)}"
SOURCE_REF="${SOURCE_REF:-$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo detached)}"
MANIFEST="${ROOT_DIR}/scripts/dev/docs_branch_manifest.txt"
DRY_RUN="${DRY_RUN:-0}"
SKIP_PUSH="${SKIP_PUSH:-0}"

if [[ ! -f "$MANIFEST" ]]; then
  echo "Missing manifest: $MANIFEST" >&2
  exit 1
fi

STAGING="$(mktemp -d)"
trap 'rm -rf "$STAGING"' EXIT

echo "[publish-docs] Exporting doc paths from ${SOURCE_REF}@${SOURCE_SHA:0:7}"

while IFS= read -r line || [[ -n "$line" ]]; do
  line="${line%%#*}"
  line="$(echo "$line" | xargs)"
  [[ -z "$line" ]] && continue

  src="${ROOT_DIR}/${line}"
  if [[ ! -e "$src" ]]; then
    echo "[publish-docs] WARN: missing path (skipped): $line" >&2
    continue
  fi

  dest="${STAGING}/${line}"
  mkdir -p "$(dirname "$dest")"
  if [[ -d "$src" ]]; then
    mkdir -p "$dest"
    cp -a "$src/." "$dest/"
  else
    cp -a "$src" "$dest"
  fi
done < "$MANIFEST"

FULL_REPO_URL="${FULL_REPO_URL:-https://github.com/ToXY0392/Glucose-For-Watch}"
SYNC_TIME="$(date -u +"%Y-%m-%d %H:%M UTC")"

cat > "${STAGING}/README.md" <<EOF
# Glucose For Watch — Documentation

> **Auto-published branch** — do not edit files here directly; changes are overwritten on the next sync.

| Field | Value |
|-------|--------|
| **Source branch** | \`${SOURCE_REF}\` |
| **Source commit** | [\`${SOURCE_SHA:0:7}\`](${FULL_REPO_URL}/commit/${SOURCE_SHA}) |
| **Last sync** | ${SYNC_TIME} |
| **Full repository** | [Glucose-For-Watch](${FULL_REPO_URL}) (app code on \`integrate\` / \`main\`) |

## Start here

- [Documentation hub](docs/index.md)
- [User guide](docs/guide/user.md)
- [Developer setup](docs/dev/setup.md)
- [Plan / progress](docs/plan/PROGRESS.md)

## Sync

Updated automatically by [\`sync-docs-branch\`](${FULL_REPO_URL}/blob/integrate/.github/workflows/sync-docs-branch.yml) when documentation paths change on \`integrate\`, \`main\`, or any branch (see workflow).

Manual publish from a checkout:

\`\`\`bash
bash scripts/dev/publish_docs_branch.sh
\`\`\`
EOF

# Preserve hub link at repo root for GitHub browsing
if [[ -f "${STAGING}/docs/index.md" ]]; then
  : # hub already under docs/
fi

WORKTREE="$(mktemp -d)"
trap 'rm -rf "$STAGING" "$WORKTREE"' EXIT

git fetch origin "${DOCS_BRANCH}" 2>/dev/null || true

if git show-ref --verify --quiet "refs/heads/${DOCS_BRANCH}"; then
  git worktree add -B "${DOCS_BRANCH}" "$WORKTREE" "${DOCS_BRANCH}"
elif git show-ref --verify --quiet "refs/remotes/origin/${DOCS_BRANCH}"; then
  git worktree add -B "${DOCS_BRANCH}" "$WORKTREE" "origin/${DOCS_BRANCH}"
else
  git worktree add -B "${DOCS_BRANCH}" "$WORKTREE"
fi

cd "$WORKTREE"

# Replace tree (keep .git)
find . -mindepth 1 -maxdepth 1 ! -name '.git' -exec rm -rf {} +
cp -a "${STAGING}/." .

git add -A
if git diff --staged --quiet; then
  echo "[publish-docs] No changes — docs branch already up to date"
  exit 0
fi

MSG="docs: sync from ${SOURCE_REF}@${SOURCE_SHA:0:7}"

if [[ "$DRY_RUN" == "1" ]]; then
  echo "[publish-docs] DRY_RUN: would commit: $MSG"
  git diff --staged --stat
  exit 0
fi

git commit -m "$MSG" -m "Automated docs-only branch publish." -m "Source: ${FULL_REPO_URL}/tree/${SOURCE_SHA}"

if [[ "$SKIP_PUSH" == "1" ]]; then
  echo "[publish-docs] Committed locally (SKIP_PUSH=1)"
  exit 0
fi

git push origin "${DOCS_BRANCH}"
echo "[publish-docs] Pushed ${DOCS_BRANCH}"
