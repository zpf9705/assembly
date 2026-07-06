
package top.osjf.commons.lang;

import top.osjf.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class was copied from {@code cn.hutool.core.lang}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 *
 * <p>Simple usage example:</p>
 * <pre>{@code
 *  TableText table = TableText.create()
 *          // Add table header row
 *          .addHeader("Task ID", "expression", "Is it running")
 *          // Add two data rows
 *          .addBody("2074044199956606976", "0 0/10 * * * ?", "false")
 *          .addBody("2074044199956606977", "0 0/2 * * * ?", "true");
 *  // Call toString() to get complete table string and print
 *  System.out.println(table);
 *  }</pre>
 *
 * <p>Render output result after calling toString()：</p>
 * <pre>
 * +-----------------------+-----------------+------------+
 * ｜　Task ID　　　　　　　｜　expression　　　　｜　Is it running　｜
 * +-----------------------+-----------------+------------+
 * ｜　2074044199956606976　｜　0 0/10 * * * ?｜　false　　 ｜
 * ｜　2074044199956606977　｜　0 0/2 * * * ? ｜　true　　　｜
 * +-----------------------+-----------------+------------+
 * </pre>
 */
public class TableText {

    private static final char ROW_LINE = '－';
    private static final char COLUMN_LINE = '|';
    private static final char CORNER = '+';
    private static final char SPACE = '\u3000';
    private static final char LF = '\n';


    private boolean isSBCMode = true;

    private TableText() {}

    public static TableText create() {
        return new TableText();
    }

    private final List<List<String>> headerList = new ArrayList<>();

    private final List<List<String>> bodyList = new ArrayList<>();

    private List<Integer> columnCharNumber;

    public TableText setSBCMode(boolean isSBCMode) {
        this.isSBCMode = isSBCMode;
        return this;
    }

    public TableText addHeader(String... titles) {
        if (columnCharNumber == null) {
            columnCharNumber = new ArrayList<>(Collections.nCopies(titles.length, 0));
        }
        List<String> l = new ArrayList<>();
        fillColumns(l, titles);
        headerList.add(l);
        return this;
    }

    public TableText addBody(String... values) {
        List<String> l = new ArrayList<>();
        bodyList.add(l);
        fillColumns(l, values);
        return this;
    }

    private void fillColumns(List<String> l, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            String column = String.valueOf(columns[i]);
            if (isSBCMode) {
                column = toSBC(column);
            }
            l.add(column);
            int width = column.length();
            if (width > columnCharNumber.get(i)) {
                columnCharNumber.set(i, width);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        fillBorder(sb);
        fillRows(sb, headerList);
        fillBorder(sb);
        fillRows(sb, bodyList);
        fillBorder(sb);
        return sb.toString();
    }

    private void fillRows(StringBuilder sb, List<List<String>> list) {
        for (List<String> row : list) {
            sb.append(COLUMN_LINE);
            fillRow(sb, row);
            sb.append(LF);
        }
    }

    private void fillRow(StringBuilder sb, List<String> row) {
        final int size = row.size();
        String value;
        for (int i = 0; i < size; i++) {
            value = row.get(i);
            sb.append(SPACE);
            sb.append(value);
            final int length = value.length();
            final int sbcCount = sbcCount(value);
            if(sbcCount % 2 == 1){
                sb.append(' ');
            }
            sb.append(SPACE);
            int maxLength = columnCharNumber.get(i);
            for (int j = 0; j < (maxLength - length + (sbcCount / 2)); j++) {
                sb.append(SPACE);
            }
            sb.append(COLUMN_LINE);
        }
    }

    private void fillBorder(StringBuilder sb) {
        sb.append(CORNER);
        for (Integer width : columnCharNumber) {
            sb.append(repeat(width + 2));
            sb.append(CORNER);
        }
        sb.append(LF);
    }

    private String repeat(int count) {
        if (count <= 0) {
            return "";
        }
        char[] result = new char[count];
        Arrays.fill(result, ROW_LINE);
        return new String(result);
    }

    private String toSBC(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        final char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    private int sbcCount(String value) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) < '\177') {
                count++;
            }
        }
        return count;
    }
}
