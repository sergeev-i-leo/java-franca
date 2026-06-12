# Typescript France

**Typescript Franca** is an attempt to create a cross-platform transpiler that allows you to write UI and application logic once in Typescript, and transpiles it to native platforms.

Write your code once using Typescript, and Typescript France transpiles it to:
- **C++** (with Qt or Skia backend)
- **Java** (with Swing, JavaFX, or raw Canvas)
- **Swift** (with Canvas)

## Features

- **Single source of truth** — keep your core logic in one language (Typescript)
- **Native performance** — no embedded browsers or JS bridges; generated code runs at native speed

## Additiona Features
- **Abstracted rendering** — implement platform-specific `Device` and `Painter`, keep business logic pure
- **Animation system** — declarative animations with automatic repaint scheduling
- **Memory-safe design** — clear ownership rules, `destroy()` semantics (ready for hardcore manual memory management when transpiled to C++)

## Design Principles

* Simplicity first — if a feature makes the transpiler complex, leave it out initially
* Observability — generated code must be traceable and debuggable
* No magic — every transformation must be traceable from source to target
* Iterative — start with minimal subset, expand gradually
