//
//  GraphicsDevice.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class GraphicsDevice: Device {
  static let shared = GraphicsDevice()

  private var _isReady = false

  override func getTime() -> Int64 {
    return Int64(Date().timeIntervalSince1970 * 1000)
  }

  func isReady() -> Bool {
    return _isReady
  }

  func loadResources() async {
    // Для Core Graphics ничего грузить не нужно
    _isReady = true
  }
}
