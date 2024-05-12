package com.example.services;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.sun.management.HotSpotDiagnosticMXBean;

@Service
public class JmxService {

  private static final Logger LOGGER = LogManager.getLogger(JmxService.class);
  private HotSpotDiagnosticMXBean mbean = null;

  public void dumpHeap(String outputFile, boolean live) {
    if (mbean == null) {
      try {
        mbean = ManagementFactory.newPlatformMXBeanProxy(
            ManagementFactory.getPlatformMBeanServer(),
            "com.sun.management:type=HotSpotDiagnostic",
            HotSpotDiagnosticMXBean.class);
      } catch (IOException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
    try {
      String myFile = outputFile;
      if (!myFile.endsWith(".hprof")) {
        myFile = outputFile + ".hprof";
      }
      mbean.dumpHeap(myFile, live);
      if (!Objects.equals(outputFile, myFile)) {
        File generatedFile = new File(myFile);
        File newFile = new File(outputFile);
        generatedFile.renameTo(newFile);
      }
    }
    catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

}
