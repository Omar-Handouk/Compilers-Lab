package LL1;

import java.util.*;

// Predictive parsing table
public class PPT {
    private int variablesCount = 0;
    private final HashMap<Character, Integer> variablesEnum;
    private final ArrayList<Character> variables;

    private int alphabetCount = 0;
    private final HashMap<Character, Integer> alphabetEnum;
    private final ArrayList<Character> alphabet;

    private final HashMap<String, HashSet<Character>> first;
    private final HashMap<Character, HashSet<Character>> follow;

    private final HashMap<Character, ArrayList<String>> rules;

    private final String description;
    private String cfg;
    private String firsts;
    private String follows;

    private final String[][] table;

    private final int dollarEnum;

    public PPT(String description) {
        this.description = description;

        variablesEnum = new HashMap<>();
        variables = new ArrayList<>();
        alphabetEnum = new HashMap<>();
        alphabet = new ArrayList<>();
        first = new HashMap<>();
        follow = new HashMap<>();
        rules = new HashMap<>();

        this.init();

        dollarEnum = alphabetCount;

        table = new String[variables.size()][alphabet.size() + 1];
        this.constructTable();
        this.insertDollar();
    }

    private void init() {
        this.splitDescription();
        this.getVars();
        this.constructAlphabet();
        this.constructRules();
        this.constructFirst();
        this.constructFollow();
    }

    private void splitDescription() {
        String[] split = description.split("#");

        cfg = split[0];
        firsts = split[1];
        follows = split[2];
    }

    private void getVars() {
        String[] split = cfg.split(";");

        for (String s : split) {
            variablesEnum.put(s.charAt(0), variablesCount++);
            variables.add(s.charAt(0));
        }
    }

    private void constructAlphabet() {
        for (int i = 'a'; i <= 'z'; i++) {
            if (i == 'e') { continue; }

            alphabetEnum.put((char) i, alphabetCount++);
            alphabet.add((char) i);
        }
    }

    private void constructRules() {
        String[] rules = cfg.split(";");

        for (String rule : rules) {
            String[] ruleSplit = rule.split(",");

            char var = ruleSplit[0].charAt(0);
            ArrayList<String> rightSide = new ArrayList<>();

            for (int i = 1; i < ruleSplit.length; i++) {
                rightSide.add(ruleSplit[i]);
            }

            this.rules.put(var, rightSide);
        }
    }

    private void constructFirst() {
        String[] rules = firsts.split(";");

        for (String rule : rules) {
            String[] ruleSplit = rule.split(",");

            char var = ruleSplit[0].charAt(0);
            ArrayList<String> rightSide = this.rules.get(var);

            for (int i = 0; i < rightSide.size(); i++) {
                HashSet<Character> set = new HashSet<>();
                for (char c : ruleSplit[i + 1].toCharArray()) set.add(c);

                first.put(rightSide.get(i), set);
            }
        }
    }

    private void constructFollow() {
        String[] rules = follows.split(";");

        for (String rule : rules) {
            String[] ruleSplit = rule.split(",");

            char var = ruleSplit[0].charAt(0);

            for (int i = 1; i < ruleSplit.length; i++) {
                HashSet<Character> set = new HashSet<>();
                for (char c : ruleSplit[i].toCharArray()) set.add(c);

                follow.put(var, set);
            }
        }
    }

    private void constructTable() {
        for (int i = 0; i < variables.size(); i++) {
            char A = variables.get(i);
            ArrayList<String> rightSide = rules.get(A);

            for (int j = 0; j < alphabet.size(); j++) {
                char a = alphabet.get(j);

                for (String alpha : rightSide) {
                    HashSet<Character> theFirst = first.get(alpha);
                    HashSet<Character> theFollow = follow.get(A);

                    if (theFirst.contains(a) || theFirst.contains('e') && theFollow.contains(a)) {
                        table[i][j] = alpha;
                    }
                }
            }
        }
    }

    private void insertDollar() {
        for (int i = 0; i < variables.size(); i++) {
            char A = variables.get(i);
            ArrayList<String> rightSide = rules.get(A);

            char a = '$';

            for (String alpha : rightSide) {
                HashSet<Character> theFirst = first.get(alpha);
                HashSet<Character> theFollow = follow.get(A);

                if (theFirst.contains(a) || theFirst.contains('e') && theFollow.contains(a)) {
                    table[i][dollarEnum] = alpha;
                }
            }
        }
    }

    public String derivation(char A, char a) {
        if (a == '$') {
            return table[variablesEnum.get(A)][25];
        }

        return table[variablesEnum.get(A)][alphabetEnum.get(a)];
    }

    public ArrayList<Character> getVariables() {
        return variables;
    }

    public ArrayList<Character> getAlphabet() {
        return alphabet;
    }
}
