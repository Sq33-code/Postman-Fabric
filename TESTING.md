# 0.6.0 smoke-test checklist

Run `./gradlew clean runClient` (or `.\gradlew.bat clean runClient` on Windows). Keep a backup of your previous source and configs.

- [ ] Minecraft starts and the ClickGUI opens with Right Shift without blur crashes.
- [ ] HUD watermark reads `Postman v0.6.0`; enabling rainbow changes the entire text, and a static custom color also affects the entire text.
- [ ] Nametags + PlayerClone: name, health, ordered equipment, distinct durability percentages and enchantment labels are visible, upright, and remain aligned while moving the camera.
- [ ] Nametags + Freecam: clone is labeled; disable both and confirm vanilla labels return.
- [ ] ESP: single/double chests, ender chests, barrels and shulkers; check outline and fill while turning, changing FOV and GUI scale.
- [ ] HoleESP: fill/outline conform to the projected shape and do not create oversized flat rectangles.
- [ ] Tracers, Xray and NewChunks: overlays track the camera in first and third person.
- [ ] ViewModel/LowOffHand: move each hand independently; test left-handed mode, eating and offhand totem.
- [ ] Peek: put items in first, middle and last shulker slots; verify empty positions are retained.
- [ ] Disable render modules, change world, and re-enable; check for stale overlays or crashes.
- [ ] `./gradlew clean build` produces `build/libs/postman-0.6.0.jar`.

If any item fails, record the exact module, settings, screenshot, and the latest `run/logs/latest.log` or crash report.
