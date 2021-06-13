package LL1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

public class LL1 {

    private String description;
    private String input;

    private PPT ppt;

    private HashSet<Character> variables;
    private HashSet<Character> alphabet;

    private Stack<Character> stack;
    private int pointer = 0;

    public LL1() {
        stack = new Stack<>();
    }

    public String parse(String description, String input) {
        this.description = description;
        this.input = input + '$';

        ppt = new PPT(description);

        variables = new HashSet<>(ppt.getVariables());
        alphabet = new HashSet<>(ppt.getAlphabet());

        return parse(true);
    }

    private String parse(boolean debug) {
        stack.push('$');
        stack.push('S');

        if (debug) System.out.println(Arrays.toString(stack.toArray()));

        StringBuilder out = new StringBuilder();
        out.append('S');

        ArrayList<Character> word = new ArrayList<>();
        HashSet<String> exists = new HashSet<>();

        while (true) {
            char c = stack.peek();

            if (pointer >= input.length() && c != '$') {
                out.append(",ERROR");
                break;
            }

            if (variables.contains(c)) {
                stack.pop();

                String replacement = ppt.derivation(c, input.charAt(pointer));

                if (replacement == null) {
                    out.append(",ERROR");
                    break;
                } else if (!replacement.equals("e")) {
                    for (int i = replacement.length() - 1; 0 <= i ; i--) {
                        stack.push(replacement.charAt(i));
                    }
                }
            } else if (alphabet.contains(c)) {
                stack.pop();
                pointer++;

                word.add(c);
            } else if (c == '$') {
                break;
            }

            StringBuilder sss = new StringBuilder();

            for (char cc : word) {
                sss.append(cc);
            }

            Stack<Character> cl = (Stack<Character>) stack.clone();
            while (cl.peek() != '$') {
                sss.append(cl.pop());
            }

            if (!exists.contains(sss.toString())) {
                exists.add(sss.toString());
                out.append(",").append(sss);
            }

            if (debug) System.out.println(Arrays.toString(stack.toArray()));
        }

        return out.toString();
    }

    public static void main(String[] args) {
        LL1 ll1 = new LL1();
        System.out.println(ll1.parse("S,iST,e;T,cS,a#S,i,e;T,c,a#S,ca$;T,ca$", "iiac"));

       /* System.out.println(ll1.parse("S,ipD,oSmDc,e;D,VmS,LxS;V,n,e;L,oSc,e#S,i,o,e;D,mn,ox;V,n,e;L,o,e#S,cm$;D,cm$;V,m;L,x", "oo")
                .equals("S,oSmDc,ooSmDcmDc,oomDcmDc,ERROR"));*/
    }
}
