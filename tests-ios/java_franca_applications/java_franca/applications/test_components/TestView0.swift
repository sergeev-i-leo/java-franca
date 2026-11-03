//
//  TestView0.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class TestView0: View {
  private var myTween: MyTween?

  override func paint(router: Router, painter: Painter, page: Page) {
    let x = Float(Int.random(in: 0..<50)) + 50
    let y = Float(Int.random(in: 0..<50)) + 50

    if let tween = myTween {
      let value = tween.getEase()?.currentValue ?? 0
      painter.paintText(String(value), x: x, y: y, deviceFontKey: "", deviceColor: 255)
    } else {
      painter.paintText("PAINT", x: x, y: y, deviceFontKey: "", deviceColor: 255)
    }
  }

  override func handlePointerDown(
    router: Router,
    page: Page,
    painterX: Float,
    painterY: Float,
    pointedX: Float,
    pointedY: Float,
    buttonNumber: Int
  ) {
    if buttonNumber == 1 {
      removeViewAnimation(router)
      myTween = MyTween(page: page, testView0: self)
      router.registerTween(myTween!)
    } else {
      router.requestRepainting()
    }
  }

  func removeViewAnimation(_ router: Router) {
    if let tween = myTween {
      router.removeTween(tween)
      myTween = nil
    }
  }
}

// MARK: - MyTween

private class MyTween: Tween {
  private weak var testView0: TestView0?

  init(page: Page, testView0: TestView0) {
    self.testView0 = testView0
    super.init(page: page, view: testView0, duration: 500, tickerType: Ease.EASE_LINEAR)
  }

  override func needsRepainting(_ router: Router) -> Bool {
    let result = super.needsRepainting(router)
    if !result {
      testView0?.removeViewAnimation(router)
      return false
    }
    return true
  }
}
