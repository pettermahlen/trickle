/*
 * Copyright 2013-2014 Spotify AB. All rights reserved.
 *
 * The contents of this file are licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.trickle;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Maps.newHashMap;

class TraverseState {
  private final Map<Input<?>, Object> bindings;
  private final Map<Graph<?>, ListenableFuture<?>> visited = newHashMap();
  private final List<FutureCallInformation> callInformation = newLinkedList();
  private final Executor executor;

  TraverseState(Map<Input<?>, Object> bindings, Executor executor) {
    this.bindings = checkNotNull(bindings, "bindings");
    this.executor = checkNotNull(executor, "executor");
  }

  <T> T getBinding(Input<T> input) {
    checkNotNull(input, "input");

    // this cast is fine because the API enforces it
    //noinspection unchecked
    return (T) bindings.get(input);
  }

  <T> ListenableFuture<T> futureForGraph(Graph<T> graph) {
    checkNotNull(graph, "node");
    final ListenableFuture<T> future;

    if (hasVisited(graph)) {
      future = getVisited(graph);
    } else {
      future = graph.run(this);
      visit(graph, future);
    }
    return future;
  }

  <T> boolean hasVisited(Graph<T> graph) {
    checkNotNull(graph, "graph");

    return visited.containsKey(graph);
  }

  <T> ListenableFuture<T> getVisited(Graph<T> graph) {
    checkNotNull(graph, "graph");

    // this cast is fine because the API enforces it
    //noinspection unchecked
    return (ListenableFuture<T>) visited.get(graph);
  }

  <T> void visit(Graph<T> graph, ListenableFuture<T> future) {
    checkNotNull(graph, "graph");
    checkNotNull(future, "future");

    visited.put(graph, future);
  }

  Executor getExecutor() {
    return executor;
  }

  public List<FutureCallInformation> getCallInformation() {
    return ImmutableList.copyOf(callInformation);
  }

  void addBindings(Map<Input<?>, Object> newBindings) {
    Sets.SetView<Input<?>> intersection = Sets.intersection(bindings.keySet(), newBindings.keySet());
    checkState(intersection.isEmpty(), "Duplicate binding for inputs: %s", intersection);
    bindings.putAll(newBindings);
  }

  void record(NodeInfo node, List<Dep<?>> deps, List<ListenableFuture<?>> parameterValues) {
    List<NodeInfo> parameters = Lists.transform(deps, new Function<Dep<?>, NodeInfo>() {
      @Override
      public NodeInfo apply(Dep<?> input) {
        return input.getNodeInfo();
      }
    });

    callInformation.add(new FutureCallInformation(node, parameters, parameterValues));
  }

  static TraverseState empty(Executor executor) {
    return new TraverseState(Maps.<Input<?>, Object>newHashMap(), executor);
  }

  static class FutureCallInformation {
    final NodeInfo node;
    final List<NodeInfo> parameters;
    final List<ListenableFuture<?>> parameterFutures;

    FutureCallInformation(NodeInfo node, List<NodeInfo> parameters,
                          List<ListenableFuture<?>> parameterFutures) {
      this.node = node;
      this.parameters = parameters;
      this.parameterFutures = parameterFutures;
    }
  }
}
