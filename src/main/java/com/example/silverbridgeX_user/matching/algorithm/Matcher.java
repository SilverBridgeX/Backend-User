package com.example.silverbridgeX_user.matching.algorithm;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Matcher {

    private final List<Integer>[] adj;
    private final Integer[] matching;
    private final Integer[] parent;
    private final Integer[] color;
    private final Integer[] visited;
    private final Integer[] group;

    public Matcher(List<Integer>[] adj) {
        this.adj = adj;
        this.matching = new Integer[adj.length];
        this.parent = new Integer[adj.length];
        this.color = new Integer[adj.length];
        this.visited = new Integer[adj.length];
        this.group = new Integer[adj.length];
    }

    public int run() {
        int res = 0;
        for (int i = 1; i < adj.length; i++) {
            matching[i] = 0;
        }

        for (int i = 1; i < adj.length; i++) {
            if (matching[i] == 0) {
                if (findAugmentPath(i)) {
                    res++;
                }
            }
        }

        return res;
    }

    private boolean findAugmentPath(int r) {
        for (int i = 1; i < adj.length; i++) {
            parent[i] = 0;
            color[i] = -1;
            visited[i] = 0;
            group[i] = i;
        }

        Queue<Integer> q = new ArrayDeque<>();

        color[r] = 0;
        visited[r] = 1;
        q.add(r);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : adj[u]) {
                if (color[v] == -1) {
                    parent[v] = u;
                    color[v] = 1;

                    if (matching[v] == 0) {
                        flipAugmentPath(r, v);
                        return true;
                    }

                    color[matching[v]] = 0;
                    visited[matching[v]] = 1;
                    q.add(matching[v]);
                }
                else if (color[v] == 0 && !group[u].equals(group[v])) {
                    int p = lca(group[r], group[u], group[v]);

                    group_blossom(p, u, v);
                    group_blossom(p, v, u);

                    for (int i = 1; i < adj.length; i++) {
                        if (visited[i] != 0 && color[i] != 0) {
                            color[i] = 0;
                            q.add(i);
                        }
                    }
                }
            }
        }
        return false;
    }

    private void flipAugmentPath(int root, int u) {
        while (parent[u] != root) {
            int v = parent[u];
            int w = matching[v];

            matching[u] = v;
            matching[v] = u;

            matching[w] = 0;
            u = w;
        }
        matching[u] = root;
        matching[root] = u;
    }

    private int lca(int root, int u, int v) {
        Integer[] check = new Integer[adj.length];
        for (int i = 1; i < adj.length; i++) {
            check[i] = 0;
        }

        while (u != root) {
            check[u] = 1;
            u = group[parent[matching[u]]];
        }

        while (v != root) {
            if (check[v] != 0) return v;
            v = group[parent[matching[v]]];
        }

        return root;
    }

    private void group_blossom(int p, int u, int v) {
        while (group[u] != p) {
            int nv = matching[u];
            int nu = parent[nv];
            if (visited[nv] == 0) {
                visited[nv] = 1;
            }

            parent[u] = v;
            group[u] = p;
            group[nv] = p;
            u = nu;
            v = nv;
        }
    }

    public Integer[] getMatching() {
        return matching;
    }
}
