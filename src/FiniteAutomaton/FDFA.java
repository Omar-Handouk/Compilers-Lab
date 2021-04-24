package FiniteAutomaton;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class FDFA {
    private int numberOfStates = -1;
    private int[] states;
    private final int[] alphabet = new int[] {0, 1};
    private int[][] transitions;
    private final int startState = 0;
    private int[] acceptStates;
    private String[] actions;

    private String automata;

    public FDFA(String automata) {
        this.automata = automata;
        parse();
    }

    public String run(String input) {
        StringBuilder output = new StringBuilder();

        int R = 0;
        int L = 0;
        int lastReachedState = 0;

        Stack<Integer> stack = new Stack<Integer>();

        outer: while (true) {
            stack.push(0);

            for (L = R; L < input.length(); ++L) {
                int topState = stack.peek();
                int transitionInput = input.charAt(L) - '0';

                stack.push(transitions[topState][transitionInput]);
            }

            lastReachedState = stack.peek();

            if (Arrays.binarySearch(acceptStates, lastReachedState) > -1) {
                output.append(actions[lastReachedState]);
                break;
            }

            while (!stack.isEmpty()) {
                int state = stack.pop();
                L -= 1;

                if (Arrays.binarySearch(acceptStates, state) > -1) {
                    output.append(actions[state]);
                    L += 1;
                    R = L;
                    stack.clear();
                    continue outer;
                }
            }

            output.append(actions[lastReachedState]);
            break;
        }

        return output.toString();
    }

    private void parse() {
        String[] PS = automata.split("#");
        String[] P = PS[0].split(";");

        acceptStates = Pattern
                .compile(",")
                .splitAsStream(PS[1])
                .mapToInt(Integer::parseInt)
                .toArray();

        Arrays.sort(acceptStates); // For binary searching accept states

        numberOfStates = countStates(P);
        states = IntStream.range(0, numberOfStates).toArray();

        transitions = new int[numberOfStates][alphabet.length];
        actions = new String[numberOfStates];

        for (String str : P) {
            String[] tmp = str.split(",");

            int i = Integer.parseInt(tmp[0]);
            int j = Integer.parseInt(tmp[1]);
            int k = Integer.parseInt(tmp[2]);

            transitions[i][0] = j;
            transitions[i][1] = k;

            actions[i] = tmp[3];
        }
    }

    private int countStates(String[] P) {
        int maxStates = Integer.MIN_VALUE;

        for (String str : P) {
            int state = Pattern.compile(",")
                    .splitAsStream(str.substring(0, str.lastIndexOf(",")))
                    .mapToInt(Integer::parseInt)
                    .toArray()[0];

            maxStates = Math.max(maxStates, state + 1);
        }

        return maxStates;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public int[] getStates() {
        return states;
    }

    public int[] getAlphabet() {
        return alphabet;
    }

    public int[][] getTransitions() {
        return transitions;
    }

    public int getStartState() {
        return startState;
    }

    public int[] getAcceptStates() {
        return acceptStates;
    }

    public String[] getActions() {
        return actions;
    }

    public String getAutomata() {
        return automata;
    }

    @Override
    public String toString() {
        return "FDFA{" +
                "numberOfStates=" + numberOfStates +
                ", states=" + Arrays.toString(states) +
                ", alphabet=" + Arrays.toString(alphabet) +
                ", transitions=" + Arrays.deepToString(transitions) +
                ", startState=" + startState +
                ", acceptStates=" + Arrays.toString(acceptStates) +
                ", actions=" + Arrays.toString(actions) +
                ", automata='" + automata + '\'' +
                '}';
    }
}
