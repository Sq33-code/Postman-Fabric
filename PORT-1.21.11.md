# Postman 4.0 — port preliminar a Minecraft 1.21.11

Código original: Postman Fabric 1.21.4 (ZIP suministrado). Se conserva la ClickGUI original, módulos, comandos, configuraciones y licencia MIT. No se incluye un JAR compilado.

## Ejecutar en Windows

Instala un JDK 21 y abre PowerShell en la carpeta donde está `gradlew.bat`.

```powershell
java -version
.\gradlew.bat --version
.\gradlew.bat runClient
```

Para generar el mod:

```powershell
.\gradlew.bat build
```

El JAR remapeado, si la compilación finaliza, estará en `build/libs/`. Right Shift abre la ClickGUI.

## Cambios

- Minecraft 1.21.11; Yarn 1.21.11+build.6; Fabric API 0.141.4+1.21.11; Loader 0.19.2; Loom 1.14.10; Gradle 9.2.1; Java 21.
- ClientModInitializer y metadatos con versión exacta de Minecraft.
- ClickGUI y HUD Editor: Click y KeyInput; RenderPipelines.GUI_TEXTURED; retirada de dependencia Satin (el shader activo no se usaba).
- Mixins de teclado, chat, conexión, desconexión, render 3D y NoSlow adaptados.
- Lectura de movimiento por Vec2f, coordenadas mediante getX/Y/Z, escala de Y corregida, campo de velocidad Vec3d y access widener actualizado.
- AutoDisconnect actualizado al argumento Text; comprobaciones de nulos en FullBright y Coordinates.

## Estado y limitaciones

Se realizó migración manual con consulta de la documentación Yarn. Se intentó `./gradlew compileJava --no-daemon`, pero el entorno no tiene resolución DNS para services.gradle.org ni maven.fabricmc.net, por lo que no llegó a compilar. No se verificó el arranque ni la funcionalidad individual de ningún módulo; pueden quedar errores de compilación o mixins que deban ajustarse. NoPush en líquidos y VibrantShader ya estaban incompletos/desactivados en la versión original. Se mantuvieron sus fuentes.

Documentación: https://maven.fabricmc.net/docs/yarn-1.21.11+build.6/ y https://fabricmc.net/2025/12/05/12111.html

## Revisión 2: errores de compileJava reportados en Windows

- `ModuleComponent`: se elimina `RenderSystem.setShaderTexture`; `DrawContext.drawTexture` ya recibe el identificador de la textura. Se conserva la imagen check.png.
- `CommandManager`: constructor `new ChatScreen("", false)` según Yarn build.6.
- `Sneak`: se reemplazan los dos comandos SHIFT eliminados por `PlayerInputC2SPacket` con el estado de movimiento existente; al desactivar, se restablece el input real. El resultado dentro del juego queda pendiente de prueba.
- Se mantienen las versiones de Gradle, Loom, Java y Fabric de la revisión anterior. Los cuatro errores reportados fueron corregidos en las fuentes. No se ha verificado una compilación completa en este entorno por falta de acceso a los repositorios.


## Revisión 3: crasheo al abrir ClickGUI

- El registro de Windows de la V2 confirma el arranque de Minecraft 1.21.11 y la entrada a un mundo local. El fallo posterior fue `Can only blur once per frame` en `ClickGuiScreen.render`.
- ClickGuiScreen y HudEditorScreen: el fondo se dibuja exclusivamente mediante la sobrescritura de `renderBackground`, sin invocarlo desde `render`. El fondo es ahora una capa negra semitransparente, sin blur, y sigue siendo configurable mediante `background`.
- HudEditorScreen consulta sus propios ajustes de fondo y pausa.
- Load: se omiten los archivos de configuración inexistentes al primer inicio y se conservan los valores por defecto; la carga de posiciones de la ClickGUI se difiere hasta que sus categorías existan. Load y Save crean la carpeta de configuración con mkdirs.
- No se modifica la dependencia de Gradle, Fabric, Minecraft ni Java; se conservan los módulos y componentes de la V2.
- Realms y `Failed to retrieve profile key pair` (HTTP 401) son avisos de autenticación separados del crash; no se modifica la autenticación.
- Revisión estática y comprobación de integridad ZIP realizadas. La ejecución de la V3 dentro del juego queda pendiente de prueba en Windows.
