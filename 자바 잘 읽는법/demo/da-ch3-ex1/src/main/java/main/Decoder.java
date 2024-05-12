package main;

import java.util.List;
import java.util.stream.Collectors;

public class Decoder {

  public Integer decode(List<String> input) {
    try {
      int total = 0;
      for (String s : input) {
        var digits = new StringDigitExtractor(s).extractDigits();
        var sum = digits.stream().collect(Collectors.summingInt(i -> i));
        total += sum;
      }

      return total;
    } catch (Exception e) {
      return -1;
    }
  }
}
