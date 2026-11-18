# Render port — Postman 4.0 / Minecraft 1.21.11

Base: Postman Fabric V3 (Java 21, Loom 1.14.10). Source reference: postman-3.001 (15 render modules).

Registered: CameraClip, DamageTiltCorrection (damageTilt), ESP, Freecam, FullBright, HoleESP, LowOffHand, Nametags, NewChunks, NoRender, Peek, Tracers, ViewModel, World and Xray.

- PlayerClone and Freecam use a shared client-only FakePlayerEntity with an independent GameProfile UUID, player skin snapshot, copied health, hands and armor. Nametags includes these entities explicitly, even when not in the usual player list. FakePlayer ping is omitted and the tag says LOCAL. Native GUI item rendering keeps icons upright. `fakePlayers` is enabled by default; `protType` shows the protection enchantment type and level.
- The original OpenGL 1.12 renderer has been replaced with GUI-projected overlays for ESP, Nametags, Tracers, HoleESP and Xray. Projection may not exactly follow view bobbing and damage tilt. Xray is an ore overlay, not transparent terrain. NewChunks marks chunks loaded while enabled, not proven newly generated chunks. Peek is a shulker tooltip preview (occupied slots, compacted). World and render mixin hooks are subject to in-game verification; optional hooks may not apply. FullBright retains its previous implementation.
- Original V3 GUI and HUD crash correction are retained.

## Smoke tests (Windows PowerShell)

1. `./gradlew clean runClient`
2. Join a local creative world, equip armor and hold an item, then open ClickGUI (right Shift).
3. Enable PlayerClone in Player and Nametags in Render. Walk several blocks away and turn to face the clone. Verify FakePlayer | HP | LOCAL and upright equipment. Disable PlayerClone; make sure the clone and label disappear. Test Freecam and its label separately.
4. Test the remaining render modules individually; keep scan ranges low on low-memory hardware.
5. Run `./gradlew build` after testing.

Verification: archive and Java source inventory checked; several target Yarn 1.21.11+build.6 signatures checked. Gradle could not download 9.2.1 in the editing environment (DNS UnknownHostException services.gradle.org). Compilation, mixin application and rendering in this revision have not been verified by executing Minecraft.
