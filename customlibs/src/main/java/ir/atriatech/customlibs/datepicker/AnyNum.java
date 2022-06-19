package ir.atriatech.customlibs.datepicker;

public final class AnyNum {
    /* ARABIC */

    /**
     * This method converts any English number to Arabic in a string
     *
     * @param text A string including English numbers
     * @return Converted string including Arabic numbers
     */
    public static String enToAr(String text) {
        try {
            char[] arabicChars = {'\u0660', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669'};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i)))
                    builder.append(arabicChars[(int) (text.charAt(i)) - 48]);
                else
                    builder.append(text.charAt(i));
            }
            return builder.toString();

        } catch (Exception ignored) {
            return safeEnToAr(text);
        }
    }

    /**
     * This method converts any English number to Arabic in a string
     *
     * @param onlyNumber A string including ONLY English numbers
     * @return Converted string including Arabic numbers
     */
    public static String enNumToAr(String onlyNumber) {
        try {
            char[] arabicChars = {'\u0660', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669'};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < onlyNumber.length(); i++)
                builder.append(arabicChars[(int) (onlyNumber.charAt(i)) - 48]);

            return builder.toString();

        } catch (Exception ignored) {
            return safeEnToAr(onlyNumber);
        }
    }

    /**
     * This method converts any English/Persian number to Arabic in a string
     *
     * @param text A string including English/Persian numbers
     * @return Converted string including Arabic numbers
     */
    public static String anyToAr(String text) {
        return
                //English to Arabic
                safeEnToAr(text)
                        //Persian to Arabic (same character checks have been removed)
                        .replace("\u06f0", "\u0660")
                        .replace("\u06f1", "\u0661")
                        .replace("\u06f2", "\u0662")
                        .replace("\u06f4", "\u0664")
                        .replace("\u06f5", "\u0665")
                        .replace("\u06f6", "\u0666")
                        .replace("\u06f7", "\u0667")
                        .replace("\u06f8", "\u0668");
    }

    /* PERSIAN */

    /**
     * This method converts any English number to Persian in a string
     *
     * @param text A string including English numbers
     * @return Converted string including Persian numbers
     */
    public static String enToFa(String text) {
        try {
            char[] persianChars = {'\u06f0', '\u06f1', '\u06f2', '\u0663', '\u06f4', '\u06f5', '\u06f6', '\u06f7', '\u06f8', '\u0669'};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i)))
                    builder.append(persianChars[(int) (text.charAt(i)) - 48]);
                else
                    builder.append(text.charAt(i));
            }
            return builder.toString();

        } catch (Exception ignored) {
            return safeEnToFa(text);
        }
    }

    /**
     * This method converts any English number to Persian in a string
     *
     * @param onlyNumber A string including ONLY English numbers
     * @return Converted string including Persian numbers
     */
    public static String enNumToFa(String onlyNumber) {
        try {
            char[] persianChars = {'\u06f0', '\u06f1', '\u06f2', '\u0663', '\u06f4', '\u06f5', '\u06f6', '\u06f7', '\u06f8', '\u0669'};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < onlyNumber.length(); i++)
                builder.append(persianChars[(int) (onlyNumber.charAt(i)) - 48]);

            return builder.toString();

        } catch (Exception ignored) {
            return safeEnToFa(onlyNumber);
        }
    }

    /**
     * This method converts any English/Arabic number to Persian in a string
     *
     * @param text A string including English/Arabic numbers
     * @return Converted string including Persian numbers
     */
    public static String anyToFa(String text) {
        return
                //English to Persian
                safeEnToFa(text)
                        //Arabic to Persian (same character checks have been removed)
                        .replace("\u0660", "\u06f0")
                        .replace("\u0661", "\u06f1")
                        .replace("\u0662", "\u06f2")
                        .replace("\u0664", "\u06f4")
                        .replace("\u0665", "\u06f5")
                        .replace("\u0666", "\u06f6")
                        .replace("\u0667", "\u06f7")
                        .replace("\u0668", "\u06f8");
    }

    /* PRIVATE METHODS */
    private static String safeEnToAr(String text) {
        return text
                .replace("0", "\u0660")
                .replace("1", "\u0661")
                .replace("2", "\u0662")
                .replace("3", "\u0663")
                .replace("4", "\u0664")
                .replace("5", "\u0665")
                .replace("6", "\u0666")
                .replace("7", "\u0667")
                .replace("8", "\u0668")
                .replace("9", "\u0669");
    }

    private static String safeEnToFa(String text) {
        return text
                .replace("0", "\u06f0")
                .replace("1", "\u06f1")
                .replace("2", "\u06f2")
                .replace("3", "\u0663")
                .replace("4", "\u06f4")
                .replace("5", "\u06f5")
                .replace("6", "\u06f6")
                .replace("7", "\u06f7")
                .replace("8", "\u06f8")
                .replace("9", "\u0669");
    }
}
