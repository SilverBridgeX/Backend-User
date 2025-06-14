package com.example.silverbridgeX_user.matching.algorithm;

import java.util.List;

public class BipartiteMatcher {
    private final List<Integer>[] adj;
    private final Integer[] matching;      // match[v] = u
    private boolean[] visited;

    public BipartiteMatcher(List<Integer>[] adj) {
        this.adj = adj;
        this.matching = new Integer[adj.length]; // 1-based index
    }

    public int run() {
        int res = 0;
        for (int u = 1; u < adj.length; u++) {
            visited = new boolean[adj.length];
            if (dfs(u)) {
                res++;
            }
        }
        return res;
    }

    private boolean dfs(int u) {
        for (int v : adj[u]) {
            if (visited[v]) continue;
            visited[v] = true;

            if (matching[v] == 0 || dfs(matching[v])) {
                matching[v] = u;
                return true;
            }
        }
        return false;
    }

    public Integer[] getMatching() {
        return matching;
    }
}
