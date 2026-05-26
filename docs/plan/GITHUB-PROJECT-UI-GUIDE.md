# GitHub Project — step-by-step UI guide

> Repo: **ToXY0392/Glucose-For-Watch** · Issues **#1–#12** already created.

You are on: **Projects** (empty screen) → green **+ New project** button.

---

## Step 1 — Create the board (30 sec)

1. Click **+ New project**
2. Choose **Board** (not Table, not Roadmap to start)
3. Title:
   ```
   Glucose For Watch v0.5 to v0.6
   ```
4. **Create project**

---

## Step 2 — Status columns (2 min)

GitHub Projects v2 uses the **Status** field as board columns.

1. Open the created project
2. Top right: **...** (menu) → **Settings**
3. **Fields** section → click **Status** (built-in)
4. **Edit field** / **Manage options**
5. Replace the list with (add with **+ Add option**, remove default Todo/Done if duplicates):

| Option (exact name) | Suggested color |
|---------------------|-----------------|
| Backlog | Gray |
| Ready | Blue |
| In Progress | Yellow |
| In Review | Orange |
| QA Hardware | Purple |
| Gate Ready | Green |
| Done | Green |

6. **Save changes** → back to board: you should see **7 columns**

---

## Step 3 — Add the 12 issues (1 min)

1. On the board, click **+ Add item** (bottom of a column) **or** **Add items** at the top
2. Choose **Issues from repository** → **Glucose-For-Watch**
3. Check **all** open issues (#1 to #12) or search:
   ```
   is:issue is:open
   ```
4. **Add selected items**

You should see **12 cards** (probably all in **Backlog** or **Todo**).

---

## Step 4 — Place cards (drag & drop)

Drag each card to the right column:

| Column | Issues |
|--------|--------|
| **In Progress** | **#3** C.7 soak |
| **Ready** | **#1** X.3 · **#2** X.7 · **#4** incident |
| **Backlog** | **#5** #6 #7 #8 #9 #10 #11 #12 |

Solo rule: **only one** card In Progress (#3 soak).

---

## Step 5 — Custom fields (2 min, optional)

**Settings** → **Fields** → **+ New field**

| Name | Type | Options |
|------|------|---------|
| Bloc | Single select | S, X, A, M, B, C, D, F |
| Gate | Single select | G-X, G-A, G-M, G-B, G-C, G-D, G-M7, G-M8 |
| Sync touch | Checkbox | — |
| Hardware QA | Checkbox | — |

Fill on cards (click card → right panel):

| Issue | Bloc | Gate | Sync | Hardware |
|-------|------|------|------|----------|
| #1 | X | G-X | yes | yes |
| #2 | X | G-X | no | no |
| #3 | C | G-C | yes | yes |
| #4 | X | G-X | yes | no |
| #8 | B | G-B | yes | yes |
| #9-11 | C | G-C | varies | yes |

---

## Step 6 — Link to repo

1. Project **Settings** → **Manage access** / **Linked repositories**
2. **Link repository** → **Glucose-For-Watch**

Or from https://github.com/ToXY0392/Glucose-For-Watch/projects → **Link a project** → select the created board.

---

## Verification

- [ ] 7 Status columns visible
- [ ] 12 cards (#1–#12)
- [ ] #3 in **In Progress**
- [ ] #1 #2 #4 in **Ready**
- [ ] Repo linked to project

**Project URL:** `https://github.com/users/ToXY0392/projects/<number>`

---

## Automation (after `project` scope on token)

```powershell
gh auth login --scopes "project,read:project,repo"
.\scripts\dev\setup_github_project.ps1
```
