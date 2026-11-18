# Changelog

## 0.6.0 — Render and HUD update (development)

### Changed
- Renamed the development version from the informal V5/V6 naming to 0.6.0. Minecraft remains 1.21.11.
- Gradle version is now the source of truth for Fabric metadata, archive name, and in-game version text.
- Watermark uses the chosen custom/rainbow color for the entire label.
- World overlays capture the render-pass view and projection matrices rather than estimating them from FOV, yaw and pitch.
- Box fills use clipped projected polygons; outlines are clipped to the viewport. Boxes intersecting the near plane are skipped to avoid large artifacts.
- Storage ESP uses the block outline bounding shape and refreshes range checks per frame.
- HoleESP fill restored using the corrected polygon renderer.
- Nametags displays color-coded durability, including 100%, with smaller text and wider item spacing. FakePlayer / Freecam support retained.
- ViewModel and LowOffHand apply offsets once on the hand-local matrix after the vanilla push, including during use animations.
- Peek preserves empty shulker inventory slots and draws a 9×3 grid.
- Added README credit for Sq33, retained original author and MIT license, and added GitHub Actions build workflow.

### Validation and limitations
- Source changes prepared against Yarn 1.21.11+build.6; full build blocked by DNS resolving services.gradle.org.
- In-game rendering and mixin application require testing. Projected overlays do not provide native depth testing; near-plane-intersecting boxes can disappear rather than explode across the screen.
- NewChunks is a session-loaded-chunk indicator. Xray is an ore highlight. Existing incomplete legacy features are not represented as finished.
