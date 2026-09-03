import java.util.ArrayList;
import java.util.List;

class Main {
  public static void main(String[] args) throws Exception {
    Board board = new Board();
    cls();
    for (int i = 0; i < 10; i++) {
      IO.println(board.randomField());
    }
  }

  public static void cls() throws Exception {
    if (System.getProperty("os.name").startsWith("Win")) {
      new ProcessBuilder("cls").inheritIO().start().waitFor();
      return;
    }
    new ProcessBuilder("clear").inheritIO().start().waitFor();
    return;
  }
}

record Board(List<Field> fields) {
  Board() {
    var list = new ArrayList<Field>();
    for (int i = 0; i <= 36; i++) {
      list.add(new Field(i, Color.of(i)));
    }
    this(list);
  }
  Field randomField() {
    var roll = (int) (Math.random() * fields.size());
    return fields.get(roll);
  }
}

enum Color {
  GREEN,
  RED,
  BLACK;
  static Color of(int value) {
    if (value < 0 || value > 36) throw new IllegalArgumentException(); 
    if (value == 0) return GREEN;
    if (value <= 10) return value % 2 == 0 ? BLACK : RED;
    if (value <= 18) return value % 2 == 0 ? RED : BLACK;
    if (value <= 28) return value % 2 == 0 ? BLACK : RED;
    return value % 2 == 0 ? RED : BLACK;
  }
}

record Field(int value, Color color) {}