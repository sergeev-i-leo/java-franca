//
//  Ease.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class Ease {
  static let EASE_LINEAR = 1

  var initialValue: Int
  var currentValue: Int
  var targetValue: Int
  var duration: Int64

  init(initialValue: Int, targetValue: Int, duration: Int64) {
    self.initialValue = initialValue
    self.currentValue = initialValue
    self.targetValue = targetValue
    self.duration = duration
  }

  func destroy() {
    // очистка, если нужна
  }

  func isRunning() -> Bool {
    return currentValue != targetValue
  }

  func tick(_ router: Router, startedTime: Int64) -> Bool {
    // returns true when needs repainting
    return true
  }
}
