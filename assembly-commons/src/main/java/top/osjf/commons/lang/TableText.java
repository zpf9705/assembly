
package top.osjf.commons.lang;

import top.osjf.commons.util.Assert;
import top.osjf.commons.util.ReflectionUtils;
import top.osjf.commons.util.StringUtils;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
 *
 * <p>2. Auto generate table via entity annotation {@link Header} (recommended):
 * <pre>{@code
 *  // Step1: Define entity with @Header annotation
 *  public class Task {
 *      {@literal @}Header(value = "Task ID", order = 1)
 *      private String id;
 *      {@literal @}Header(value = "Cron Expression", order = 2)
 *      private String expression;
 *      {@literal @}Header(value = "Running State", order = 3)
 *      private boolean running;
 *      // getter & setter
 *  }
 *
 *  // Step2: Auto build table from List<Task>
 *  List&lt;Task&gt; taskList = new ArrayList&lt;&gt;();
 *  TableText table = TableText.toTableText(taskList, Task.class);
 *  System.out.println(table);
 *  }</pre>
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

    /**
     * Annotation for entity field, mark table header display name and column sort order.
     * Only fields annotated by this will be parsed when auto-generate table via {@link #toTableText(List, Class)}.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Header {
        /**
         * Table header display text of current field
         */
        String value();

        /**
         * Column sort order, fields will be sorted ascending by this value when generating table
         */
        int order();
    }

    /**
     * Convert single entity object to formatted {@code TableText}.
     * Simply wrap single object as list and delegate to {@link #toTableText(List, Class)}.
     * @param obj entity data to render.
     * @param clazz entity class with {@link Header} annotation on fields
     * @return complete formatted {@code TableText} instance
     * @param <T> generic type of entity
     */
    public static <T>TableText toTableText(T obj, Class<T> clazz) {
        return toTableText(Collections.singletonList(obj), clazz);
    }

    /**
     * Auto build TableText from entity list, parse header and column order via {@link Header} annotation
     * on entity field.
     * <p>
     * <strong>Logic flow:</strong>
     * <ol>
     * <li>Scan all declared fields of clazz, filter fields with {@link Header};</li>
     * <li>Sort fields ascending by annotation {@link Header#order()};</li>
     * <li>Extract {@link Header#value()} as table header row;</li>
     * <li>Traverse entity list, read each field value as table data row.</li>
     * </ol>
     * @param objects entity data list to render
     * @param clazz entity class with {@link Header} annotation on fields
     * @param <T> generic type of entity
     * @return complete formatted {@code TableText} instance
     * @throws IllegalArgumentException If invalid parameters are entered.
     */
    public static <T>TableText toTableText(List<T> objects, Class<T> clazz) {

        Assert.notEmpty(objects, "Input objects not be empty");
        Assert.notNull(clazz, "Input class not be null");

        TableText tableText = TableText.create();
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Header.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Header.class).order()))
                .collect(Collectors.toList());
        if (fields.isEmpty()) {
            return tableText;
        }
        tableText.addHeader
                (fields.stream().map(field -> field.getAnnotation(Header.class).value()).toArray(String[]::new));
        for (T object : objects) {
            tableText.addBody
                    (fields.stream().map(field -> {
                        ReflectionUtils.makeAccessible(field);
                        return String.valueOf(ReflectionUtils.getField(field, object));
                    }).toArray(String[]::new));
        }
        return tableText;
    }
}
