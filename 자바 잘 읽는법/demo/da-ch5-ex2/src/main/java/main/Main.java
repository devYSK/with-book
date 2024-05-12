package main;

import java.util.List;

public class Main {

  public static void main(String[] args) {
    Decoder d = new Decoder();
    var result = d.decode(List.of("ab1c", "a112c", "abcd", "1234"));
    System.out.println(result);
  }
}
