package com.panduoma.cwhospital.common;

import java.util.Scanner;

public class Solution {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * @param array int整型一维数组
     * @return int整型
     */
    Scanner sc = new Scanner(System.in);


    public int FindGreatestSumOfSubArray(int[] array) {
        // write code here
        int sum = 0;
        int count = 0;
        int[] arr = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 0) {
                sum += array[i];
            }
        }
        if (count != 0) return sum;
        else {
            int max = Integer.MAX_VALUE;
            return max;
        }


    }
}