package learn.data.structure.tree;

import java.util.Arrays;

/**
 * 二叉堆的构建与调整.
 *
 * 源码来自《漫画算法》，侵删
 */
public class BinaryHeap {

    /**
     * 上浮调整
     */
    public static void upAdjust(int[] array) {
        int childIndex = array.length -1;
        int parentIndex = (childIndex - 1) / 2;
        // temp 保存插入的叶子节点值，用于最后的赋值
        int temp = array[childIndex];
        while (childIndex > 0 && temp < array[parentIndex]) {
            // 无须真正交换，单向赋值即可
            array[childIndex] = array[parentIndex];
            childIndex = parentIndex;
            parentIndex = (parentIndex - 1) / 2;
        }
        array[childIndex] = temp;
    }

    /**
     * 下沉调整
     */
    public static void downAdjust(int[] array, int parentIndex, int length) {
        // temp 保存父节点的值，用于最后的赋值
        int temp = array[parentIndex];
        int childIndex = 2 * parentIndex + 1;
        while (childIndex < length) {
            // 如果有右孩子，而且右孩子小于左孩子的值，则定位到右孩子
            if (childIndex + 1 < length && array[childIndex + 1] < array[childIndex]) {
                childIndex++;
            }
            // 如果父节点小于任何一个孩子的值，则直接跳出
            if (temp <= array[childIndex]) {
                break;
            }
            // 无须真正的交换，单向赋值即可
            array[parentIndex] = array[childIndex];
            parentIndex = childIndex;
            childIndex = 2 * childIndex + 1;
        }
        array[parentIndex] = temp;
    }

    /**
     * 构建堆
     */
    public static void buildHeap(int[] array) {
        // 从最后一个非叶子节点开始，依次做「下沉」调整
        for (int i = (array.length - 2) / 2; i >= 0; i --) {
            downAdjust(array, i, array.length);
        }
    }

    public static void main(String[] args) {
        int[] array = new int[] {1, 3, 2, 6, 5, 7, 8, 9, 10, 0};
        upAdjust(array);
        System.out.println(Arrays.toString(array));
        array = new int[] {7, 1, 3, 10, 5, 2, 8, 9, 6};
        buildHeap(array);
        System.out.println(Arrays.toString(array));
    }
}
