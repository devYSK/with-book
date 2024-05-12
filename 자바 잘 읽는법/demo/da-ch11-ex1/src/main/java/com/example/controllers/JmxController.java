package com.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.services.JmxService;

@RestController()
@RequestMapping("jmx")
public class JmxController {

  private final JmxService jmxService;

  public JmxController(JmxService jmxService) {
    this.jmxService = jmxService;
  }

  @GetMapping("heapDump")
  public void dumpHeap(@RequestParam("file") String outputFile) {
    if (outputFile == null || outputFile.isBlank()) {
      throw new IllegalArgumentException("'file' argument must not be blank.");
    }
    jmxService.dumpHeap(outputFile, true);
  }

}
