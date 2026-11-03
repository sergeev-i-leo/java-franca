//
//  Tween.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class Tween {
  weak var page: Page?
  weak var view: View?
  var ease: Ease?

  var tweenId: Int = 0
  var registeredTime: Int64 = 0
  weak var previousTween: Tween?
  var nextTween: Tween?

  init(page: Page?, view: View?, duration: Int64, tickerType: Int) {
    self.page = page
    self.view = view

    switch tickerType {
    case Ease.EASE_LINEAR:
      self.ease = LinearEase(initialValue: 0, targetValue: 100, duration: duration)
    default:
      // repaint just once
      self.ease = nil
    }
  }

  func setEase(_ ease: Ease) {
    self.ease = ease
  }

  func getEase() -> Ease? {
    return ease
  }

  func needsRepainting(_ router: Router) -> Bool {
    guard let ease = ease else {
      // one shot animation
      return true
    }
    return ease.tick(router, startedTime: registeredTime)
  }

  func needsNextRepainting(_ router: Router) -> Bool {
    guard let ease = ease else {
      // one shot animation
      return false
    }
    return ease.isRunning()
  }

  func destroy() {
    // очистка, если нужна
    page = nil
    view = nil
    ease = nil
    previousTween = nil
    nextTween = nil
  }
}
