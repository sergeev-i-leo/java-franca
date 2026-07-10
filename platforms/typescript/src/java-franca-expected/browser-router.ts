import {Router} from "@java-franca/expected/router";
import {Page} from "@java-franca/graphics/page";
import {Painter} from "@java-franca/expected/painter";

export class BrowserRouter extends Router {

  parentHTMLElement: HTMLElement | null = null;
  htmlCanvasElement: HTMLCanvasElement | null = null;
  painter: Painter | null =null;

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

  attach(parentHTMLElement: HTMLElement, rootPage: Page): void {
    this.parentHTMLElement = parentHTMLElement;
    this.htmlCanvasElement = document.createElement("canvas") as HTMLCanvasElement;
    this.parentHTMLElement.appendChild(this.htmlCanvasElement);

    this.htmlCanvasElement.addEventListener("pointerdown", this.boundHandlePointerDown);
    this.htmlCanvasElement.addEventListener("pointermove", this.boundHandlePointerMove);
    this.htmlCanvasElement.addEventListener("pointerup", this.boundHandlePointerUp);
    this.htmlCanvasElement.addEventListener("contextmenu", this.preventContextMenu);

    this.pushPage(rootPage);

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

  detach(): void {
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

    const canvasX = (pointerEvent.clientX - rect.left) * scaleX;
    const canvasY = (pointerEvent.clientY - rect.top) * scaleY;

    let button = 0;
    if (pointerEvent.button === 0) {
      button = 1;
    } else if (pointerEvent.button === 1) {
      button = 2;
    } else if (pointerEvent.button === 2) {
      button = 3;
    }

    this.pointerId = pointerEvent.pointerId;
    this.htmlCanvasElement.setPointerCapture(pointerEvent.pointerId);

    super.handlePointerDown(canvasX, canvasY, button);
  }

  onPointerMove(pointerEvent: PointerEvent): void {
    if ((!this.htmlCanvasElement) || (this.pointerId !== pointerEvent.pointerId)) {
      return;
    }
    const rect = this.htmlCanvasElement.getBoundingClientRect();
    const scaleX = this.htmlCanvasElement.width / rect.width;
    const scaleY = this.htmlCanvasElement.height / rect.height;

    const canvasX = (pointerEvent.clientX - rect.left) * scaleX;
    const canvasY = (pointerEvent.clientY - rect.top) * scaleY;

    super.handlePointerMove(canvasX, canvasY);
  }

  onPointerUp(pointerEvent: PointerEvent): void {
    if (pointerEvent.pointerId != this.pointerId) {
      return;
    }
    this.pointerId = null;
    this.htmlCanvasElement?.releasePointerCapture(pointerEvent.pointerId);
    super.handlePointerUp();
  }

  preventContextMenu(event: any): void {
    event.preventDefault();
  }

}
