package me.usainsrht.itemapi.itemtext;

final class AmountRenderer {

    private static final char[] SUPERSCRIPT = {'⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹'};
    private static final char[] SUBSCRIPT = {'₀', '₁', '₂', '₃', '₄', '₅', '₆', '₇', '₈', '₉'};

    private AmountRenderer() {
    }

    static String render(int amount, AmountDisplay display) {
        if (display == AmountDisplay.NORMAL) {
            return Integer.toString(amount);
        }
        char[] digits = display == AmountDisplay.SUPERSCRIPT ? SUPERSCRIPT : SUBSCRIPT;
        String value = Integer.toString(Math.abs(amount));
        StringBuilder builder = new StringBuilder(value.length() + (amount < 0 ? 1 : 0));
        if (amount < 0) {
            builder.append(display == AmountDisplay.SUPERSCRIPT ? '⁻' : '₋');
        }
        for (int i = 0; i < value.length(); i++) {
            builder.append(digits[value.charAt(i) - '0']);
        }
        return builder.toString();
    }
}
