/**
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

package org.apache.hadoop.examples.terasort;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.examples.terasort.TeraInputFormat.TeraRecordReader;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.hadoop.mapred.InvalidJobConfException;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.security.TokenCache;

/**
 * An output format that writes the key and value appended together.
 */
public class TeraOutputFormat extends FileOutputFormat<Text,Text> {
  static final String FINAL_SYNC_ATTRIBUTE = "mapreduce.terasort.final.sync";
  private OutputCommitter committer = null;
  
  private static final Map<String, TeraRecordWriter> writers = new HashMap<String, TeraRecordWriter>();

  /**
   * Set the requirement for a final sync before the stream is closed.
   */
  static void setFinalSync(JobContext job, boolean newValue) {
    job.getConfiguration().setBoolean(FINAL_SYNC_ATTRIBUTE, newValue);
  }

  /**
   * Does the user want a final sync at close?
   */
  public static boolean getFinalSync(JobContext job) {
    return job.getConfiguration().getBoolean(FINAL_SYNC_ATTRIBUTE, false);
  }

  static class TeraRecordWriter extends RecordWriter<Text,Text> {
    private boolean finalSync = false;
    private FSDataOutputStream out;
    
    public long totalWritten = 0;
    public long totalRecords = 0;
    public long totalMillis = 0, init = 0, close = 0;

    public TeraRecordWriter(FSDataOutputStream out,
                            JobContext job) {
    	long start = System.currentTimeMillis();
      finalSync = getFinalSync(job);
      this.out = out;
      init += System.currentTimeMillis() - start;
    }

    public synchronized void write(Text key, 
                                   Text value) throws IOException {
    	long start = System.currentTimeMillis();
      out.write(key.getBytes(), 0, key.getLength());
      out.write(value.getBytes(), 0, value.getLength());
      totalMillis += System.currentTimeMillis() - start;
      ++totalRecords;
      totalWritten += key.getLength() + value.getLength();
    }
    
    public void close(TaskAttemptContext context) throws IOException {
    	long start = System.currentTimeMillis();
      if (finalSync) {
        out.sync();
      }
      out.close();
      close += System.currentTimeMillis();
    }
  }

  @Override
  public void checkOutputSpecs(JobContext job
                              ) throws InvalidJobConfException, IOException {
    // Ensure that the output directory is set
    Path outDir = getOutputPath(job);
    if (outDir == null) {
      throw new InvalidJobConfException("Output directory not set in JobConf.");
    }

    final Configuration jobConf = job.getConfiguration();

    // get delegation token for outDir's file system
    TokenCache.obtainTokensForNamenodes(job.getCredentials(),
        new Path[] { outDir }, jobConf);

    final FileSystem fs = outDir.getFileSystem(jobConf);

    if (fs.exists(outDir)) {
      // existing output dir is considered empty iff its only content is the
      // partition file.
      //
      final FileStatus[] outDirKids = fs.listStatus(outDir);
      boolean empty = false;
      if (outDirKids != null && outDirKids.length == 1) {
        final FileStatus st = outDirKids[0];
        final String fname = st.getPath().getName();
        empty =
          !st.isDirectory() && TeraInputFormat.PARTITION_FILENAME.equals(fname);
      }
      if (TeraSort.getUseSimplePartitioner(job) || !empty) {
        throw new FileAlreadyExistsException("Output directory " + outDir
            + " already exists");
      }
    }
  }
  
  public static void printStats() {
	  System.out.println("TeraOutputFormat Stats:");
	  for (Entry<String, TeraRecordWriter> e : writers.entrySet()) {
		  System.out.println("Job/Task/Split: " + e.getKey());
		  System.out.println("\tTotal Written: " + e.getValue().totalWritten);
		  System.out.println("\tTotal Records: " + e.getValue().totalRecords);
		  System.out.println("\tInit Millis: " + e.getValue().init);
		  System.out.println("\tTotal Millis: " + e.getValue().totalMillis);
		  System.out.println("\tClose Millis: " + e.getValue().close);
	  }
	  System.out.println();
  }

  public RecordWriter<Text,Text> getRecordWriter(TaskAttemptContext job
                                                 ) throws IOException {
    Path file = getDefaultWorkFile(job, "");
    FileSystem fs = file.getFileSystem(job.getConfiguration());
     FSDataOutputStream fileOut = fs.create(file);
     String id = "job" + job.getJobID() + "-task" + job.getTaskAttemptID() + "-no" + writers.size();
     writers.put(id, new TeraRecordWriter(fileOut, job));
    return writers.get(id);
  }
  
  public OutputCommitter getOutputCommitter(TaskAttemptContext context) 
      throws IOException {
    if (committer == null) {
      Path output = getOutputPath(context);
      committer = new FileOutputCommitter(output, context);
    }
    return committer;
  }

}
