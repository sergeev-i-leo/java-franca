//
//  ContentView.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import SwiftUI

struct ContentView: SwiftUI.View {
  var body: some SwiftUI.View {
    TabView {
      FirstView()
        .tabItem {
          Label("Первая", systemImage: "01.square")
        }

      SecondView()
        .tabItem {
          Label("Тест", systemImage: "02.square")
        }

      ThirdView()
        .tabItem {
          Label("Третья", systemImage: "03.square")
        }
    }
  }
}
