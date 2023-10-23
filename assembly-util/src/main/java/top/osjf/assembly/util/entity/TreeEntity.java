package top.osjf.assembly.util.entity;


/**
 * Obtain the single data ID and parent class ID that are necessary
 * for building a tree structure.
 *
 * @author zpf
 * @since 1.0.4
 */
public interface TreeEntity {

    String getId();

    String getParentId();
}
