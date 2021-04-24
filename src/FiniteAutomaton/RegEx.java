package FiniteAutomaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.regex.Pattern;

public class RegEx {

    private int stateNumber = -1;

    private String union(String a, String b) {
        int startState = ++stateNumber;
        int endState = ++stateNumber;

        String[] splitA = a.split("#");
        String[] splitB = b.split("#");

        int[] startA = Pattern.compile(",").splitAsStream(splitA[0]).mapToInt(Integer::parseInt).toArray();
        int[] acceptA = Pattern.compile(",").splitAsStream(splitA[1]).mapToInt(Integer::parseInt).toArray();
        int[] startB = Pattern.compile(",").splitAsStream(splitB[0]).mapToInt(Integer::parseInt).toArray();
        int[] acceptB = Pattern.compile(",").splitAsStream(splitB[1]).mapToInt(Integer::parseInt).toArray();

        ArrayList<int[]> start = new ArrayList<>();
        start.add(startA);
        start.add(startB);

        ArrayList<int[]> accept = new ArrayList<>();
        accept.add(acceptA);
        accept.add(acceptB);

        StringBuilder startTransitions = new StringBuilder();
        StringBuilder endTransitions = new StringBuilder();
        StringBuilder result = new StringBuilder();

        boolean first = true;

        for (int[] arr : start) {
            if (first) {
                first = false;
            } else {
                startTransitions.append(";");
            }

            for (int elem : arr) {
                startTransitions.append(startState).append(",").append(2).append(",").append(elem);
            }
        }

        first = true;

        for (int[] arr : accept) {
            if (first) {
                first = false;
            } else {
                endTransitions.append(";");
            }

            for (int elem : arr) {
                endTransitions.append(elem).append(",").append(2).append(",").append(endState);
            }
        }

        result
                .append(startState)
                .append("#")
                .append(endState)
                .append("#")
                .append(startTransitions)
                .append(";")
                .append(splitA[2])
                .append(";")
                .append(splitB[2])
                .append(";")
                .append(endTransitions);

        return result.toString();
    }

    private String concat(String a, String b) {
        String[] splitA = a.split("#");
        String[] splitB = b.split("#");

        int[] acceptA = Pattern.compile(",").splitAsStream(splitA[1]).mapToInt(Integer::parseInt).toArray();
        int[] startB = Pattern.compile(",").splitAsStream(splitB[0]).mapToInt(Integer::parseInt).toArray();

        StringBuilder epsilonTransition = new StringBuilder();
        StringBuilder result = new StringBuilder();

        boolean first = true;

        for (int i : acceptA) {
            for (int j : startB) {
                if (first) {
                    first = false;
                } else {
                    epsilonTransition.append(";");
                }

                epsilonTransition.append(i).append(",").append(2).append(",").append(j);
            }
        }

        result
                .append(splitA[0])
                .append("#")
                .append(splitB[1])
                .append("#")
                .append(splitA[2])
                .append(";")
                .append(epsilonTransition)
                .append(";")
                .append(splitB[2]);

        return result.toString();
    }

    private String star(String a) {
        int startState = ++stateNumber;
        int endState = ++stateNumber;

        String[] split = a.split("#");
        int[] start = Pattern.compile(",").splitAsStream(split[0]).mapToInt(Integer::parseInt).toArray();
        int[] accept = Pattern.compile(",").splitAsStream(split[1]).mapToInt(Integer::parseInt).toArray();

        StringBuilder epsilonTransitions = new StringBuilder();
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (int i : start) {
            if (first) {
                first = false;
            } else {
                epsilonTransitions.append(";");
            }

            epsilonTransitions.append(startState).append(",").append(2).append(",").append(i);
        }

        for (int i : accept) {
            epsilonTransitions.append(";");

            epsilonTransitions.append(i).append(",").append(2).append(",").append(endState);
        }

        for (int i : accept) {
            for (int j : start) {
                epsilonTransitions.append(";");

                epsilonTransitions.append(i).append(",").append(2).append(",").append(j);
            }
        }

        epsilonTransitions.append(";").append(startState).append(",").append(2).append(",").append(endState);

        result
                .append(startState)
                .append("#")
                .append(endState)
                .append("#")
                .append(epsilonTransitions)
                .append(";")
                .append(split[2]);

        return result.toString();
    }

    public String regToNFA(String regex) {
        Stack<String> NFAs = new Stack<String>();

        StringBuilder NFA;

        int startState = 0;
        int endState = 0;

        for (char c : regex.toCharArray()) {

            if (c == '0' || c == '1' || c == 'e') {
                startState = ++stateNumber;
                endState = ++stateNumber;

                NFA = new StringBuilder();

                NFA
                        .append(startState)
                        .append("#")
                        .append(endState)
                        .append("#")
                        .append(startState)
                        .append(",")
                        .append((c == '0' ? 0 : (c == '1' ? 1 : 2))) // 2 - epsilon
                        .append(",")
                        .append(endState);

                NFAs.push(NFA.toString());

            } else {
                String b = NFAs.pop();
                String a = c == '*' ? null : NFAs.pop();
                String result = null;

                switch (c) {
                    case '|':
                        result = union(a, b);
                        break;
                    case '.':
                        result = concat(a, b);
                        break;
                    default:
                        result = star(b);
                        break;
                }

                NFAs.push(result);
            }
        }

        String result = NFAs.pop();
        String[] split = result.split("#");

        int numberOfState = stateNumber + 1;
        String start = split[0];
        String accept = split[1];
        String[] trans = split[2].split(";");

        ArrayList<Integer>[][] transitions = new ArrayList[numberOfState][3];
        for (int i = 0; i < numberOfState; ++i) {
            for (int j = 0; j < 3; ++j) {
                transitions[i][j] = new ArrayList<>();
            }
        }

        for (String transition : trans) {
            int[] tmp = Pattern.compile(",").splitAsStream(transition).mapToInt(Integer::parseInt).toArray();

            transitions[tmp[0]][tmp[1]].add(tmp[2]);

            Collections.sort(transitions[tmp[0]][tmp[1]]);
        }

        StringBuilder zeros = new StringBuilder();
        StringBuilder ones = new StringBuilder();
        StringBuilder eps = new StringBuilder();

        boolean first = true;

        for (int i = 0; i < numberOfState; ++i) {
            for (int j = 0; j < transitions[i][0].size(); ++j) {
                if (first) {
                    first = false;
                } else {
                    zeros.append(";");
                }

                zeros.append(i).append(",").append(transitions[i][0].get(j));
            }
        }

        first = true;

        for (int i = 0; i < numberOfState; ++i) {
            for (int j = 0; j < transitions[i][1].size(); ++j) {
                if (first) {
                    first = false;
                } else {
                    ones.append(";");
                }

                ones.append(i).append(",").append(transitions[i][1].get(j));
            }
        }

        first = true;

        for (int i = 0; i < numberOfState; ++i) {
            for (int j = 0; j < transitions[i][2].size(); ++j) {
                if (first) {
                    first = false;
                } else {
                    eps.append(";");
                }

                eps.append(i).append(",").append(transitions[i][2].get(j));
            }
        }

        StringBuilder ans = new StringBuilder();

        ans
                .append(numberOfState)
                .append("#")
                .append(start)
                .append("#")
                .append(accept)
                .append("#")
                .append(zeros)
                .append("#")
                .append(ones)
                .append("#")
                .append(eps);

        return ans.toString();
    }

    public static void main(String[] args) {
        RegEx t = new RegEx();

        System.out.println(t.regToNFA("00.1|*").equals("10#8#9#0,1;2,3#4,5#1,2;3,7;5,7;6,0;6,4;7,6;7,9;8,6;8,9"));;
    }
}
