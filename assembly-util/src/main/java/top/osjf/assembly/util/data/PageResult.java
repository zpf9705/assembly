package top.osjf.assembly.util.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


/**
 * Suggested pagination result encapsulation.
 * @param <T> The type of result set item
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class PageResult<T> implements Serializable, PageAfter<T> {

    private static final long serialVersionUID = -8145365562456292127L;

    /**
     * Number of pages
     */
    private Long pageNum;

    /**
     * Number of pieces
     */
    private Long pageSize;

    /**
     * Data collection
     */
    private List<T> list = Collections.emptyList();

    /**
     * Total number of articles
     */
    private Long total = 0L;

    public PageResult() {
    }

    public PageResult(Long pageNum, Long pageSize) {
        this(pageNum, pageSize, null, null);
    }

    public PageResult(Long pageNum, Long pageSize, List<T> list, Long total) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.total = total;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public PageResult<T> after(Consumer<List<T>> consumer) {
        consumer.accept(this.list);
        return this;
    }
}
