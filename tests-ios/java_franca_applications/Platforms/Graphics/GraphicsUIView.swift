//
//  GraphicsUIView.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import UIKit
import CoreGraphics

class GraphicsUIView: UIView {
  private var device: GraphicsRouter?

  override init(frame: CGRect) {
    super.init(frame: frame)
    setup()
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
    setup()
  }

  private func setup() {
    backgroundColor = .white
    device = GraphicsRouter()
    device?.drawingView = self

    let device = GraphicsDevice.shared
    device?.setDevice(device)

    let page = Page(device: device!)
    let testView = TestView0()
    page.views.append(testView)
    device?.pushPage(page)

    // Запускаем первый рендер
    device?.startRepainting()
  }

  override func draw(_ rect: CGRect) {
    guard let context = UIGraphicsGetCurrentContext(),
          let device = device else { return }

    let painter = GraphicsPainter(context: context, size: bounds.size)
    device.topPage?.paint(painter)
  }

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
    guard let point = touches.first?.location(in: self) else { return }
    device?.handlePointerDown(Float(point.x), Float(point.y), 1)
    // setNeedsDisplay() убираем — перерисовка через CADisplayLink
  }
}
