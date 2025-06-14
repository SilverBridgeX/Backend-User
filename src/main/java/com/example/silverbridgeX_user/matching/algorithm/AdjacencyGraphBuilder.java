package com.example.silverbridgeX_user.matching.algorithm;

import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.user.domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdjacencyGraphBuilder {

    public static List<Integer>[] buildAdjacencyGraph(List<MatchRequest> matchRequests) {
        int n = matchRequests.size();
        List<Integer>[] graph = new ArrayList[n + 1];

        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (canConnect(matchRequests.get(i - 1), matchRequests.get(j - 1))) {
                    graph[i].add(j);
                    graph[j].add(i);
                }
            }
        }

        return graph;
    }

    private static boolean canConnect(MatchRequest a, MatchRequest b) {
        User A = a.getUser();
        User B = b.getUser();

//        String sexA = A.getSex();
//        String sexB = B.getSex();
//
//        if (sexA == null || sexB == null) return false;
//
//        Double latA = Double.parseDouble(A.getLatitude());
//        Double lonA = Double.parseDouble(A.getLongitude());
//        Double latB = Double.parseDouble(B.getLatitude());
//        Double lonB = Double.parseDouble(B.getLongitude());
//
//        if (latA == null || lonA == null || latB == null || lonB == null) return false;
//
//        LocalDate birthA = A.getBirth();
//        LocalDate birthB = B.getBirth();
//
//        if (birthA == null || birthB == null) return false;
//
//        return !sexA.equals(sexB) &&
//                GeoUtils.haversine(latA, lonA, latB, lonB) <= 3.0 &&
//                Math.abs(birthA.getYear() - birthB.getYear()) <= 10;

        String latAStr = A.getLatitude();
        String lonAStr = A.getLongitude();
        String latBStr = B.getLatitude();
        String lonBStr = B.getLongitude();

        if (latAStr == null || lonAStr == null || latBStr == null || lonBStr == null) {
            return false;
        }

        try {
            double latA = Double.parseDouble(latAStr);
            double lonA = Double.parseDouble(lonAStr);
            double latB = Double.parseDouble(latBStr);
            double lonB = Double.parseDouble(lonBStr);

            return GeoUtils.haversine(latA, lonA, latB, lonB) <= 3.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
