package FiniteAutomaton;

import java.util.Arrays;

public class DFA {
    private int numberOfStates;
    private int[] states;
    private final int[] alphabet = {0, 1};
    private int[][] transitions;
    private final int start = 0;
    private int[] accept;

    private String input;

    public DFA(String input) {
        this.input = input;

        parse();
    }

    private void parse() {
        numberOfStates = 0;

        String[] PS = input.split("#");
        String[] P = PS[0].split(";");
        String[] S = PS[1].split(",");

        for (String s : P) {
            String[] tmp = s.split(",");
            for (String ss : tmp) {
                numberOfStates = Math.max(numberOfStates, Integer.parseInt(ss) + 1);
            }
        }

        states = new int[numberOfStates];
        for (int i = 0; i < numberOfStates; ++i) states[i] = i;

        transitions = new int[numberOfStates][alphabet.length];
        for (String s : P) {
            String[] tmp = s.split(",");

            int i = Integer.parseInt(tmp[0]);
            int j = Integer.parseInt(tmp[1]);
            int k = Integer.parseInt(tmp[2]);

            transitions[i][0] = j;
            transitions[i][1] = k;
        }

        accept = new int[S.length];
        for (int i = 0; i < accept.length; ++i) {
            accept[i] = Integer.parseInt(S[i]);
        }
    }

    public boolean run(String input) {
        int currentState = start;
        int[] splitInput = new int[input.toCharArray().length];

        for (int i = 0; i < splitInput.length; ++i) {
            splitInput[i] = input.charAt(i) - '0';
        }

        for (int i : splitInput) {
            currentState = transitions[currentState][i];
        }

        return Arrays.binarySearch(accept, currentState) >= 0;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void setNumberOfStates(int numberOfStates) {
        this.numberOfStates = numberOfStates;
    }

    public int[] getStates() {
        return states;
    }

    public void setStates(int[] states) {
        this.states = states;
    }

    public int[] getAlphabet() {
        return alphabet;
    }

    public int[][] getTransitions() {
        return transitions;
    }

    public void setTransitions(int[][] transitions) {
        this.transitions = transitions;
    }

    public int getStart() {
        return start;
    }

    public int[] getAccept() {
        return accept;
    }

    public void setAccept(int[] accept) {
        this.accept = accept;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
