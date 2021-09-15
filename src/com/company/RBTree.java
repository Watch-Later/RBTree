package com.company;

/**
 * 红黑树
 *
 * @param <K> Key
 * @param <V> Value
 */
@SuppressWarnings("all")
public class RBTree<K extends Comparable<K>, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private RBNode root;

    public RBNode getRoot() {
        return root;
    }

    /**
     * 获取当前节点父节点
     *
     * @param node 当前节点
     * @return 父节点
     */
    private RBNode parentOf(RBNode node) {
        if (node != null) {
            return node.parent;
        }
        return null;
    }

    /**
     * 节点是否为红色
     *
     * @param node 节点
     * @return bool
     */
    private boolean isRed(RBNode node) {
        if (node != null) {
            return node.color == RED;
        }
        return false;
    }

    /**
     * 节点是否为黑色
     *
     * @param node 节点
     * @return bool
     */
    private boolean isBlack(RBNode node) {
        if (node != null) {
            return node.color == BLACK;
        }
        return false;
    }

    /**
     * 设置节点为红色
     *
     * @param node 节点
     */
    private void setRed(RBNode node) {
        if (node != null) {
            node.color = RED;
        }
    }

    /**
     * 设置节点为黑色
     *
     * @param node 节点
     */
    private void setBlack(RBNode node) {
        if (node != null) {
            node.color = BLACK;
        }
    }

    /**
     * 中序遍历
     */
    public void inOrderPrint() {
        inOrderPrint(this.root);
    }

    /**
     * 中序遍历
     *
     * @param root 根节点
     */
    private void inOrderPrint(RBNode root) {
        if (root != null) {
            inOrderPrint(root.left);
            System.out.println("key:" + root.key + "-value:" + root.value);
            inOrderPrint(root.right);
        }
    }

    /**
     * 左旋
     *      p              p
     *      |              |
     *      x              y
     *     / \   --->     / \
     *    lx  y          x   ry
     *   / \            / \
     *  ly ry          lx ly
     *
     * (1) x.right=y.left
     * (2) y.left.parent=x
     * (3) y.parent=x.parent
     * (4) x.parent=y
     *
     * @param x 左旋节点
     */
    private void leftRotate(RBNode x) {
        RBNode y = x.right;
        // (1)
        x.right = y.left;
        // (2)
        if (y.left != null) {
            y.left.parent = x;
        }
        // (3)
        if (x.parent != null) {
            y.parent = x.parent;
            if (x == x.parent.left) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        } else {
            // root
            this.root = y;
            this.root.parent = null;
        }
        // (4)
        x.parent = y;
        y.left = x;
    }

    /**
     * 右旋
     *      p              p
     *      |              |
     *      y              x
     *     / \   --->     / \
     *    x  ry          lx  y
     *   / \                / \
     *  lx ly              ly ry
     *
     * (1) y.left=x.right
     * (2) x.right.parent=y
     * (3) x.parent=y.parent
     * (4) y.parent=x
     *
     * @param y 右旋节点
     */
    private void rightRotate(RBNode y) {
        RBNode x = y.left;
        // (1)
        y.left = x.right;
        // (2)
        if (x.right != null) {
            x.right.parent = y;
        }
        // (3)
        if (y.parent != null) {
            x.parent = y.parent;
            if (y == y.parent.left) {
                y.parent.left = x;
            } else {
                y.parent.right = x;
            }
        } else {
            // root
            this.root = x;
            this.root.parent = null;
        }
        // (4)
        y.parent = x;
        x.right = y;
    }

    /**
     * 对外的插入
     * @param key key
     * @param value value
     */
    public void insert(K key, V value) {
        RBNode node = new RBNode();
        node.setKey(key);
        node.setValue(value);
        // 新节点一定是红色
        node.setColor(RED);
        insert(node);
    }

    /**
     * 插入实现
     * @param node 插入节点
     */
    private void insert(RBNode node) {
        RBNode parent = null;
        RBNode x = this.root;

        while (x != null) {
            parent = x;
            // cmp>0:node.key>x.key->遍历右子树
            // cmp=0:node.key=x.key->replace
            // cmp<0:node.key<x.key->遍历左子树
            int cmp = node.key.compareTo(x.key);
            if (cmp > 0) {
                x = x.right;
            } else if (cmp == 0) {
                x.setValue(node.getValue());
                return;
            } else {
                x = x.left;
            }
        }
        node.parent = parent;
        if (parent != null) {
            // cmp>0:node.key>parent.key->node在parent右子节点
            // cmp<0:node.key<parent.key->node在parent左子节点
            int cmp = node.key.compareTo(parent.key);
            if (cmp > 0) {
                parent.right = node;
            } else {
                parent.left = node;
            }
        } else {
            this.root = node;
        }

        // 恢复平衡
        insertFixup(node);
    }

    /**
     * 恢复平衡
     */
    private void insertFixup(RBNode node) {
        this.root.setColor(BLACK);
        // 父节点
        RBNode parent = parentOf(node);
        // 父节点的父节点
        RBNode gparent = parentOf(parent);
        // 父节点为黑色直接平衡
        // 父节点为红色应分多种情况
        if (parent != null && isRed(parent)) {
            // 父节点的父节点的另一个子节点（叔叔）
            RBNode uncle = null;
            // 父节点是爷节点左节点
            if (parent == gparent.left) {
                uncle = gparent.right;
                /**
                 *         |                      |
                 *      gp(B)                   gp(R)
                 *      /   \      --->         /   \
                 *    p(R)  u(R)              p(B)  u(B)
                 *    /                       /
                 *  n(R)                    n(R)
                 */
                if (uncle != null && isRed(uncle)) {
                    // 重新染色后下一轮处理
                    setBlack(parent);
                    setBlack(uncle);
                    setRed(gparent);
                    insertFixup(gparent);
                    return;
                }
                if (uncle == null || isBlack(uncle)) {
                    /**
                     *         |                      |
                     *      gp(B)                    p(B)
                     *      /   \      --->         /   \
                     *    p(R)  u(B)              n(R)  gp(R)
                     *    /                               \
                     *  n(R)                              u(B)
                     */
                    if (node == parent.left) {
                        setBlack(parent);
                        setRed(gparent);
                        // 右旋
                        rightRotate(gparent);
                        return;
                    }
                    /**
                     *         |                      |
                     *      gp(B)                    gp(B)
                     *      /   \      --->         /   \       ---> 上一步
                     *    p(R)  u(B)              n(R)  u(B)
                     *       \                    /
                     *       n(R)               p(R)
                     */
                    if (node == parent.right) {
                        leftRotate(parent);
                        insertFixup(parent);
                        // 右旋
                        return;
                    }
                }
            } else {
                // 父节点是右节点
                uncle = gparent.left;
                /**
                 *         |                      |
                 *      gp(B)                   gp(R)
                 *      /   \      --->         /   \
                 *    u(R)  p(R)              u(B)  p(B)
                 *    /                       /
                 *  n(R)                    n(R)
                 */
                if (uncle != null && isRed(uncle)) {
                    // 重新染色后下一轮处理
                    setBlack(parent);
                    setBlack(uncle);
                    setRed(gparent);
                    insertFixup(gparent);
                    return;
                }
                // 不存在叔节点
                if (uncle == null || isBlack(uncle)) {
                    /**
                     *         |                      |
                     *      gp(B)                    p(B)
                     *      /   \      --->         /   \
                     *    u(B)  p(R)              gp(R) n(R)
                     *            \               /
                     *            n(R)          u(R)
                     */
                    if (node == parent.right) {
                        setBlack(parent);
                        setRed(gparent);
                        leftRotate(gparent);
                        return;
                    }
                    /**
                     *         |                      |
                     *      gp(B)                    gp(B)
                     *      /   \      --->         /   \     ---> 上一步
                     *    u(B)  p(R)              u(B)  n(R)
                     *          /                         \
                     *        n(R)                        p(R)
                     */
                    if (node == parent.left) {
                        rightRotate(parent);
                        insertFixup(parent);
                        return;
                    }
                }
            }
        }
    }

    static class RBNode<K extends Comparable<K>, V> {
        private RBNode parent;
        private RBNode left;
        private RBNode right;
        private boolean color;

        private K key;
        private V value;

        public RBNode() {
        }

        public RBNode(RBNode parent, RBNode left, RBNode right, boolean color, K key, V value) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.color = color;
            this.key = key;
            this.value = value;
        }

        public RBNode getParent() {
            return parent;
        }

        public void setParent(RBNode parent) {
            this.parent = parent;
        }

        public RBNode getLeft() {
            return left;
        }

        public void setLeft(RBNode left) {
            this.left = left;
        }

        public RBNode getRight() {
            return right;
        }

        public void setRight(RBNode right) {
            this.right = right;
        }

        public boolean isColor() {
            return color;
        }

        public void setColor(boolean color) {
            this.color = color;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
