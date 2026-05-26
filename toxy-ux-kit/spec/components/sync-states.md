# Sync states (phone + watch)

> Semantic colors use **ToXY chrome** — glucose values inside states still use **AGP**.

## Phone

| State | Visual | Token |
|-------|--------|-------|
| Synced + ack | Green chevron / dot | `toxy.color.sync.ok` |
| Fetching | Neutral spinner | `toxy.color.text.secondary` |
| Watch offline | Badge / pill | `toxy.color.sync.warn` |
| Dexcom auth error | Error banner | `toxy.color.sync.error` |
| Push pending (no ack) | Amber indicator | `toxy.color.sync.warn` |

## Watch tile

| State | Value | Trend | Sync button |
|-------|-------|-------|-------------|
| Fresh | AGP by range | AGP | Enabled |
| Stale | Last value | `agp.unknown` | Enabled |
| Refreshing | Last value | — | Disabled / loading |
| Failed message | Last value | — | Enabled |
| No data | `--` | hidden | Enabled |

## Watch refresh messages

| Message | Duration | Color |
|---------|----------|-------|
| Refreshing… | 45 s | toxy.text.secondary |
| Up to date | 45 s | toxy.sync.ok |
| Phone unavailable | 90 s | toxy.sync.error |
| Sync failed | 90 s | toxy.sync.error |
