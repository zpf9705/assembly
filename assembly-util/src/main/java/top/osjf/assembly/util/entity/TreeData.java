package top.osjf.assembly.util.entity;

import java.util.List;


/**
 * Tree node subclass obtains interface.
 *
 * <pre>
 *  public class ResourceVO implements TreeData&lt;ResourceVO&gt;, Serializable{
 *     private List&lt;ResourceVO&gt; children = new ArrayList&lt;&gt;();
 *     &#064;Override
 *     public List&lt;ResourceVO&gt; getChildren() {
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
