package me.swift.step_gs.contract;

import me.swift.engine.contract.OptionalInt;

@FunctionalInterface
public interface OptionalIntConsumer {
  void accept(OptionalInt result);
}
