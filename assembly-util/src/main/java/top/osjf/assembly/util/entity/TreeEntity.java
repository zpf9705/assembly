package top.osjf.assembly.util.entity;


/**
 * Obtain the single data ID and parent class ID that are necessary
 * for building a tree structure.
 *
 * <pre>
 *  public class SysMenu implements Serializable, TreeEntity{
 *     private String menuId;
 *     private String parentId;
 *     &#064;Override
 *     public String getId() {
 *         return menuId;
 *     }
 *     &#064;Override
 *     public String getParentId() {
 *         return parentId;
 *     }
 *  }
 * </pre>
 * @see TreeData
 * @see EntityTree
 * @see EntityConsumer
 * @see Node
 * @author zpf
 * @since 1.0.4
 */
public interface TreeEntity {

    String getId();

    String getParentId();
}
