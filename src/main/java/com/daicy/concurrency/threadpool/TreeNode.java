package com.daicy.concurrency.threadpool;

import com.google.common.collect.Sets;

import java.util.Set;

public class TreeNode {

    int value;

    Set<TreeNode> children;

    public TreeNode(int value, TreeNode... children) {
        this.value = value;
        this.children = Sets.newHashSet(children);
    }

}
