//
//  SecondView.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import SwiftUI

struct SecondView: SwiftUI.View {
  @State private var counter = 0

  var body: some SwiftUI.View {
    VStack(spacing: 20) {
      Text("Тестовая вкладка")
        .font(.title)

      Text("Счётчик: \(counter)")
        .font(.largeTitle)
        .foregroundColor(.blue)

      Button("Нажми меня") {
        counter += 1
      }
      .buttonStyle(.borderedProminent)
    }
    .padding()
  }
}

#Preview {
  SecondView()
}
