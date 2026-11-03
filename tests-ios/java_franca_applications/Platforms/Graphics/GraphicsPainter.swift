//
//  GraphicsPainter.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation
import CoreGraphics
import UIKit

class GraphicsPainter: Painter {
  private let context: CGContext
  private let size: CGSize

  init(context: CGContext, size: CGSize) {
    self.context = context
    self.size = size
  }

  override func paintText(_ text: String, x: Float, y: Float, deviceFontKey: String, deviceColor: Int) {
    let nsText = text as NSString
    let fontSize: CGFloat = 20
    let font = UIFont.systemFont(ofSize: fontSize)
    let attributes: [NSAttributedString.Key: Any] = [
      .font: font,
      .foregroundColor: UIColor.black.withAlphaComponent(CGFloat(deviceColor) / 255.0)
    ]
    
    // Рисуем текст В ЭТОМ ЖЕ КОНТЕКСТЕ (переданном в конструктор)
    nsText.draw(at: CGPoint(x: CGFloat(x), y: CGFloat(y)), withAttributes: attributes)
  }
}
