package top.osjf.assembly.util.entity;

import top.osjf.assembly.util.lang.Asserts;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Functional tree structure abstract class.
 * <p>Suitable for displaying and constructing tree menu structures,
 * such as menu business, department business, etc.
 *
 * @author zpf
 * @since 1.0.4
 */
public abstract class EntityTree<T extends TreeEntity, E extends TreeData<E>> implements EntityConsumer<T, E> {

    private final Map<String, Node<T, E>> nodes;

    private Node<T, E> root;

    public EntityTree(Collection<T> data) {
        Asserts.notEmpty(data, "The tree structure construction data cannot be empty.");
        this.nodes = data.stream().collect(Collectors.toMap(TreeEntity::getId,
                Node::new,
                (s1, s2) -> s1,
                LinkedHashMap::new));
        //Process nodes.
        this.nodes.values().forEach(this);
        //Check the data of the highest node.
        Asserts.notNull(this.root, "Error building tree data, missing root node.");
        Asserts.isNull(this.root.getParentId(),
                "Error building tree data, incorrect root node data.");
    }

    @Override
    public void accept(Node<T, E> node) {
        Node<T, E> parent = this.nodes.get(node.getParentId());
        if (parent == null) {
            if (this.root != null) {
                throw new IllegalArgumentException("Tree node error, multiple leaf nodes " +
                        "without parent nodes were found.");
            }
            this.root = node;
        } else {
            parent.addChild(node);
        }
    }

    @Override
    public E toTreeData() {
        E tree;
        if (this.root != null) {
            tree = this.root.toTreeData(getConverter());
        } else {
            tree = null;
        }
        return tree;
    }

    /**
     * Obtain an assignment transformation method for tree shaped data.
     * @return {@link Function} types to return.
     */
    public abstract Function<T, E> getConverter();
}
