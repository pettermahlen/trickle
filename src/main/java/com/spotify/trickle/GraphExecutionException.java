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

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nullable;

/**
 * TODO: document!
 */
public class GraphExecutionException extends RuntimeException {
  private final List<CallInfo> calls;

  public GraphExecutionException(@Nullable Throwable cause, List<CallInfo> calls) {
    super(cause);
    this.calls = ImmutableList.copyOf(calls);
  }

  public List<CallInfo> getCalls() {
    return calls;
  }
}
