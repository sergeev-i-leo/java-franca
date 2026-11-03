//
//  LinearEase.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class LinearEase: Ease {
  override init(initialValue: Int, targetValue: Int, duration: Int64) {
    super.init(initialValue: initialValue, targetValue: targetValue, duration: duration)
  }

  override func destroy() {
    super.destroy()
  }

  override func tick(_ router: Router, startedTime: Int64) -> Bool {
    // returns true when needs repainting

    if currentValue == targetValue {
      return false
    }

    let currentTime = router.getDevice()?.getTime() ?? 0

    if currentTime >= startedTime + duration {
      currentValue = targetValue
      return true
    }

    currentValue = initialValue + Int(Double(targetValue - initialValue) * (Double(currentTime - startedTime) / Double(duration)))
    
    print("tick: currentValue = \(currentValue)")

    return true
  }
}
