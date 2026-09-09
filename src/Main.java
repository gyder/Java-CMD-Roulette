import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// https://askubuntu.com/questions/558280/changing-colour-of-text-and-background-of-terminal
// about changing the fore- and background color of the terminal through commands
class Main {
  static void main() {
    Board board = new Board();
    List<Play> activePlays = new ArrayList<>();

    int coins = 100;

    intro();

    mainloop:
    while (true) {
      String[] cmd = IO.readln("\n----->").split(" ");
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
            IO.println("#" + (i+1) + ": " + toHumanReadable(play));
          }
        }
        case "rem" -> {
          int index = Integer.parseInt(cmd[1]) - 1;
          IO.println("Removing bet N." + (index + 1));
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
        case "h", "help" -> helpBoard();
        case "b", "board" -> helpBoardClean();
        case "idkfa" -> coins += 10000;
        case "cls", "clear" -> cls();
        case "exit" -> {
          break mainloop;
        }
      }
    }
  }

  static void helpBoard() {
    IO.println(
"""
 01 | 02 |<03> <- SINGLE 3
<04>| 05 | 06  <- SPLIT-V 4
(07)| 08 | 09
 10 |<11>|(12) <- SPLIT-H 11
 13 | 14 | 15
<16>|(17)|(18) <- STREET 16
 19 | 20 | 21
<22>|(23)| 24  <- CORNER 22
(25)|(26)| 27
 28 | 29 | 30
<25>|(26)|(33) <- ....
(25)|(26)|(36)
""");
  }

  static void helpBoardClean() {
    IO.println(
"""
 01 | 02 | 03
 04 | 05 | 06
 07 | 08 | 09
 10 | 11 | 12
 13 | 14 | 15
 16 | 17 | 18
 19 | 20 | 21
 22 | 23 | 24
 25 | 26 | 27
 28 | 29 | 30
 25 | 26 | 33
 25 | 26 | 36
""");
  }

  static void intro() {
    cls();
    IO.println(
"""
    /$$$$$                               /$$$$$$$                      /$$             /$$     /$$
   |__  $$                              | $$__  $$                    | $$            | $$    | $$
      | $$  /$$$$$$  /$$    /$$ /$$$$$$ | $$  \\ $$  /$$$$$$  /$$   /$$| $$  /$$$$$$  /$$$$$$ /$$$$$$    /$$$$$$
      | $$ |____  $$|  $$  /$$/|____  $$| $$$$$$$/ /$$__  $$| $$  | $$| $$ /$$__  $$|_  $$_/|_  $$_/   /$$__  $$
 /$$  | $$  /$$$$$$$ \\  $$/$$/  /$$$$$$$| $$__  $$| $$  \\ $$| $$  | $$| $$| $$$$$$$$  | $$    | $$    | $$$$$$$$
| $$  | $$ /$$__  $$  \\  $$$/  /$$__  $$| $$  \\ $$| $$  | $$| $$  | $$| $$| $$_____/  | $$ /$$| $$ /$$| $$_____/
|  $$$$$$/|  $$$$$$$   \\  $/  |  $$$$$$$| $$  | $$|  $$$$$$/|  $$$$$$/| $$|  $$$$$$$  |  $$$$/|  $$$$/|  $$$$$$$
 \\______/  \\_______/    \\_/    \\_______/|__/  |__/ \\______/  \\______/ |__/ \\_______/   \\___/   \\___/   \\_______/
""");
  }

  static void cls() {
    String[] args =
        System.getProperty("os.name").startsWith("Win")
            ? new String[] {"cmd", "/c", "cls"}
            : new String[] {"clear"};
    try (var process = new ProcessBuilder(args).inheritIO().start()) {
      process.waitFor();
    } catch (Exception ignore) {}
  }

  static int evaluate(Play play, Field ball) {
    var payout = payout(play.guess());
    int value = ball.value();
    int win = payout * play.bet();
    int loss = -play.bet();

    return switch (play.guess()) {
      case Guess.Single single -> single.value() == value ? win : loss;

      case Guess.Split split -> split.lo() == value || split.hi() == value ? win : loss;

      case Guess.Street street -> {
        int lo = street.highest() - 2;
        int hi = street.highest();
        yield value >= lo && value <= hi ? win : loss;
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

  static int payout(Guess guess) {
    return switch (guess) {
      case Guess.Single _ -> 35;
      case Guess.Split _ -> 17;
      case Guess.Street _ -> 8;
      case Guess.Preset.COL1, Guess.Preset.COL2, Guess.Preset.COL3 -> 2;
      case Guess.Preset.RED, Guess.Preset.BLACK -> 1;
    };
  }

  static String toHumanReadable(Play play) {
    var builder = new StringBuilder();
    builder.append("Bet ").append(play.bet());
    switch (play.guess()) {
      case Guess.Single single -> builder.append(" on ").append(single.value());
      case Guess.Split split -> builder.append(" split between ").append(split.lo()).append(" and ").append(split.hi());
      case Guess.Street street -> builder.append(" on a street from ").append(street.highest()-2).append(" to ").append(street.highest());
      case Guess.Preset.RED -> builder.append(" on ").append("red");
      case Guess.Preset.BLACK -> builder.append(" on ").append("black");
      case Guess.Preset.COL1 -> builder.append(" on ").append("the first column");
      case Guess.Preset.COL2 -> builder.append(" on ").append("the second column");
      case Guess.Preset.COL3 -> builder.append(" on ").append("the third column");
    }
    return builder.toString();
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
  // any SINGLE number
  record Single(int value) implements Guess {}
  // ANY pair of 2 Numbers that are adjacent either vertically or horizontally
  record Split(int lo, int hi) implements Guess {
    public Split {
      if (lo <= 0 || lo > 35) throw new IllegalArgumentException();
      if (hi <= 1 || hi > 36) throw new IllegalArgumentException();
      if ((hi - lo) != 3) {
        if ((hi - lo) != 1) throw new IllegalArgumentException();
      }
    }
  }
  // 3 numbers that end on a multiple of 3
  record Street(int highest) implements Guess {}
  // line 1x3
  // corner 2x2
  // block 2x3
  enum Preset implements Guess {
    RED,
    BLACK,
    COL1,
    COL2,
    COL3
  }

  static Guess of(String input) {
    if (input.startsWith("single")) {
      return new Single(Integer.parseInt(input.substring(6).trim()));
    }
    if (input.startsWith("split")) {
      var array = input.substring(5).trim().split(" ");
      return new Split(Integer.parseInt(array[0]), Integer.parseInt(array[1]));
    }
    if (input.startsWith("street")) {
      return new Street(Integer.parseInt(input.substring(6).trim()));
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
