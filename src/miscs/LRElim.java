package miscs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class LRElim {

    public static TreeMap<String, ArrayList<String>> getRules(String cfg) {

        TreeMap<String, ArrayList<String>> rules = new TreeMap<>();

        String[] rulesSplit = cfg.split(";");

        for (String rule : rulesSplit) {
            String[] seq = rule.split(",");

            ArrayList<String> rightSide = new ArrayList<>();

            for (int i = 1; i < seq.length; ++i) {
                rightSide.add(seq[i].trim());
            }

            rules.put(seq[0].trim(), rightSide);
        }

        return rules;
    }

    public static String[] getVariables(String cfg) {

        String[] rulesSplit = cfg.split(";");

        int i = 0;
        String[] variables = new String[rulesSplit.length];

        for (String rule : rulesSplit) {
            String[] seq = rule.split(",");

            variables[i++] = seq[0].trim();
        }

        return variables;
    }

    public static String LRE(String cfg) {
        ArrayList<String> variables = new ArrayList<>(Arrays.asList(getVariables(cfg)));
        TreeMap<String, ArrayList<String>> rules = getRules(cfg);

        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < i; j++) {
                String vari = variables.get(i);
                String varj = variables.get(j);

                ArrayList<String> variRules = rules.get(vari);
                ArrayList<String> varjRules = rules.get(varj);

                for (int k = 0; k < variRules.size(); k++) {
                    String varjDelta = variRules.get(k);

                    if (varjDelta.indexOf(varj) == 0) {
                        String delta = varjDelta.substring(varj.length());

                        for (int l = 0; l < varjRules.size(); l++) {
                            if (l == 0) {
                                variRules.remove(k);
                            }

                            variRules.add(k + l, varjRules.get(l) + delta);
                        }

                        k += varjRules.size() - 1;
                    }
                }

                rules.put(vari, variRules);
            }

            String modifiedCFG = getCFG(variables.get(i), rules.get(variables.get(i)));
            String[] vars = getVariables(modifiedCFG);
            TreeMap<String, ArrayList<String>> rls = getRules(modifiedCFG);

            for (int j = 0; j < vars.length; j++) {
                if (j == 0) {
                    variables.remove(i);
                }
                variables.add(i + j, vars[j]);
                rules.put(vars[j], rls.get(vars[j]));
            }

            i += vars.length - 1;
        }

        StringBuilder out = new StringBuilder();
        boolean first = true;

        for (String variable : variables) {
            if (first) {
                first = false;
            } else {
                out.append("; ");
            }

            out.append(variable);

            for (String segment : rules.get(variable)) {
                out.append(", ").append(segment);
            }
        }

        return out.toString();
    }

    public static String getCFG(String variable, ArrayList<String> rules) {
        ArrayList<String> alphas = new ArrayList<>();
        ArrayList<String> betas = new ArrayList<>();

        for (int i = 0; i < rules.size(); i++) {
            String rule = rules.get(i);

            if (rule.indexOf(variable) == 0) {
                alphas.add(rule.substring(variable.length()));
            } else {
                betas.add(rule);
            }
        }

        StringBuilder out = new StringBuilder();

        if (alphas.size() == 0) {
            out.append(variable);

            for (int i = 0; i < rules.size(); i++) {
                out.append(", ").append(rules.get(i));
            }
        } else {
            out.append(variable);
            for (int i = 0; i < betas.size(); i++) {
                out.append(", ").append(betas.get(i)).append(variable).append("'");
            }

            out.append("; ").append(variable).append("'");
            for (int i = 0; i < alphas.size(); i++) {
                out.append(", ").append(alphas.get(i)).append(variable).append("'");
            }
            out.append(", ").append("e");
        }

        return out.toString();
    }
}
