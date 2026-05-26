#!/usr/bin/env bash
# Move linked issue cards on Project #1 to "In Review" when a PR opens (AUTO-7).
set -eu

PROJECT_ID="${GFW_PROJECT_ID:-PVT_kwHODxpB9c4BYyWH}"
REPO="${GITHUB_REPOSITORY:-ToXY0392/Glucose-For-Watch}"

if [[ -z "${PR_BODY:-}" ]]; then
  echo "[gfw] PR_BODY empty — skip"
  exit 0
fi

mapfile -t ISSUE_NUMS < <(
  printf '%s' "$PR_BODY" | grep -ioE '(close[sd]?|fix(e[sd])?|resolve[sd]?|ref[s]?)\s+#([0-9]+)' \
    | grep -ioE '#[0-9]+' | tr -d '#' | sort -u
)

if [[ ${#ISSUE_NUMS[@]} -eq 0 ]]; then
  echo "[gfw] No linked issues in PR body"
  exit 0
fi

echo "[gfw] Linked issues: ${ISSUE_NUMS[*]}"

STATUS_FIELD_ID="$(gh api graphql -f query='
  query($id: ID!) {
    node(id: $id) {
      ... on ProjectV2 {
        field(name: "Status") {
          ... on ProjectV2SingleSelectField { id options { id name } }
        }
      }
    }
  }' -f id="$PROJECT_ID" --jq '.data.node.field.id')"

IN_REVIEW_OPTION_ID="$(gh api graphql -f query='
  query($id: ID!) {
    node(id: $id) {
      ... on ProjectV2 {
        field(name: "Status") {
          ... on ProjectV2SingleSelectField { id options { id name } }
        }
      }
    }
  }' -f id="$PROJECT_ID" --jq '.data.node.field.options[] | select(.name=="In Review") | .id')"

for num in "${ISSUE_NUMS[@]}"; do
  ISSUE_NODE="$(gh api "repos/$REPO/issues/$num" --jq .node_id)"
  ITEM_ID="$(gh api graphql -f query='
    mutation($project: ID!, $content: ID!) {
      addProjectV2ItemById(input: {projectId: $project, contentId: $content}) {
        item { id }
      }
    }' -f project="$PROJECT_ID" -f content="$ISSUE_NODE" --jq '.data.addProjectV2ItemById.item.id' 2>/dev/null || true)"

  if [[ -z "$ITEM_ID" || "$ITEM_ID" == "null" ]]; then
    ITEM_ID="$(gh api graphql -f query='
      query($project: ID!) {
        node(id: $project) {
          ... on ProjectV2 {
            items(first: 100) {
              nodes {
                id
                content { ... on Issue { number } }
              }
            }
          }
        }
      }' -f project="$PROJECT_ID" --jq ".data.node.items.nodes[] | select(.content.number==$num) | .id" | head -1)"
  fi

  if [[ -z "$ITEM_ID" || "$ITEM_ID" == "null" ]]; then
    echo "[gfw] Issue #$num not on project — skip"
    continue
  fi

  gh project item-edit --project-id "$PROJECT_ID" --id "$ITEM_ID" \
    --field-id "$STATUS_FIELD_ID" --single-select-option-id "$IN_REVIEW_OPTION_ID"
  echo "[gfw] Issue #$num → In Review"
done
