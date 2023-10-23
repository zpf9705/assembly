package top.osjf.assembly.util.entity;

import java.util.List;


/**
 * Tree node subclass obtains interface.
 *
 * @author zpf
 * @since 1.0.4
 */
public interface TreeData<T extends TreeData<T>> {

    List<T> getChildren();
}
