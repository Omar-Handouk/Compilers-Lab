import FiniteAutomaton.DFA;
import FiniteAutomaton.NFA;
import FiniteAutomaton.RegEx;

import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        /*NFA a = new NFA("1,2;4,5;6,7;8,9#2,3;5,6#0,1;0,4;3,1;3,4;7,8;7,10;9,8;9,10#10");

        System.out.println(a.run("01001"));*/

        RegEx t = new RegEx();

        System.out.println(t.regToNFA("00.1|*").equals("10#8#9#0,1;2,3#4,5#1,2;3,7;5,7;6,0;6,4;7,6;7,9;8,6;8,9"));;
    }
}
