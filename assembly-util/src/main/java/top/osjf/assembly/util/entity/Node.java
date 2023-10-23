package top.osjf.assembly.util.entity;

import top.osjf.assembly.util.lang.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A tree structured leaf node data model that records the parent and
 * child information of the current node.
 *
 * @see TreeEntity
 * @see EntityTree
 * @see EntityConsumer
 * @see TreeData
 * @author zpf
 * @since 1.0.4
 */
public class Node<T extends TreeEntity, E extends TreeData<E>> implements Serializable {

    private static final long serialVersionUID = -6654220311647846013L;

    private final String parentId;

    public String getParentId() {
        return parentId;
    }

    private final T data;

    private final List<Node<T, E>> children = new ArrayList<>();

    public Node(T data) {
        this.data = data;
        this.parentId = data.getParentId();
    }

    public void addChild(Node<T, E> other) {
        children.add(other);
    }

    public E toTreeData(Function<T, E> converter) {
        //Process Basic Fields
        E leaf = converter.apply(this.data);
        //Add Subclass Menu
        if (CollectionUtils.isNotEmpty(this.children)) {
            this.children.forEach(node -> leaf.getChildren().add(node.toTreeData(converter)));
        }
        return leaf;
    }
}
