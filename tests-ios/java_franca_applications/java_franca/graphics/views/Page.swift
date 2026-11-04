//
//  Page.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class Page {
  weak var device: Router?
  var views: [View] = []
  weak var nextPage: Page?

  init(device: Router) {
    self.device = device
  }

  func destroy() {
    views.removeAll()
  }

  func paint(_ painter: Painter) {
    guard let device = device else { return }
    for view in views {
      view.paint(device: device, painter: painter, page: self)
    }
  }

  func handlePointerDown(_ pointedX: Float, _ pointedY: Float, _ buttonNumber: Int) {
    guard let device = device else { return }
    for view in views {
      view.handlePointerDown(
        device: device,
        page: self,
        painterX: 0,
        painterY: 0,
        pointedX: pointedX,
        pointedY: pointedY,
        buttonNumber: buttonNumber
      )
    }
  }
}
