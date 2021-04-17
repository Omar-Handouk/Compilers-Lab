package FiniteAutomaton;

import java.util.*;

public class NFA {
    private int numberOfStates;
    private int[] states;
    private final int[] alphabet = {0, 1};
    private ArrayList<Integer>[][] transitions;
    private TreeSet<Integer>[] closures;
    private final int start = 0;
    private int[] accept;

    private String input;
    private String equivalentDFA;
    private DFA dfa;

    public NFA(String input) {
        this.input = input;

        parse();
        equivalentDFA = solve();
        dfa = new DFA(equivalentDFA);
    }

    private void parse() {
        String[] ZOEF = input.split("#");
        String[] F = ZOEF[3].split(",");

        numberOfStates = 0;
        for (int i = 0; i < 3; ++i) {
            String[] tmp = ZOEF[i].split(";");

            for (String s : tmp) {
                String[] tmp2 = s.split(",");

                for (String ss : tmp2) {
                    numberOfStates = Math.max(numberOfStates, Integer.parseInt(ss) + 1);
                }
            }
        }

        states = new int[numberOfStates];
        for (int i = 0; i < numberOfStates; ++i) states[i] = i;

        transitions = new ArrayList[numberOfStates][3];
        for (int i = 0; i < 3; ++i) {
            String[] tmp = ZOEF[i].split(";");

            for (String s : tmp) {
                String[] tmp2 = s.split(",");

                int a = Integer.parseInt(tmp2[0]);
                int b = Integer.parseInt(tmp2[1]);

                if (transitions[a][i] == null) {
                    transitions[a][i] = new ArrayList<Integer>();
                }

                transitions[a][i].add(b);
            }
        }

        closures = new TreeSet[numberOfStates];
        for (int i = 0; i < numberOfStates; ++i) {
            closures[i] = getClosure(i);
        }

        accept = new int[F.length];
        for (int i = 0; i < accept.length; ++i) {
            accept[i] = Integer.parseInt(F[i]);
        }
    }

    private TreeSet<Integer> getClosure(int node) {
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(node);

        TreeSet<Integer> reachable = new TreeSet<Integer>();

        while (!queue.isEmpty()) {
            int n = queue.remove();

            if (!reachable.contains(n)) {
                reachable.add(n);

                if (transitions[n][2] != null) {
                    queue.addAll(transitions[n][2]);
                }
            }
        }

        return reachable;
    }

    public TreeSet<Integer> getNewState(TreeSet<Integer> start) {
        TreeSet<Integer> state = new TreeSet<Integer>();

        for (int i : start) {
            state.addAll(closures[i]);
        }

        return state;
    }

    public String solve() {
        Queue<TreeSet<Integer>> queue = new LinkedList<TreeSet<Integer>>();
        queue.add(closures[0]);

        HashMap<TreeSet<Integer>, Integer> found = new HashMap<TreeSet<Integer>, Integer>();

        int i = 0;
        found.put(closures[0], i++);

        StringBuilder graph = new StringBuilder();
        TreeSet<Integer> acceptStates = new TreeSet<Integer>();

        boolean first = true;

        while (!queue.isEmpty()) {

            if (first) {
                first = false;
            } else {
                graph.append(";");
            }

           TreeSet<Integer> newState = queue.remove();

           int index = found.get(newState);

           if (isAccept(newState)) {
               acceptStates.add(index);
           }

           graph.append(index).append(",");

           TreeSet<Integer> zeroTransition = getNewState(newState, 0);
           TreeSet<Integer> zeroTransitionClosure = getNewState(zeroTransition);
           TreeSet<Integer> oneTransition = getNewState(newState, 1);
           TreeSet<Integer> oneTransitionClosure = getNewState(oneTransition);

           if (!found.containsKey(zeroTransitionClosure)) {
               graph.append(i).append(",");
               found.put(zeroTransitionClosure, i++);
               queue.add(zeroTransitionClosure);
           } else {
               graph.append(found.get(zeroTransitionClosure)).append(",");
           }

           if (!found.containsKey(oneTransitionClosure)) {
               graph.append(i).append(",");
               found.put(oneTransitionClosure, i++);
               queue.add(oneTransitionClosure);
           } else {
               graph.append(found.get(oneTransitionClosure));
           }
        }

        graph.append("#");
        first = true;
        for (int j : acceptStates) {
            if (first) {
                first = false;
            } else {
                graph.append(",");
            }

            graph.append(j);
        }

        return graph.toString();
    }

    private boolean isAccept(TreeSet<Integer> set) {

        for (int i : set) {
            for (int j = 0; j < accept.length; ++j) {
                if (i == accept[j]) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean run(String input) {
        return dfa.run(input);
    }

    public TreeSet<Integer> getNewState(TreeSet<Integer> start, int alphabetMember) {
        TreeSet<Integer> state = new TreeSet<Integer>();

        for (int i : start) {
            if (transitions[i][alphabetMember] != null) {
                state.addAll(transitions[i][alphabetMember]);
            }
        }

        return state;
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

    public ArrayList<Integer>[][] getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList<Integer>[][] transitions) {
        this.transitions = transitions;
    }

    public TreeSet<Integer>[] getClosures() {
        return closures;
    }

    public void setClosures(TreeSet<Integer>[] closures) {
        this.closures = closures;
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
