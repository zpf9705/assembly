package top.osjf.assembly.util.entity;

import java.util.List;


/**
 * Tree node subclass obtains interface.
 *
 * <pre>
 *  public class ResourceVO implements TreeData<ResourceVO>, Serializable{
 *     private List<ResourceVO> children = new ArrayList<>();
 *     &#064;Override
 *     public List<ResourceVO> getChildren() {
 *         return children;
 *     }
 *  }
 * </pre>
 * @see TreeEntity
 * @see EntityTree
 * @see EntityConsumer
 * @see Node
 * @author zpf
 * @since 1.0.4
 */
public interface TreeData<T extends TreeData<T>> {

    List<T> getChildren();
}
