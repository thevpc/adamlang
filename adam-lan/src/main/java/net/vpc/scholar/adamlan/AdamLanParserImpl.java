/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.nodes.*;
import net.vpc.scholar.adamlan.utils.AdamLanUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import net.vpc.scholar.adamlan.commands.en.EnglishLanSupport;
import net.vpc.scholar.adamlan.commands.fr.FrenchLanSupport;

/**
 * @author vpc
 */
public class AdamLanParserImpl extends DefaultExpressionManager implements AdamLanParser {

    private AdamLanExecEnv env;
    private Locale locale = Locale.ENGLISH;
    private ResourceBundle bundle;
    private List<CommandResolver> resolvers = new ArrayList<CommandResolver>();
    private List<CommandResolver> cache_resolvers = null;
    private LangSupport[] all_languages = new LangSupport[]{EnglishLanSupport.INSTANCE, FrenchLanSupport.INSTANCE};
    private List<MyRunnable> allThreads = new ArrayList<>();

    public AdamLanParserImpl() {
        this(new AdamLanExecEnvBase());
    }

    public AdamLanParserImpl(AdamLanExecEnv env) {
        this.env = env;
        this.declareBinaryOperators("+", "-", "*", "/", "&&", "||", "<", "<=", ">", ">=", "!=", "=", ",");
        this.declareBinaryOperator("", JeepUtils.PRECEDENCE_0);

//        definition.declareOperatorAlias("and", "&&", Boolean.TYPE, Boolean.TYPE);
//        definition.declareOperatorAlias("&", "&&", Boolean.TYPE, Boolean.TYPE);
//
//        definition.declareOperatorAlias(",", "||", Boolean.TYPE, Boolean.TYPE);
//        definition.declareOperatorAlias("or", "||", Boolean.TYPE, Boolean.TYPE);
//        definition.declareOperatorAlias("|", "||", Boolean.TYPE, Boolean.TYPE);
//        definition.declareOperatorAlias("", "or", Boolean.TYPE, Boolean.TYPE);
//
//        definition.declareOperatorAlias("sauf", "-", Boolean.TYPE, Boolean.TYPE);
//        definition.declareOperatorAlias("but", "-", Boolean.TYPE, Boolean.TYPE);
//
//        definition.declareConst("true", true);
//        definition.declareConst("all", true);
//        definition.declareConst("tous", true);
//        definition.declareConst("false", false);
//        definition.declareConst("aucun", false);
//        definition.declareConst("none", false);
        //definition.importType(PlatformHelper.class);
        //definition.importType(ExtraHelper.class);
        this.declareListOperator("", Object.class);
        this.declareListOperator(",", Object.class);
        this.declareOperator(new FunctionBase("=", Object.class, new Class[]{Object.class}) {
            @Override
            public Class getResultType(ExpressionManager evaluator, ExpressionNode[] operands) {
                return operands == null ? Object.class : operands[1].getExprType(evaluator);
            }

            @Override
            public Class getEffectiveResultType(ExpressionEvaluator evaluator, ExpressionNode[] operands) {
                return operands == null ? Object.class : operands[1].getEffectiveExprType(evaluator);
            }

            @Override
            public Object evaluate(ExpressionNode[] operands, ExpressionEvaluator evaluator) {
                ExpressionNodeVariable var = (ExpressionNodeVariable) operands[0];
                ExpressionNode value = operands[1];
                Object v = value.evaluate(evaluator);
                evaluator.setVariableValue(var.getName(), v);
                return v;
            }
        });
        this.importType(PlatformHelper.class);
        this.importType(AdamLanFunctions.class);
        this.addResolver(new AdamLanResolverImpl());
        this.setTokenizerConfig(new ExpressionStreamTokenizerConfig()
                .setAcceptComplexNumber(false)
                .setCStyleComments()
                .setLineComment("#")
                .setIdentifierFilter(new AbstractIdentifierFilter() {
                    @Override
                    public boolean isIdentifierStart(char cc) {
                        //believe it or not ids can start with numbers ... yeh !!!
                        return super.isIdentifierStart(cc) || (cc >= '0' && cc <= '9');
                    }

                    @Override
                    public boolean isIdentifierPart(char cc) {
                        // should accept simple quote
                        return super.isIdentifierPart(cc) || cc == '\'';
                    }

                })
        );
        for (LangSupport langSupport : all_languages) {
            for (CommandResolver resolver : langSupport.getResolvers()) {
                addResolver(resolver);
            }
        }
    }

    private void addResolver(CommandResolver resolver) {
        if (resolver != null) {
            resolvers.add(resolver);
        }
    }

    private List<CommandResolver> getEnabledResolvers() {
        if (cache_resolvers == null) {
            cache_resolvers = new ArrayList<>();
            for (CommandResolver resolver : resolvers) {
                if (resolver.isEnabled(this)) {
                    cache_resolvers.add(resolver);
                }
            }
        }
        return cache_resolvers;
    }

    public ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("net.vpc.scholar.adam.adamlan", locale == null ? Locale.ENGLISH : locale);
        }
        return bundle;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        bundle = null;
        cache_resolvers = null;
        System.out.println("setLocale " + locale);
    }

    @Override
    public AdamLanExecEnv env() {
        return this.env;
    }

    public ExpressionNode parseLine(String line) {
        //should find first # and skip comment ??
        if (line.isEmpty()) {
            return null;
        } else {
            return parseResolvedExpressionNode(line);
        }
    }

    private class Yaccer {

        BufferedReader r;
        LinkedList<ExpressionNode> queue = new LinkedList<ExpressionNode>();

        public Yaccer(String text) {
            r = new BufferedReader(new StringReader(text));
        }

        private ExpressionNodeBlock nextAll() {
            ExpressionNodeBlock b = new ExpressionNodeBlock();
            ExpressionNode a = null;
            while ((a = nextAny()) != null) {
                b.add(a);
            }
            return b;
        }

        private ExpressionNode nextAny() {
            ExpressionNode li = nextLine();
            if (li == null) {
                return null;
            }
            if (li instanceof ExpressionNodeBlock) {
                while (true) {
                    ExpressionNode n = nextLine();
                    if (n == null) {
                        // no more
                        return li;
                    }
                    if (n instanceof ExpressionNodeEnd) {
                        return li;
                    }
                    pushBack(n);
                    ExpressionNode a = nextAny();
                    ((ExpressionNodeBlock) li).add(n);
                }
            } else if (li instanceof ExpressionNodeWhile) {
                ExpressionNodeBlock b = new ExpressionNodeBlock();
                while (true) {
                    ExpressionNode n = nextLine();
                    if (n == null) {
                        // no more
                        break;
                    }
                    if (n instanceof ExpressionNodeEnd) {
                        break;
                    }
                    pushBack(n);
                    ExpressionNode a = nextAny();
                    b.add(a);
                }
                ((ExpressionNodeWhile) li).setBlock(b);
                return li;
            } else if (li instanceof ExpressionNodeFor) {
                ExpressionNodeBlock b = new ExpressionNodeBlock();
                while (true) {
                    ExpressionNode n = nextLine();
                    if (n == null) {
                        // no more
                        break;
                    }
                    if (n instanceof ExpressionNodeEnd) {
                        break;
                    }
                    pushBack(n);
                    ExpressionNode a = nextAny();
                    b.add(a);
                }
                ((ExpressionNodeFor) li).setBlock(b);
                return li;
            } else if (li instanceof ExpressionNodeIf) {
                ExpressionNodeBlock btrue = new ExpressionNodeBlock();
                boolean expectElse = false;
                while (true) {
                    ExpressionNode n = nextLine();
                    if (n == null) {
                        // no more
                        break;
                    }
                    if (n instanceof ExpressionNodeEnd) {
                        break;
                    }
                    pushBack(n);
                    ExpressionNode a = nextAny();
                    btrue.add(a);
                }

                ((ExpressionNodeIf) li).setTrueBlock(btrue);

                if (expectElse) {
                    ExpressionNodeBlock bfalse = new ExpressionNodeBlock();
                    while (true) {
                        ExpressionNode n = nextLine();
                        if (n == null) {
                            // no more
                            break;
                        }
                        if (n instanceof ExpressionNodeEnd) {
                            break;
                        }
                        pushBack(n);
                        ExpressionNode a = nextAny();
                        bfalse.add(a);
                    }
                    ((ExpressionNodeIf) li).setFalseBlock(bfalse);
                }

                return li;
            }
            return li;
        }

        private void pushBack(ExpressionNode n) {
            queue.push(n);
        }

        private ExpressionNode nextLine() {
            if (!queue.isEmpty()) {
                return queue.remove();
            }
            String line = null;
            while (true) {
                try {
                    line = r.readLine();
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                }
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        if (line.endsWith("...")) {
                            sb.append(line.substring(0, line.length() - 3));
                            try {
                                line = r.readLine();
                            } catch (IOException ex) {
                                //Exceptions.printStackTrace(ex);
                            }
                            if (line == null) {
                                break;
                            }
                        } else {
                            sb.append(line);
                            break;
                        }
                    }
                    ExpressionNode ff = parseLine(sb.toString());
                    if (ff != null) {
                        return ff;
                    }
                }
            }
            return null;
        }

    }


    public ExpressionStreamTokenizer.Token[] readVars(DefaultExpressionParser parser, int max) {
        List<ExpressionStreamTokenizer.Token> found = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            ExpressionStreamTokenizer.Token word = parser.readWithComments();
            if (word.ttype == ExpressionStreamTokenizer.TT_EOF) {
                break;
            } else if (word.ttype == ExpressionStreamTokenizer.TT_WORD) {
                found.add(word);
            } else {
                break;
            }
        }
        return found.toArray(new ExpressionStreamTokenizer.Token[found.size()]);
    }

    public ExpressionNode parseResolvedExpressionNode(String text) {
        // there is a problem with = operator as.
        // if it s in the very first line, it should have the lower precedence!
        DefaultExpressionParser parser = new DefaultExpressionParser(text, this);

        String assignVarName = null;
        ExpressionStreamTokenizer.Token aa = parser.readWithComments();
        ExpressionStreamTokenizer.Token bb = parser.readWithComments();
        if (bb.ttype == '=' && aa.ttype == ExpressionStreamTokenizer.TT_WORD) {
            assignVarName = aa.image;
        } else {
            parser.getTokenizer().pushBack(aa);
            parser.getTokenizer().pushBack(bb);
        }

//        ExpressionStreamTokenizer.Token[] tokens = readVars(parser, 5);
//        ExpressionStreamTokenizer.Token[] lets = AdamLanUtils.readWordId("let", tokens, getLangSupport());
//        if(lets==null){
//            lets=tokens;
//
//        }

//        ExpressionStreamTokenizer tok = parser.getTokenizer();
//        for (int i = 0; i < 3; i++) {
//
//        }
//        ExpressionStreamTokenizer.Token word = parser.readWithComments();
//        String assignVarName = null;
//        if (word.ttype == ExpressionStreamTokenizer.TT_WORD) {
//            ExpressionStreamTokenizer.Token eq = parser.readWithComments();
//            if (eq.image.equals("=")) {
//                //this is an assign!
//                assignVarName = word.image;
//            } else {
//                tok.pushBack(word);
//                if (eq.ttype != ExpressionStreamTokenizer.TT_EOF) {
//                    tok.pushBack(eq);
//                }
//            }
//        }
        ExpressionNode n = null;
        try {
            n = parser.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (assignVarName != null) {
            return new ExpressionNodeAssign(assignVarName, n);
        }
        if (n != null) {
            WeightedExpressionNode b = null;
            for (CommandResolver resolver : getEnabledResolvers()) {
                WeightedExpressionNode nn = resolver.resolve(n, this);
                if (nn != null && (b == null || nn.getWeight() > b.getWeight())) {
                    b = nn;
                }
            }
            if (b == null) {
                ExpressionNode[] arr = AdamLanUtils.toArray(n);
                int x = 0;
                while (x < arr.length && AdamLanUtils.isVar(arr[x])) {
                    x++;
                }
                if (x > 0) {
                    for (int i = x - 1; i >= 0; i--) {
                        LangSupport ll = getLangSupport();
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j <= i; j++) {
                            if (j > 0) {
                                sb.append(" ");
                            }
                            sb.append(AdamLanUtils.getVarName(arr[j]));
                        }
                        ExpressionNode[] arr2 = new ExpressionNode[arr.length - i - 1];
                        System.arraycopy(arr, i + 1, arr2, 0, arr.length - i - 1);
                        String tname = ll.translateWord(sb.toString());
                        Function function = findFunction(tname, arr2);
                        if (function != null) {
                            return new ExpressionNodeFunctionCall(tname, arr2);
                        }
                    }
                } else if (arr.length == 1 && arr[0] instanceof ExpressionNodeFunctionCall) {
                    return arr[0];
                }

                throw new IllegalArgumentException("Invalid command " + text);
            }
            return b == null ? null : b.getNode();
        }
        return null;
    }

    public ExpressionNode parseLineExpressionNode(String text) {
        return super.parse(text);
    }

    public LangSupport getLangSupport() {
        for (LangSupport all_language : all_languages) {
            if (all_language.getLocale().getLanguage().equals(getLocale().getLanguage())) {
                return all_language;
            }
        }
        throw new IllegalArgumentException("Not found");
    }

    public Locale detectLocale(String text) {
        int bestLangSupport = -1;
        int bestLangSupportIndex = -1;
        for (int j = 0; j < all_languages.length; j++) {
            LangSupport all_language = all_languages[j];
            int i = all_language.getSupportLevel(text);
            if (i > 0 && bestLangSupport < 0 || bestLangSupport < i) {
                bestLangSupport = i;
                bestLangSupportIndex = j;
            }
        }
        if (bestLangSupportIndex >= 0) {
            return (all_languages[bestLangSupportIndex].getLocale());
        }
        return null;
    }

    public ExpressionNode parse(String text) {
        Locale locale = detectLocale(text);
        if (locale != null) {
            setLocale(locale);
        }
        Yaccer yy = new Yaccer(text);
        while (true) {
            ExpressionNode line = yy.nextLine();
            if (line == null) {
                break;
            }
            if (line instanceof ExpressionNodeAssign) {
                ExpressionNodeAssign a = (ExpressionNodeAssign) line;
                if (a.getName().equals("lang")) {
                    ExpressionNode v = a.getValue();
                    if (v instanceof ExpressionNodeLiteral) {
                        setLocale(new Locale(String.valueOf(((ExpressionNodeLiteral) v).getValue())));
                    } else if (v instanceof ExpressionNodeVariableName) {
                        setLocale(new Locale(String.valueOf(((ExpressionNodeVariableName) v).getName())));
                    } else {
                        throw new IllegalArgumentException("Invalid");
                    }
                } else {
                    yy.pushBack(line);
                    break;
                }
            } else if (line != null) {
                yy.pushBack(line);
                break;
            }
        }
        ExpressionNodeBlock expressionNodeBlock = yy.nextAll();
        System.out.println(expressionNodeBlock);
        return expressionNodeBlock;
    }

    public Object evaluate(File file) {
        return createEvaluator(readAllBytesJava7(file)).evaluate();
    }

    private static String readAllBytesJava7(File filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(filePath.toPath()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return content;
    }

    private class MyRunnable implements Runnable {
        Thread curr;
        Runnable run;

        @Override
        public void run() {
            synchronized (allThreads) {
                allThreads.add(this);
            }
            try {
                run.run();
            } finally {
                synchronized (allThreads) {
                    allThreads.remove(this);
                }
            }
        }
    }

    public Thread run(Runnable r) {
        MyRunnable r2 = new MyRunnable();
        Thread t = new Thread(r2);
        r2.curr = t;
        r2.run = r;
        t.start();
        return t;
    }

    public void dispose() {

    }
}
