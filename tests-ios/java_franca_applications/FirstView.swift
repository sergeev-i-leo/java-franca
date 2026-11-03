//
//  FirstView.swift
//  java_franca_applications
//
//  Created by admin on 12.06.2026.
//

import SwiftUI

struct FirstView: SwiftUI.View {
  var body: some SwiftUI.View {
    GraphicsViewRepresentable()
      .ignoresSafeArea()
  }
}

struct GraphicsViewRepresentable: UIViewRepresentable {
  func makeUIView(context: Context) -> GraphicsUIView {
    return GraphicsUIView()
  }

  func updateUIView(_ uiView: GraphicsUIView, context: Context) {}
}
