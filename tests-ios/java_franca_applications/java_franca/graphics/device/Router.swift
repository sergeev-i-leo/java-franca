//
//  Router.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import Foundation

class Router {
  var device: Device?
  var topPage: Page?

  // animation orchestration
  private var lastTweenId: Int = 0
  private var firstTween: Tween?

  func setDevice(_ device: Device) {
    self.device = device
  }

  func getDevice() -> Device? {
    return device
  }

  func getTopPage() -> Page? {
    return topPage
  }

  func pushPage(_ page: Page) {
    page.nextPage = topPage
    topPage = page
  }

  func popPage() {
    if let page = topPage {
      topPage = page.nextPage
    }
  }

  func paint(_ painter: Painter) {
    topPage?.paint(painter)
  }

  func handlePointerDown(_ pointedX: Float, _ pointedY: Float, _ buttonNumber: Int) {
    topPage?.handlePointerDown(pointedX, pointedY, buttonNumber)
  }

  func requestRepainting() {
    // one-shot animation
    let tween = Tween(page: nil, view: nil, duration: 0, tickerType: 0)
    registerTween(tween)
  }

  func registerTween(_ tween: Tween) {
    lastTweenId += 1
    tween.tweenId = lastTweenId
    tween.registeredTime = getDevice()?.getTime() ?? 0

    tween.nextTween = firstTween
    firstTween?.previousTween = tween
    firstTween = tween

    startRepainting()
  }

  func startRepainting() {
    // to be overridden
  }

  func removeTween(_ tween: Tween) {
    var currentTween = firstTween
    while let current = currentTween {
      if current.tweenId == tween.tweenId {
        let previousTween = current.previousTween
        let nextTween = current.nextTween

        previousTween?.nextTween = nextTween
        nextTween?.previousTween = previousTween

        if current === firstTween {
          firstTween = nextTween
        }
        break
      }
      currentTween = current.nextTween
    }

    if firstTween == nil {
      lastTweenId = 0
    }
  }

  func needsRepainting() -> Bool {
    var result = false
    var currentTween = firstTween

    while let current = currentTween {
      result = current.needsRepainting(self) || result

      if current.getEase() == nil {
        // one-shot tween
        let previousTween = current.previousTween
        let nextTween = current.nextTween

        previousTween?.nextTween = nextTween
        nextTween?.previousTween = previousTween

        if current.tweenId == firstTween?.tweenId {
          firstTween = nextTween
        }

        current.destroy()
        currentTween = nextTween
      } else {
        currentTween = current.nextTween
      }
    }

    return result
  }

  func needsNextRepainting() -> Bool {
    var result = false
    var currentTween = firstTween

    while let current = currentTween {
      if current.getEase() == nil {
        // one-shot tween created during paint()
        result = true
      } else {
        result = current.needsNextRepainting(self) || result
      }
      currentTween = current.nextTween
    }

    return result
  }
}
