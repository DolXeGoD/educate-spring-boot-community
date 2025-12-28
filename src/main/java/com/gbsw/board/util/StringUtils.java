package com.gbsw.board.util;

public class StringUtils {
    public static String safeTruncate(String content, int maxLength) {
        if (content == null) return "";
        return content.length() <= maxLength ? content : content.substring(0, maxLength) + "...";
    }
}
