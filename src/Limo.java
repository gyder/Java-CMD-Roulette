public class Limo {
    void main() {
        double coins = 20.00;
        while (true) {
            cls();
            // Phases:
            // Info
            // Make X Limo
            // Run (Customers)
            // Buy Upgrades

            int customers = (int) (Math.random() * 10);
            int limosPrepared = readint("Prepare >");

            coins -= limosPrepared;

            double price = readdouble("Price per >");

            println("Prepared " + limosPrepared + " limos.");
            println("Todays customers: " + customers);

            coins += customers * price;

            println("You earned " + (customers * price) + " coins today.");
            readln("Press ENTER to continue >");
        }
    }

    // so i dont need to write IO.- everytime
    public static String readln(String prompt) {
        return IO.readln(prompt);
    }

    public static void println(String in) {
        IO.println(in);
    }

    public static void print(String in) {
        IO.print(in);
    }

    public static int readint(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readln(prompt));
            } catch (Exception e) {
                println("Not a valid input, use numbers: {1, 2, 3, ...}");
            }
        }
    }

    public static double readdouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readln(prompt));
            } catch (Exception e) {
                println("Not a valid input, use: {0.05, 1.58, 2, ...");
            }
        }
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
}
