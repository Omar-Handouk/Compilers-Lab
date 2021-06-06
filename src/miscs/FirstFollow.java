package miscs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class FirstFollow {

    private ArrayList<Character> variables;
    private HashSet<Character> alphabet;
    private HashMap<Character, ArrayList<String>> rules;

    private HashMap<Character, ArrayList<Character>> first;
    private HashMap<Character, ArrayList<Character>> follow;

    public FirstFollow(String grammar) {
        variables = new ArrayList<>();

        alphabet = new HashSet<>();
        for (int i = 'a'; i <= 'z'; ++i) {
            if (i == 'e')
                continue;

            alphabet.add((char) i);
        }

        rules = new HashMap<>();
        parse(grammar);

        first = new HashMap<>();
        for (char chr : variables) first.put(chr, new ArrayList<>());

        follow = new HashMap<>();
        for (char chr : variables) follow.put(chr, new ArrayList<>());
    }

    private void parse(String grammar) {
        String[] rules = grammar.split(";");

        for (String rule : rules) {
            String[] split = rule.split(",");

            char leftSide = split[0].charAt(0);
            variables.add(leftSide);

            ArrayList<String> rightSide = new ArrayList<>();

            for (int i = 1; i < split.length; i++) {
                rightSide.add(split[i]);
            }

            this.rules.put(leftSide, rightSide);
        }
    }

    public String first() {
        for (char chr : variables) {
            first(chr);
        }

        StringBuilder out = new StringBuilder();

        boolean first = true;
        for (char chr : variables) {
            if (first) {
                first = false;
            } else {
                out.append(";");
            }

            out.append(chr).append(",");

            ArrayList<Character> rightSide = this.first.get(chr);
            Collections.sort(rightSide);

            for (char c : rightSide) {
                out.append(c);
            }
        }

        return out.toString();
    }

    private ArrayList<Character> first(char chr) {
        if (first.get(chr).size() != 0)
            return first.get(chr);

        ArrayList<String> rightSide = rules.get(chr);

        ArrayList<Character> first = new ArrayList<>();

        for (String str : rightSide) {
            char c;

            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);

                if (alphabet.contains(c) && !first.contains(c)) { // Terminal
                    first.add(c);
                    break;
                } else if (c == 'e' && !first.contains('e')) { // Epsilon
                    first.add('e');
                    break;
                } else { // Non-terminal
                    ArrayList<Character> nonTerminalFirst = null;
                    if (c != chr)
                        nonTerminalFirst = first(c);
                    else
                        continue;

                    boolean hasEpsilon = false;
                    for (int j = 0; j < nonTerminalFirst.size(); j++) {
                        char cc = nonTerminalFirst.get(j);

                        if (i == str.length() - 2 && cc == 'e' && str.charAt(str.length() - 1 ) == chr && !first.contains('e')){ // Special case if last character is chr and the one before me has epsilon, then add epsilon TODO: Need to check this
                            first.add('e');
                            continue;
                        }

                        if (cc == 'e' && i != str.length() - 1) { // If a non-terminal has epsilon and we are not in the last character
                            hasEpsilon = true;
                            continue;
                        }

                        if (!first.contains(cc))
                            first.add(cc);
                    }

                    if (!hasEpsilon) {
                        break;
                    }
                }
            }
        }

        this.first.put(chr, first);

        return first;
    }

    public String follow() {
        first();

        for (char chr : variables) {
            follow(chr);
        }

        StringBuilder out = new StringBuilder();

        boolean first = true;
        for (char chr : variables) {
            if (first) {
                first = false;
            } else {
                out.append(";");
            }

            out.append(chr).append(",");

            ArrayList<Character> rightSide = this.follow.get(chr);
            Collections.sort(rightSide);

            if (rightSide.size() != 0 && rightSide.get(0) == '$') {
                rightSide.remove(0);

                rightSide.add('$');
            }

            for (char c : rightSide) {
                out.append(c);
            }
        }

        return out.toString();
    }

    private ArrayList<Character> follow(char chr) {
        if (follow.get(chr).size() != 0) {
            return follow.get(chr);
        }

        ArrayList<Character> follow = new ArrayList<>();

        if (chr == 'S') {
            follow.add('$');
        }

        for (char variable : variables) {
            ArrayList<String> rules = this.rules.get(variable);

            for (String rule : rules) {
                boolean first = false;

                for (int i = 0; i < rule.length(); ++i) {
                    char c = rule.charAt(i);

                    // Follow(c) - either a character after it or nothing then follow(variable)
                    if (!first) {
                        if (c == chr) {
                            if (i == rule.length() - 1 && variable != c) {
                                ArrayList<Character> followOfVariable = follow(variable);

                                for (char cc : followOfVariable) {
                                    if (!follow.contains(cc)) {
                                        follow.add(cc);
                                    }
                                }
                            } else {
                                first = true;
                            }
                        }
                    } else {
                        if (alphabet.contains(c) && !follow.contains(c)) {
                            follow.add(c);
                        } else {
                            ArrayList<Character> firstOfI = this.first.get(c);

                            boolean epsilon = false;
                            for (int j = 0; j < firstOfI.size(); j++) {
                                if (firstOfI.get(j) == 'e') {
                                    epsilon = true;
                                    continue;
                                }

                                if (!follow.contains(firstOfI.get(j)))
                                    follow.add(firstOfI.get(j));
                            }

                            if (epsilon) {
                                if (i == rule.length() - 1 && variable != c) {
                                    ArrayList<Character> followOfVariable = follow(variable);

                                    for (char cc : followOfVariable) {
                                        if (!follow.contains(cc)) {
                                            follow.add(cc);
                                        }
                                    }
                                } else {
                                    first = true;
                                }
                            } else {
                                first = false;
                            }
                        }
                    }
                }
            }
        }

        this.follow.put(chr, follow);

        return follow;
    }

    public static void main(String[] args) {
        FirstFollow firstFollow = new FirstFollow("S,ACB,CbB,Ba;A,da,BC;B,g,e;C,h,e");

        System.out.println(firstFollow.first());
        System.out.println(firstFollow.follow());
    }
}
