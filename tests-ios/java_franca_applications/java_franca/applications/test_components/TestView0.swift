//
//  TestView0.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class TestView0: View {
  private var myTween: MyTween?

  override func paint(device: Router, painter: Painter, page: Page) {
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
    device: Router,
    page: Page,
    painterX: Float,
    painterY: Float,
    pointedX: Float,
    pointedY: Float,
    buttonNumber: Int
  ) {
    if buttonNumber == 1 {
      removeViewAnimation(device)
      myTween = MyTween(page: page, testView0: self)
      device.registerTween(myTween!)
    } else {
      device.requestRepainting()
    }
  }

  func removeViewAnimation(_ device: Router) {
    if let tween = myTween {
      device.removeTween(tween)
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

  override func needsRepainting(_ device: Router) -> Bool {
    let result = super.needsRepainting(device)
    if !result {
      testView0?.removeViewAnimation(device)
      return false
    }
    return true
  }
}
