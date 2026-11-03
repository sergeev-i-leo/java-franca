//
//  Page.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class Page {
  weak var router: Router?
  var views: [View] = []
  weak var nextPage: Page?

  init(router: Router) {
    self.router = router
  }

  func destroy() {
    views.removeAll()
  }

  func paint(_ painter: Painter) {
    guard let router = router else { return }
    for view in views {
      view.paint(router: router, painter: painter, page: self)
    }
  }

  func handlePointerDown(_ pointedX: Float, _ pointedY: Float, _ buttonNumber: Int) {
    guard let router = router else { return }
    for view in views {
      view.handlePointerDown(
        router: router,
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
