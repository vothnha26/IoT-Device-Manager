package com.iot.management.rules;

import java.util.Map;

/**
 * Lightweight expression evaluator compatible with simple logical expressions like:
 * "nhiet_do > 30 AND do_am < 70" or "NOT (trang_thai = 'tat')".
 * Supports: &&, ||, !, AND/OR/NOT, parentheses, comparisons (>, <, >=, <=, =, ==, !=) against numbers/booleans/strings.
 */
public class EasyRulesEngine {

    // Normalize logical operators from user-friendly text to Java-style tokens
    public static String normalizeExpression(String expr) {
        if (expr == null) return null;
        String s = expr.trim();
        s = s.replace('\n', ' ');
        s = s.replaceAll("(?i)\\bAND\\b", "&&");
        s = s.replaceAll("(?i)\\bOR\\b", "||");
        s = s.replaceAll("(?i)\\bNOT\\b", "!");
        return s;
    }

    public static boolean evaluate(String expression, Map<String, Object> values) {
        String expr = normalizeExpression(expression);
        if (expr == null || expr.isEmpty()) return false;
        try {
            Tokenizer tokenizer = new Tokenizer(expr);
            Parser parser = new Parser(tokenizer, values);
            boolean result = parser.parseExpression();
            if (tokenizer.current.type != TokenType.EOF) {
                // trailing tokens -> invalid
                return false;
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    // Tokenizer and Parser implementation for a minimal boolean expression language
    enum TokenType { IDENT, NUMBER, STRING, BOOL, OP, LPAREN, RPAREN, NOT, AND, OR, EOF }

    static class Token {
        final TokenType type;
        final String text;
        Token(TokenType type, String text) { this.type = type; this.text = text; }
    }

    static class Tokenizer {
        private final String s;
        private int i = 0;
        Token current;
        Tokenizer(String s) {
            this.s = s;
            advance();
        }
        void advance() {
            skipWs();
            if (i >= s.length()) { current = new Token(TokenType.EOF, ""); return; }
            char c = s.charAt(i);
            if (c == '(') { i++; current = new Token(TokenType.LPAREN, "("); return; }
            if (c == ')') { i++; current = new Token(TokenType.RPAREN, ")"); return; }
            if (c == '!' ) { i++; current = new Token(TokenType.NOT, "!"); return; }
            if (c == '&' && peek('&')) { i += 2; current = new Token(TokenType.AND, "&&"); return; }
            if (c == '|' && peek('|')) { i += 2; current = new Token(TokenType.OR, "||"); return; }
            if (c == '>' || c == '<' || c == '=' || c == '!') {
                String op = readOp(); current = new Token(TokenType.OP, op); return;
            }
            if (c == '\'' || c == '"') {
                String str = readString(c); current = new Token(TokenType.STRING, str); return;
            }
            if (Character.isDigit(c) || (c == '-' && peekDigit())) {
                String num = readNumber(); current = new Token(TokenType.NUMBER, num); return;
            }
            String ident = readIdent();
            if (ident.equalsIgnoreCase("true") || ident.equalsIgnoreCase("false")) {
                current = new Token(TokenType.BOOL, ident.toLowerCase());
            } else if (ident.equalsIgnoreCase("and")) {
                current = new Token(TokenType.AND, "&&");
            } else if (ident.equalsIgnoreCase("or")) {
                current = new Token(TokenType.OR, "||");
            } else if (ident.equalsIgnoreCase("not")) {
                current = new Token(TokenType.NOT, "!");
            } else {
                current = new Token(TokenType.IDENT, ident);
            }
        }
        private void skipWs() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
        private boolean peek(char ch) { return (i + 1 < s.length()) && s.charAt(i + 1) == ch; }
        private boolean peekDigit() { return (i + 1 < s.length()) && Character.isDigit(s.charAt(i + 1)); }
        private String readOp() {
            int start = i; i++;
            if (i < s.length()) {
                char next = s.charAt(i);
                if (next == '=' || (s.charAt(start) == '=' && next == '=')) i++;
            }
            return s.substring(start, i);
        }
        private String readString(char quote) {
            i++;
            StringBuilder b = new StringBuilder();
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == quote) break;
                if (c == '\\' && i < s.length()) {
                    char n = s.charAt(i++);
                    b.append(n);
                } else {
                    b.append(c);
                }
            }
            return b.toString();
        }
        private String readNumber() {
            int start = i; i++;
            while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) i++;
            return s.substring(start, i);
        }
        private String readIdent() {
            int start = i; i++;
            while (i < s.length()) {
                char c = s.charAt(i);
                if (Character.isLetterOrDigit(c) || c == '_' ) { i++; }
                else break;
            }
            return s.substring(start, i);
        }
    }

    static class Parser {
        private final Tokenizer tz;
        private final Map<String, Object> facts;
        Parser(Tokenizer tz, Map<String, Object> facts) { this.tz = tz; this.facts = facts; }

        boolean parseExpression() {
            boolean v = parseOr();
            return v;
        }
        private boolean parseOr() {
            boolean v = parseAnd();
            while (tz.current.type == TokenType.OR) { tz.advance(); v = v || parseAnd(); }
            return v;
        }
        private boolean parseAnd() {
            boolean v = parseNot();
            while (tz.current.type == TokenType.AND) { tz.advance(); v = v && parseNot(); }
            return v;
        }
        private boolean parseNot() {
            if (tz.current.type == TokenType.NOT) { tz.advance(); return !parsePrimary(); }
            return parsePrimary();
        }
        private boolean parsePrimary() {
            if (tz.current.type == TokenType.LPAREN) { tz.advance(); boolean v = parseExpression(); expect(TokenType.RPAREN); tz.advance(); return v; }
            return parseComparison();
        }
        private void expect(TokenType type) { if (tz.current.type != type) throw new IllegalArgumentException("Unexpected token: " + tz.current.text); }

        private boolean parseComparison() {
            if (tz.current.type != TokenType.IDENT) throw new IllegalArgumentException("Expect identifier, got: " + tz.current.text);
            String ident = tz.current.text; tz.advance();
            if (tz.current.type != TokenType.OP) throw new IllegalArgumentException("Expect operator after ident");
            String op = tz.current.text; tz.advance();
            Object right = parseLiteral();
            Object left = facts != null ? facts.get(ident) : null;
            return compare(left, op, right);
        }

        private Object parseLiteral() {
            Token t = tz.current; tz.advance();
            return switch (t.type) {
                case NUMBER -> parseNumber(t.text);
                case STRING -> t.text;
                case BOOL -> Boolean.parseBoolean(t.text);
                case IDENT -> t.text; // treat bare ident as string
                default -> throw new IllegalArgumentException("Expect literal, got: " + t.text);
            };
        }

        private Number parseNumber(String s) {
            if (s.contains(".")) return Double.parseDouble(s);
            try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return Double.parseDouble(s); }
        }

        private boolean compare(Object left, String op, Object right) {
            if (left == null) return false;
            // Normalize numbers
            if (left instanceof Number || right instanceof Number) {
                double l = toDouble(left);
                double r = toDouble(right);
                return switch (op) {
                    case ">" -> l > r;
                    case "<" -> l < r;
                    case ">=" -> l >= r;
                    case "<=" -> l <= r;
                    case "==", "=" -> Double.compare(l, r) == 0;
                    case "!=" -> Double.compare(l, r) != 0;
                    default -> false;
                };
            }
            if (left instanceof Boolean || right instanceof Boolean) {
                boolean l = Boolean.parseBoolean(String.valueOf(left));
                boolean r = Boolean.parseBoolean(String.valueOf(right));
                return switch (op) {
                    case "==", "=" -> l == r;
                    case "!=" -> l != r;
                    default -> false;
                };
            }
            String l = String.valueOf(left);
            String r = String.valueOf(right);
            int cmp = l.compareTo(r);
            return switch (op) {
                case ">" -> cmp > 0;
                case "<" -> cmp < 0;
                case ">=" -> cmp >= 0;
                case "<=" -> cmp <= 0;
                case "==", "=" -> cmp == 0;
                case "!=" -> cmp != 0;
                default -> false;
            };
        }
        private double toDouble(Object o) { return o instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(o)); }
    }
}
