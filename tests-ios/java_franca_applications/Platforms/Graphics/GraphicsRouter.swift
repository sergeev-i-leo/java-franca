//
//  GraphicsRouter.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

import UIKit
import CoreGraphics

class GraphicsRouter: Router {
  weak var drawingView: GraphicsUIView?
  private var displayLink: CADisplayLink?
  private var isRunning = false

  override func startRepainting() {
    guard !isRunning else { return }
    isRunning = true
    displayLink = CADisplayLink(target: self, selector: #selector(tick))
    displayLink?.add(to: .main, forMode: .common)
  }

  @objc private func tick() {
    let needsRedraw = needsRepainting()

    if needsRedraw {
      drawingView?.setNeedsDisplay()
    }

    if !needsNextRepainting() {
      stopRepainting()
    }
  }

  func stopRepainting() {
    displayLink?.invalidate()
    displayLink = nil
    isRunning = false
  }
}
