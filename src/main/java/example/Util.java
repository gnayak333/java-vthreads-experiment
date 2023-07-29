package example;

public interface Util {
  static void log(String msg) {
    System.out.println(System.currentTimeMillis() + "; " + msg + "; " + Thread.currentThread());
  }
  static void print(String msg) {
    System.out.println(msg);
  }
}
