import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Main {
  public static void main(String[] args) throws Exception {
    Board board = new Board();
    List<Play> activePlays = new ArrayList<>();
    int coins = 100;

    intro();

    mainloop:while (true) {
      String[] cmd = IO.readln("----->").split(" ");
      switch (cmd[0]) {
        case "add" -> {
          var guess = IO.readln("guess>");
          var bet = IO.readln("bet-->");
          var play = new Play(Guess.of(guess), Integer.parseInt(bet));
          activePlays.add(play);
        }
        case "in", "ls", "st", "info", "status", "list" -> {
          IO.println("coins = " + coins);
          for (int i = 0; i < activePlays.size(); i++) {
            var play = activePlays.get(i);
            IO.println("NR." + i + " -> guess = " + play.guess() + ", bet = " + play.bet());
          }
        }
        case "rem" -> {
          int index = Integer.parseInt(cmd[1]);
          activePlays.remove(index);
        }
        case "roll", "r" -> {
          int i = 0;
          int total = 0;
          var ball = board.randomField();
          IO.println("The wheel rolled -> " + ball.value());
          for (Play play : activePlays) {
            i++;
            var profit = evaluate(play, ball);
            IO.println("NR." + i + " -> " + profit);
            total += profit;
          }
          coins += total;
          IO.println("coins>" + coins);
        }
        case "idkfa" -> coins += 10000;
        case "cls", "clear" -> cls();
        case "exit" -> {break mainloop;}
      }
    }
  }

  static void intro() {
    IO.println("    /$$$$$                               /$$$$$$$                      /$$             /$$     /$$");
    IO.println("   |__  $$                              | $$__  $$                    | $$            | $$    | $$");
    IO.println("      | $$  /$$$$$$  /$$    /$$ /$$$$$$ | $$  \\ $$  /$$$$$$  /$$   /$$| $$  /$$$$$$  /$$$$$$ /$$$$$$    /$$$$$$");
    IO.println("      | $$ |____  $$|  $$  /$$/|____  $$| $$$$$$$/ /$$__  $$| $$  | $$| $$ /$$__  $$|_  $$_/|_  $$_/   /$$__  $$");
    IO.println(" /$$  | $$  /$$$$$$$ \\  $$/$$/  /$$$$$$$| $$__  $$| $$  \\ $$| $$  | $$| $$| $$$$$$$$  | $$    | $$    | $$$$$$$$");
    IO.println("| $$  | $$ /$$__  $$  \\  $$$/  /$$__  $$| $$  \\ $$| $$  | $$| $$  | $$| $$| $$_____/  | $$ /$$| $$ /$$| $$_____/");
    IO.println("|  $$$$$$/|  $$$$$$$   \\  $/  |  $$$$$$$| $$  | $$|  $$$$$$/|  $$$$$$/| $$|  $$$$$$$  |  $$$$/|  $$$$/|  $$$$$$$");
    IO.println(" \\______/  \\_______/    \\_/    \\_______/|__/  |__/ \\______/  \\______/ |__/ \\_______/   \\___/   \\___/   \\_______/");
  }

  public static void cls() throws Exception {
    if (System.getProperty("os.name").startsWith("Win")) {
      new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      return;
    }
    new ProcessBuilder("clear").inheritIO().start().waitFor();
  }

  static int evaluate(Play play, Field ball) {
    var payout = play.guess().payout();
    int value = ball.value();
    int win = payout * play.bet();
    int loss = -play.bet();

    return switch (play.guess()) {
      case Guess.Single single -> single.value() == value ? win : loss;

      case Guess.Split split -> {
        int lo = split.lower();
        int hi = lo + 1;
        yield lo == value || hi == value ? win : loss;
      }

      case Guess.Preset preset ->
          switch (preset) {
            case RED -> ball.color() == Color.RED ? win : loss;
            case BLACK -> ball.color() == Color.BLACK ? win : loss;
            case COL1 -> value % 3 == 1 ? win : loss;
            case COL2 -> value % 3 == 2 ? win : loss;
            case COL3 -> value % 3 == 0 ? win : loss;
          };
    };
  }
}

record Play(Guess guess, int bet) {}

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

record Field(int value, Color color) {}

sealed interface Guess {
  int payout();

  record Single(int value) implements Guess {
    @Override
    public int payout() {
      return 35;
    }
  }

  record Split(int lower) implements Guess {
    @Override
    public int payout() {
      return 17;
    }
  }

  // line 1x3
  // corner 2x2
  // block 2x3
  enum Preset implements Guess {
    RED(1),
    BLACK(1),
    COL1(2),
    COL2(2),
    COL3(2);

    private final int payout;

    Preset(int payout) {
      this.payout = payout;
    }

    @Override
    public int payout() {
      return payout;
    }
  }

  static Guess of(String input) {
    if (input.startsWith("single")) {
      return new Single(Integer.parseInt(input.substring(6).trim()));
    }
    if (input.startsWith("split")) {
      return new Split(Integer.parseInt(input.substring(5).trim()));
    }
    return Preset.valueOf(input.toUpperCase(Locale.ROOT));
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

