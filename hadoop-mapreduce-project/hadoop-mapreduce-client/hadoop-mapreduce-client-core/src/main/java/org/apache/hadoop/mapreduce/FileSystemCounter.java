/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.mapreduce;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public enum FileSystemCounter {
  BYTES_READ,
  TIME_READ,
  BLOCK_READ,
  BLOCK_READ1B, BLOCK_READ2B, BLOCK_READ4B, BLOCK_READ8B, BLOCK_READ16B, BLOCK_READ32B, BLOCK_READ64B, BLOCK_READ128B, BLOCK_READ256B, BLOCK_READ512B,
  BLOCK_READ1K, BLOCK_READ2K, BLOCK_READ4K, BLOCK_READ8K, BLOCK_READ16K, BLOCK_READ32K, BLOCK_READ64K, BLOCK_READ128K, BLOCK_READ256K, BLOCK_READ512K,
  BLOCK_READ1M, BLOCK_READ2M, BLOCK_READ4M, BLOCK_READ8M, BLOCK_READ16M, BLOCK_READ32M, BLOCK_READ64M, BLOCK_READ128M, BLOCK_READ256M, BLOCK_READ512M,
  BLOCK_READ1G, BLOCK_READ_BIG,
  BYTES_WRITTEN,
  TIME_WRITTEN,
  BLOCK_WRITTEN,
  BLOCK_WRITTEN1B, BLOCK_WRITTEN2B, BLOCK_WRITTEN4B, BLOCK_WRITTEN8B, BLOCK_WRITTEN16B, BLOCK_WRITTEN32B, BLOCK_WRITTEN64B, BLOCK_WRITTEN128B, BLOCK_WRITTEN256B, BLOCK_WRITTEN512B,
  BLOCK_WRITTEN1K, BLOCK_WRITTEN2K, BLOCK_WRITTEN4K, BLOCK_WRITTEN8K, BLOCK_WRITTEN16K, BLOCK_WRITTEN32K, BLOCK_WRITTEN64K, BLOCK_WRITTEN128K, BLOCK_WRITTEN256K, BLOCK_WRITTEN512K,
  BLOCK_WRITTEN1M, BLOCK_WRITTEN2M, BLOCK_WRITTEN4M, BLOCK_WRITTEN8M, BLOCK_WRITTEN16M, BLOCK_WRITTEN32M, BLOCK_WRITTEN64M, BLOCK_WRITTEN128M, BLOCK_WRITTEN256M, BLOCK_WRITTEN512M,
  BLOCK_WRITTEN1G, BLOCK_WRITTEN_BIG,
  READ_OPS,
  LARGE_READ_OPS,
  WRITE_OPS,
}
