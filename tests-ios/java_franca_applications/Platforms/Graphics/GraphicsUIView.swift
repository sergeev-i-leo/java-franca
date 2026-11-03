//
//  GraphicsUIView.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import UIKit
import CoreGraphics

class GraphicsUIView: UIView {
  private var router: GraphicsRouter?

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
    router = GraphicsRouter()
    router?.drawingView = self

    let device = GraphicsDevice.shared
    router?.setDevice(device)

    let page = Page(router: router!)
    let testView = TestView0()
    page.views.append(testView)
    router?.pushPage(page)

    // Запускаем первый рендер
    router?.startRepainting()
  }

  override func draw(_ rect: CGRect) {
    guard let context = UIGraphicsGetCurrentContext(),
          let router = router else { return }

    let painter = GraphicsPainter(context: context, size: bounds.size)
    router.topPage?.paint(painter)
  }

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
    guard let point = touches.first?.location(in: self) else { return }
    router?.handlePointerDown(Float(point.x), Float(point.y), 1)
    // setNeedsDisplay() убираем — перерисовка через CADisplayLink
  }
}
