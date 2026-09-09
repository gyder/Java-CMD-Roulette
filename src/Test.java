import java.lang.Math;

void main(String[] args) {
    // hier wird eine "final" variable definiert, diese kann nachher nicht verändert werden
    final double pi = 3.141592653;
    // Math.pow() nimmt in diesem fall 2 Parameter, zuerst die Basis, dann den Exponenten
    double piSquared = Math.pow(pi, 2);
    IO.println(piSquared);

    IO.println("Hello World");
    IO.println(wurzel(9.00));
}

double wurzel(double input) {
    return Math.sqrt(input);
}
