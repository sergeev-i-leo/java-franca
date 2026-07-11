import {Router} from "@java-franca/expected/router";
import {Page} from "@java-franca/graphics/page";
import {Painter} from "@java-franca/expected/painter";

export class BrowserRouter extends Router {

  parentHTMLElement: HTMLElement | null = null;
  htmlCanvasElement: HTMLCanvasElement | null = null;
  painter: Painter | null = null;

  private resizeObserver: ResizeObserver | null = null;
  private resizePending: boolean = false;

  animationFrameId: number | null = null;
  lastTickTime: number = 0;

  pointerId: any;

  boundHandlePointerDown: (pointerEvent: PointerEvent) => void;
  boundHandlePointerMove: (pointerEvent: PointerEvent) => void;
  boundHandlePointerUp: (pointerEvent: PointerEvent) => void;
  boundHandleContextMenu: (pointerEvent: PointerEvent) => void;

  constructor() {
    super();

    this.boundHandlePointerDown = this.onPointerDown.bind(this);
    this.boundHandlePointerMove = this.onPointerMove.bind(this);
    this.boundHandlePointerUp = this.onPointerUp.bind(this);
    this.boundHandleContextMenu = this.preventContextMenu.bind(this);

    this.resizeObserver = new ResizeObserver((entries) => {
      this.requestResize();
    });
  }

  mount(parentHTMLElement: HTMLElement): void {
    this.parentHTMLElement = parentHTMLElement;
    this.htmlCanvasElement = document.createElement("canvas") as HTMLCanvasElement;
    this.parentHTMLElement.appendChild(this.htmlCanvasElement);

    this.htmlCanvasElement.addEventListener("pointerdown", this.boundHandlePointerDown);
    this.htmlCanvasElement.addEventListener("pointermove", this.boundHandlePointerMove);
    this.htmlCanvasElement.addEventListener("pointerup", this.boundHandlePointerUp);
    this.htmlCanvasElement.addEventListener("contextmenu", this.preventContextMenu);

    this.resizeObserver!.observe(this.parentHTMLElement);

    this.resize();
  }

  private requestResize(): void {
    if (this.resizePending) {
      return;
    }

    this.resizePending = true;
    requestAnimationFrame(() => {
      this.resize();
      this.resizePending = false;
    });
  }

  resize() {
    if (!this.parentHTMLElement) {
      return;
    }
    if (!this.htmlCanvasElement) {
      return;
    }

    const rect = this.parentHTMLElement.getBoundingClientRect();
    const width = Math.floor(rect.width);
    const height = Math.floor(rect.height);

    this.htmlCanvasElement.width = width;
    this.htmlCanvasElement.height = height;

    this.htmlCanvasElement.style.width = width + "px";
    this.htmlCanvasElement.style.height = height + "px";
    this.htmlCanvasElement.style.display = "block";

    this.requestRepainting();
  }

  run(page: Page) {
    this.pushPage(page);
    this.requestRepainting();
  }

  startRepainting(): void {
    if (this.animationFrameId !== null) {
      return;
    }
    this.lastTickTime = performance.now();
    this.animationFrameId = requestAnimationFrame(this.tick.bind(this));
  }

  private tick(now: number): void {
    if ((!this.painter) || (!this.htmlCanvasElement)) {
      this.stopRepainting();
      return;
    }

    if (now - this.lastTickTime < 16) {
      this.animationFrameId = requestAnimationFrame(this.tick.bind(this));
      return;
    }
    this.lastTickTime = now;

    const needsRedraw = this.needsRepainting();

    if (needsRedraw) {
      if (this.painter) {
        this.preparePainting(this.painter);
        this.doPainting(this.painter);
        this.finishPainting(this.painter);
      }
    }

    if (this.needsNextRepainting()) {
      this.animationFrameId = requestAnimationFrame(this.tick.bind(this));
    } else {
      this.stopRepainting();
    }
  }

  private stopRepainting(): void {
    if (this.animationFrameId !== null) {
      cancelAnimationFrame(this.animationFrameId);
      this.animationFrameId = null;
    }
  }

  unmount(): void {
    if (this.htmlCanvasElement) {
      this.htmlCanvasElement.removeEventListener("pointerdown", this.boundHandlePointerDown);
      this.htmlCanvasElement.removeEventListener("pointermove", this.boundHandlePointerMove);
      this.htmlCanvasElement.removeEventListener("pointerup", this.boundHandlePointerUp);
      this.htmlCanvasElement.removeEventListener("contextmenu", this.preventContextMenu);
      this.htmlCanvasElement = null;
    }
    if ((this.resizeObserver) && (this.parentHTMLElement)) {
      this.resizeObserver.unobserve(this.parentHTMLElement);
      this.resizeObserver.disconnect();
      this.resizeObserver = null;
    }
  }

  onPointerDown(pointerEvent: PointerEvent): void {
    if (!this.htmlCanvasElement) {
      return;
    }

    const rect = this.htmlCanvasElement.getBoundingClientRect();
    const scaleX = this.htmlCanvasElement.width / rect.width;
    const scaleY = this.htmlCanvasElement.height / rect.height;

    const pointedX = (pointerEvent.clientX - rect.left) * scaleX;
    const pointedY = (pointerEvent.clientY - rect.top) * scaleY;

    let buttonNumber = 0;
    if (pointerEvent.button === 0) {
      buttonNumber = 1;
    } else if (pointerEvent.button === 1) {
      buttonNumber = 2;
    } else if (pointerEvent.button === 2) {
      buttonNumber = 3;
    }

    this.pointerId = pointerEvent.pointerId;
    this.htmlCanvasElement.setPointerCapture(pointerEvent.pointerId);

    this.handlePointerDown(pointedX, pointedY, buttonNumber);
  }

  handlePointerDown(pointedX: number, pointedY: number, buttonNumber: number): void {
    super.handlePointerDown(pointedX, pointedY, buttonNumber);
  }

  onPointerMove(pointerEvent: PointerEvent): void {
    if ((!this.htmlCanvasElement) || (this.pointerId !== pointerEvent.pointerId)) {
      return;
    }
    const rect = this.htmlCanvasElement.getBoundingClientRect();
    const scaleX = this.htmlCanvasElement.width / rect.width;
    const scaleY = this.htmlCanvasElement.height / rect.height;

    const pointedX = (pointerEvent.clientX - rect.left) * scaleX;
    const pointedY = (pointerEvent.clientY - rect.top) * scaleY;

    this.handlePointerMove(pointedX, pointedY);
  }

  handlePointerMove(pointedX: number, pointedY: number): void {
    super.handlePointerMove(pointedX, pointedY);
  }

  onPointerUp(pointerEvent: PointerEvent): void {
    if (pointerEvent.pointerId != this.pointerId) {
      return;
    }
    this.pointerId = null;
    this.htmlCanvasElement?.releasePointerCapture(pointerEvent.pointerId);
    this.handlePointerUp();
  }

  handlePointerUp(): void {
    super.handlePointerUp();
  }

  preventContextMenu(event: any): void {
    event.preventDefault();
  }

}
