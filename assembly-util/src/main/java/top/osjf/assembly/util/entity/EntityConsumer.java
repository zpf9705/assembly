package top.osjf.assembly.util.entity;

import java.util.function.Consumer;

/**
 * Entity Attribute building consumers.
 *
 * @author zpf
 * @since 1.0.4
 */
public interface EntityConsumer<T extends TreeEntity, E extends TreeData<E>> extends Consumer<Node<T, E>> {

    @Override
    void accept(Node<T, E> teNode);

    E toTreeData();
}
