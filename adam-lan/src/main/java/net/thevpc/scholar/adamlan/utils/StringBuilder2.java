/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.utils;

/**
 *
 * @author thevpc
 */
public class StringBuilder2 {
    private StringBuilder sb = new StringBuilder();

    public StringBuilder2() {
    }

    public StringBuilder2(String any) {
        sb.append(any);
    }

    public StringBuilder2 append(Object s) {
        sb.append(s);
        return this;
    }

    public StringBuilder2 append(String s) {
        sb.append(s);
        return this;
    }

    public StringBuilder2 append(int s) {
        sb.append(s);
        return this;
    }

    public boolean deleteHead(String head) {
        if (startsWith(head)) {
            deleteHead(head.length());
            return true;
        }
        return false;
    }

    public StringBuilder2 deleteHead(int count) {
        sb.delete(0, count);
        return this;
    }

    public boolean startsWith(String s) {
        return toString().startsWith(s);
    }

    public boolean isDigit(int index) {
        return Character.isDigit(sb.charAt(index));
    }

    public boolean isAlphabetic(int index) {
        return Character.isAlphabetic(sb.charAt(index));
    }

    public boolean isEmpty() {
        return sb.length() == 0;
    }

    public Integer readInt() {
        int i = 0;
        if (isEmpty()) {
            return null;
        }
        if (sb.charAt(0) == '-') {
            i++;
        }
        while (i < sb.length() && Character.isDigit(sb.charAt(i))) {
            i++;
        }
        try {
            int y = Integer.parseInt(sb.substring(0, i));
            deleteHead(i);
            return y;
        } catch (Exception ex) {
            return null;
        }
    }

    public Double readDouble() {
        int i = 0;
        if (isEmpty()) {
            return null;
        }
        if (sb.charAt(0) == '-') {
            i++;
        }
        while (i < sb.length() && Character.isDigit(sb.charAt(i))) {
            i++;
        }
        if (i < sb.length() && sb.charAt(i) == '.') {
            i++;
        }
        while (i < sb.length() && Character.isDigit(sb.charAt(i))) {
            i++;
        }
        try {
            double r = Double.parseDouble(sb.substring(0, i));
            deleteHead(i);
            return r;
        } catch (Exception ex) {
            return null;
        }
    }

    public StringBuilder2 toLowerCase() {
        String v = sb.toString().toLowerCase();
        sb.delete(0, sb.length());
        sb.append(v);
        return this;
    }

    public StringBuilder2 toUpperCase() {
        String v = sb.toString().toUpperCase();
        sb.delete(0, sb.length());
        sb.append(v);
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    public StringBuilder2 clear() {
        sb.delete(0,sb.length());
        return this;
    }
}
