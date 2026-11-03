//
//  View.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class View {
  func destroy() {
    // очистка, если нужна
  }

  func paint(router: Router, painter: Painter, page: Page) {
    // to be overridden
  }

  func handlePointerDown(
    router: Router,
    page: Page,
    painterX: Float,
    painterY: Float,
    pointedX: Float,
    pointedY: Float,
    buttonNumber: Int
  ) {
    // to be overridden
  }
}
