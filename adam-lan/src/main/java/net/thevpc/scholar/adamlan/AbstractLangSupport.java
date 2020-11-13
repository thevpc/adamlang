/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.common.strings.StringUtils;

/**
 *
 * @author thevpc
 */
public abstract class AbstractLangSupport implements LangSupport {

    private static final Logger log = Logger.getLogger(AbstractLangSupport.class.getName());

    private List<CommandResolver> resolvers = new ArrayList<>();
    private Locale locale;
    private Map<String, String> dict = new HashMap<>();
    private ResourceBundle thesaurus;

    public AbstractLangSupport(Locale locale) {
        this.locale = locale;
        thesaurus = ResourceBundle.getBundle("net.thevpc.scholar.adamlan.adamlan", getLocale());
        for (String k : Collections.list(thesaurus.getKeys())) {
            String v = StringUtils.normalizeString(thesaurus.getString(k));
            StreamTokenizer t = new StreamTokenizer(new StringReader(v));
            t.resetSyntax();
            t.wordChars('a', 'z');
            t.wordChars('A', 'Z');
            t.wordChars('\'', '\'');
            t.wordChars(128 + 32, 255);
            t.whitespaceChars(0, ' ');
            t.quoteChar('"');
            try {
                while (t.nextToken() != StreamTokenizer.TT_EOF) {
                    switch (t.ttype) {
                        case StreamTokenizer.TT_WORD: {
                            dict.put(StringUtils.normalizeString(t.sval), k);
                            break;
                        }
                        case '\"': {
                            dict.put(StringUtils.normalizeString(t.sval),k);
                            break;
                        }
                        default: {
                            //ignore
                        }
                    }

                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    public String[][] getTranslations(String wordId) {
        Set<String> all = new HashSet<>();
        for (Map.Entry<String, String> entry : dict.entrySet()) {
            if (entry.getValue().equals(wordId)) {
                all.add(entry.getKey());
            }
        }
        List<String[]> ok = new ArrayList<>();
        for (String string : all) {
            List<String> w = new ArrayList<>();
            StringTokenizer t = new StringTokenizer(string, " ");
            while (t.hasMoreTokens()) {
                w.add(t.nextToken());
            }
            if (w.size() > 0) {
                ok.add(w.toArray(new String[w.size()]));
            }
        }
        ok.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                if (o1.length != o2.length) {
                    return o2.length - o1.length;
                }
                for (int i = 0; i < o1.length; i++) {
                    int x = o1[i].compareTo(o2[i]);
                    if (x != 0) {
                        return x;
                    }
                }
                //should never happen;
                return 0;
            }
        });
        return ok.toArray(new String[ok.size()][]);
    }

    @Override
    public String translateWord(String str) {
        if (str == null) {
            str = "";
        }
        String n = dict.get(StringUtils.normalizeString(str.trim().toLowerCase()));
        if (n != null) {
            return n;
        }
        return str;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    public CommandResolver[] getResolvers() {
        return resolvers.toArray(new CommandResolver[resolvers.size()]);
    }

    protected void addResolver(CommandResolver r) {
        resolvers.add(r);
    }

    public String normalizeString(String text) {
        if (text == null) {
            text = "";
        }
        return StringUtils.normalizeString(text.toLowerCase().trim());
    }

    public int getSupportLevel(String text) {
        Set<String> words = new HashSet<>(extractWords(text));
        Set<String> dic = new HashSet<>(dict.keySet());
        int lev = 0;
        for (String s : words) {
            if (dic.contains(s)) {
                lev++;
            }
        }
        return lev;
    }

    public Set<String> extractWords(String text) {
        text = normalizeString(text);
        HashSet<String> found = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        boolean lineComment = false;
        boolean blocComment = false;
        char[] cc = text.toCharArray();
        for (int i = 0; i < cc.length; i++) {
            char c = cc[i];
            if (lineComment) {
                if (c == '\n') {
                    lineComment = false;
                }
            } else if (blocComment) {
                if (c == '/' && i > 0 && cc[i - 1] == '*') {
                    blocComment = false;
                }
            } else if (c == '#') {
                if (sb.length() > 0) {
                    found.add(sb.toString());
                    sb = sb.delete(0, sb.length());
                }
                lineComment = true;
            } else if (c == '/' && i + 1 < cc.length && cc[i + 1] == '*') {
                if (sb.length() > 0) {
                    found.add(sb.toString());
                    sb = sb.delete(0, sb.length());
                }
                i++;
                blocComment = true;
            } else if (Character.isAlphabetic(c)) {
                sb.append(c);
            } else {
                if (sb.length() > 0) {
                    found.add(sb.toString());
                    sb = sb.delete(0, sb.length());
                }
            }
        }
        if (sb.length() > 0) {
            found.add(sb.toString());
        }
        return found;
    }

}
