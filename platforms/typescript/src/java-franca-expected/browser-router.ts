import {Router} from "@java-franca/expected/router";
import {Page} from "@java-franca/graphics/page";
import {Painter} from "@java-franca/expected/painter";

export class BrowserRouter extends Router {

  htmlCanvasElement: HTMLCanvasElement | null = null;
  painter: Painter | null =null;

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
  }

  attach(parentHTMLElement: HTMLElement, rootPage: Page): void {
    this.htmlCanvasElement = document.createElement("Canvas") as HTMLCanvasElement;
    parentHTMLElement.appendChild(this.htmlCanvasElement);
    this.htmlCanvasElement.style.width = "100%";
    this.htmlCanvasElement.style.height = "100%";
    this.htmlCanvasElement.style.display = "block";

    requestAnimationFrame(() => {
      if (this.htmlCanvasElement) {
        const rect = this.htmlCanvasElement.getBoundingClientRect();
        this.htmlCanvasElement.width = rect.width;
        this.htmlCanvasElement.height = rect.height;
      }
    });

    this.htmlCanvasElement.addEventListener("pointerdown", this.boundHandlePointerDown);
    this.htmlCanvasElement.addEventListener("pointermove", this.boundHandlePointerMove);
    this.htmlCanvasElement.addEventListener("pointerup", this.boundHandlePointerUp);
    this.htmlCanvasElement.addEventListener("contextmenu", this.preventContextMenu);

    this.pushPage(rootPage);

  }

  detach(): void {
    if (this.htmlCanvasElement) {
      this.htmlCanvasElement.removeEventListener("pointerdown", this.boundHandlePointerDown);
      this.htmlCanvasElement.removeEventListener("pointermove", this.boundHandlePointerMove);
      this.htmlCanvasElement.removeEventListener("pointerup", this.boundHandlePointerUp);
      this.htmlCanvasElement.removeEventListener("contextmenu", this.preventContextMenu);
      this.htmlCanvasElement = null;
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
