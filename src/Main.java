import FiniteAutomaton.FDFA;
import FiniteAutomaton.RegEx;

public class Main {

    public static void main(String[] args) {

        String[][] testCases = {
                {"0,1,0,00;1,1,2,01;2,1,3,10;3,1,0,11#3", "0100;10011;1000011011;011001;1001111010"},
                {"0,1,3,000;1,2,3,001;2,2,4,010;3,1,4,011;4,2,4,100#2,4", "01110110;0101001;1010;101011001;11110"}
        };

        for (String[] testCase : testCases) {
            String automata = testCase[0];
            String[] tests = testCase[1].split(";");

            FDFA fdfa = new FDFA(automata);

            for (String test : tests) {
                System.out.println(fdfa.run(test));
            }
        }
    }
}
