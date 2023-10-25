package top.osjf.assembly.util.entity;

import java.util.function.Consumer;

/**
 * Entity Attribute building consumers.
 *
 * @see TreeEntity
 * @see Node
 * @see EntityTree
 * @see TreeData
 * @param <T> The type of tree structured data.
 * @param <E> Subclass types of tree structure components.
 * @author zpf
 * @since 1.0.4
 */
public interface EntityConsumer<T extends TreeEntity, E extends TreeData<E>> extends Consumer<Node<T, E>> {

    @Override
    void accept(Node<T, E> teNode);

    /**
     * Convert tree data ,the specific structure satisfies {@link TreeData}.
     * @return Object types.
     */
    E toTreeData();
}
