package main;

import java.util.ArrayList;
import java.util.List;

public class StringDigitExtractor {

  private final String input;

  public StringDigitExtractor(String input) {
    this.input = input;
  }

  public List<Integer> extractDigits() {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < input.length(); i++) {
      if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
        list.add(Integer.parseInt(String.valueOf(input.charAt(i))));
      }
    }

    return list;
  }
}
