package top.osjf.generated;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * Regarding the abstract public logic management of {@link GeneratedSourceAllocation},
 * the iterator logic for sharing grouping resources {@link GroupSourceEntry}
 * in this class uses {@link List}'s logic.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
public abstract class AbstractGeneratedSourceAllocation implements GeneratedSourceAllocation {

    private Iterator<GroupSourceEntry> iterator;

    /**
     * Set up a grouping resource array.
     * @param entries Grouping resource array.
     */
    protected void setEntries(@NotNull List<GroupSourceEntry> entries) {
        this.iterator = entries.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GeneratedSourceAllocation.GroupSourceEntry next() {
        return iterator.next();
    }
}
