/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.*;
import net.thevpc.common.textsource.JTextSourceFactory;
import net.thevpc.jeep.core.nodes.*;
import net.thevpc.jeep.core.imports.PlatformHelperImports;
import net.thevpc.jeep.core.tokens.SimpleTokenPattern;
import net.thevpc.jeep.core.eval.JEvaluableNode;
import net.thevpc.jeep.impl.functions.DefaultJInvokeContext;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;
import net.thevpc.scholar.adamlan.utils.AdamLanUtils;

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

import net.thevpc.scholar.adamlan.commands.en.EnglishLanSupport;
import net.thevpc.scholar.adamlan.commands.fr.FrenchLanSupport;

/**
 * @author thevpc
 */
public class AdamLanParserImpl extends DefaultJeep implements AdamLanParser {

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
        this.operators().declareBinaryOperators("+", "-", "*", "/", "&&", "||", "<", "<=", ">", ">=", "!=", "=", ",");
        this.operators().declareBinaryOperator("", JOperatorPrecedences.PRECEDENCE_0);

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
        //definition.importType(PlatformHelperImports.class);
        //definition.importType(ExtraHelper.class);
        this.operators().declareListOperator("", Object.class,JOperatorPrecedences.PRECEDENCE_1);
        this.operators().declareListOperator(",", Object.class,JOperatorPrecedences.PRECEDENCE_1);
        JType ObjectjType = JTypeUtils.forObject(this.types());
        this.operators().declareOperator(new JFunctionBase("=", ObjectjType, new JType[]{ObjectjType}) {
            @Override
            public String getSourceName() {
                return "generated";
            }

            @Override
            public Object invoke(JInvokeContext icontext) {
                JEvaluableNode n = (JEvaluableNode) icontext.getArguments()[0];
                JNodeVarName var=(JNodeVarName) n.getNode();
                Object v = icontext.evaluateArg(1);
                icontext.getContext().vars().setValue(var.getName(), v,icontext);
                return v;
            }
        });
        this.resolvers().importType(PlatformHelperImports.class);
        this.resolvers().importType(AdamLanFunctions.class);
        this.resolvers().addResolver(new AdamLanResolverImpl());
        this.tokens().config().setAll(new JTokenConfigBuilder()
//                .setAcceptComplexNumber(false)
                .setCStyleComments()
                .setLineComment("#")
                .setIdPattern(new AdamLangIdPattern())
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
            bundle = ResourceBundle.getBundle("net.thevpc.scholar.adam.adamlan", locale == null ? Locale.ENGLISH : locale);
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

    public JNode parseLine(String line) {
        //should find first # and skip comment ??
        if (line.isEmpty()) {
            return null;
        } else {
            return parseResolvedExpressionNode(line);
        }
    }

    private static class AdamLangIdPattern extends SimpleTokenPattern {
        public AdamLangIdPattern() {
            super();
        }

        @Override
        public boolean accept(CharSequence prefix, char c) {
            if(prefix.length()==0){
                if(Character.isWhitespace(c)){
                    return false;
                }
                if("0-9\\[]{}()*+/~&=$£%'\"-".indexOf(c)>=0){
                    return false;
                }
            }else{
                if(c==' '){
                    return true;
                }
                if(Character.isWhitespace(c)){
                    return false;
                }
                if("\\[]{}()*+/~&=$£%'\"-".indexOf(c)>=0){
                    return false;
                }
            }
            return true;
        }
    }

    private class Yaccer {

        BufferedReader r;
        LinkedList<JNode> queue = new LinkedList<JNode>();

        public Yaccer(String text) {
            r = new BufferedReader(new StringReader(text));
        }

        private JNodeBlock nextAll() {
            JNodeBlock b = new JNodeBlock();
            JNode a = null;
            while ((a = nextAny()) != null) {
                b.add((JDefaultNode) a);
            }
            return b;
        }

        private JNode nextAny() {
            JNode li = nextLine();
            if (li == null) {
                return null;
            }
            if (li instanceof JNodeBlock) {
                while (true) {
                    JNode n = nextLine();
                    if (n == null) {
                        // no more
                        return li;
                    }
                    if (n instanceof JNodeEnd) {
                        return li;
                    }
                    pushBack(n);
                    JNode a = nextAny();
                    ((JNodeBlock) li).add((JDefaultNode) n);
                }
            } else if (li instanceof JNodeWhile) {
                JNodeBlock b = new JNodeBlock();
                while (true) {
                    JNode n = nextLine();
                    if (n == null) {
                        // no more
                        break;
                    }
                    if (n instanceof JNodeEnd) {
                        break;
                    }
                    pushBack(n);
                    JNode a = nextAny();
                    b.add((JDefaultNode) a);
                }
                ((JNodeWhile) li).setBlock(b);
                return li;
            } else if (li instanceof JNodeFor) {
                JNodeBlock b = new JNodeBlock();
                while (true) {
                    JNode n = nextLine();
                    if (n == null) {
                        // no more
                        break;
                    }
                    if (n instanceof JNodeEnd) {
                        break;
                    }
                    pushBack(n);
                    JNode a = nextAny();
                    b.add((JDefaultNode) a);
                }
                ((JNodeFor) li).setBlock(b);
                return li;
            } else if (li instanceof JNodeIf) {
                JNodeBlock btrue = new JNodeBlock();
                boolean expectElse = false;
                while (true) {
                    JNode n = nextLine();
                    if (n == null) {
                        // no more
                        break;
                    }
                    if (n instanceof JNodeEnd) {
                        break;
                    }
                    pushBack(n);
                    JNode a = nextAny();
                    btrue.add((JDefaultNode) a);
                }

                ((JNodeIf) li).setTrueBlock(btrue);

                if (expectElse) {
                    JNodeBlock bfalse = new JNodeBlock();
                    while (true) {
                        JNode n = nextLine();
                        if (n == null) {
                            // no more
                            break;
                        }
                        if (n instanceof JNodeEnd) {
                            break;
                        }
                        pushBack(n);
                        JNode a = nextAny();
                        bfalse.add((JDefaultNode) a);
                    }
                    ((JNodeIf) li).setFalseBlock(bfalse);
                }

                return li;
            }
            return li;
        }

        private void pushBack(JNode n) {
            queue.push(n);
        }

        private JNode nextLine() {
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
                    JNode ff = parseLine(sb.toString());
                    if (ff != null) {
                        return ff;
                    }
                }
            }
            return null;
        }

    }


    public JToken[] readVars(DefaultJParser parser, int max) {
        List<JToken> found = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            JToken word = parser.next();
            if (word.def.ttype == JTokenType.TT_EOF) {
                break;
            } else if (word.def.ttype == JTokenType.TT_IDENTIFIER) {
                found.add(word);
            } else {
                break;
            }
        }
        return found.toArray(new JToken[found.size()]);
    }

    public JNode parseResolvedExpressionNode(String text) {
        // there is a problem with = operator as.
        // if it s in the very first line, it should have the lower precedence!
        DefaultJParser parser = new DefaultJParser(
                tokens().of(text,true,true),
                new DefaultJCompilationUnit(JTextSourceFactory.fromString(text,"<text>")),
                this,
                //TODO null?
                null);

        String assignVarName = null;
        JToken aa = parser.next();
        JToken bb = parser.next();
        if (bb.def.ttype == '=' && aa.def.ttype == JTokenType.TT_IDENTIFIER) {
            assignVarName = aa.image;
        } else {
            parser.tokenizer().pushBack(aa);
            parser.tokenizer().pushBack(bb);
        }

//        JTokenizer.JToken[] tokens = readVars(parser, 5);
//        JTokenizer.JToken[] lets = AdamLanUtils.readWordId("let", tokens, getLangSupport());
//        if(lets==null){
//            lets=tokens;
//
//        }

//        JTokenizer tok = parser.getTokenizer();
//        for (int i = 0; i < 3; i++) {
//
//        }
//        JTokenizer.JToken word = parser.readWithComments();
//        String assignVarName = null;
//        if (word.ttype == JTokenizer.TT_WORD) {
//            JTokenizer.JToken eq = parser.readWithComments();
//            if (eq.image.equals("=")) {
//                //this is an assign!
//                assignVarName = word.image;
//            } else {
//                tok.pushBack(word);
//                if (eq.ttype != JTokenizer.TT_EOF) {
//                    tok.pushBack(eq);
//                }
//            }
//        }
        JNode n = parser.parse();
        if (assignVarName != null) {
            return new JNodeAssign(new JNodeVarName(assignVarName), (JDefaultNode) n);
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
                JNode[] arr = AdamLanUtils.toArray(n);
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
                        JNode[] arr2 = new JNode[arr.length - i - 1];
                        System.arraycopy(arr, i + 1, arr2, 0, arr.length - i - 1);
                        String tname = ll.translateWord(sb.toString());
                        JFunction JFunction = functions().findFunctionMatchOrNull(
                                JSignature.of(
                                tname, JNodeUtils2.getTypes((JDefaultNode[]) arr2)
                        ),
                                null// TODO null?
                                );
                        if (JFunction != null) {
                            return new JNodeFunctionCall(tname, (JDefaultNode[]) arr2);
                        }
                    }
                } else if (arr.length == 1 && arr[0] instanceof JNodeFunctionCall) {
                    return arr[0];
                }

                throw new IllegalArgumentException("Invalid command " + text);
            }
            return b == null ? null : b.getNode();
        }
        return null;
    }

    public JNode parseLineExpressionNode(String text) {
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

    public JNode parse(String text) {
        Locale locale = detectLocale(text);
        if (locale != null) {
            setLocale(locale);
        }
        Yaccer yy = new Yaccer(text);
        while (true) {
            JNode line = yy.nextLine();
            if (line == null) {
                break;
            }
            if (line instanceof JNodeAssign) {
                JNodeAssign a = (JNodeAssign) line;
                if (a.getName().equals("lang")) {
                    JNode v = a.getValue();
                    if (v instanceof JNodeLiteral) {
                        setLocale(new Locale(String.valueOf(((JNodeLiteral) v).getValue())));
                    } else if (v instanceof JNodeVarName) {
                        setLocale(new Locale(String.valueOf(((JNodeVarName) v).getName())));
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
        JNodeBlock expressionNodeBlock = yy.nextAll();
        System.out.println(expressionNodeBlock);
        return expressionNodeBlock;
    }

    public Object evaluate(File file) {
        JContext jContext = newContext();
        JEvaluator jEvaluator = jContext.evaluators().newEvaluator();
        return jEvaluator.evaluate(
               jContext.parsers().of(file).parse(),
                new DefaultJInvokeContext(
                        jContext,
                        jEvaluator,
                        null,
                        new JEvaluable[0],
                        null,
                        null
                )

        );
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
