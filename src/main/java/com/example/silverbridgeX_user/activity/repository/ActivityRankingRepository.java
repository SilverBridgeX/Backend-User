package com.example.silverbridgeX_user.activity.repository;

import com.example.silverbridgeX_user.activity.domain.ActivityRanking;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActivityRankingRepository extends JpaRepository<ActivityRanking, Long> {
    /**
     * 사용자 위치로부터 10 km 이내 인기 rank 상위 N 반환
     */
    @Query(value = """
                SELECT ar.activity_id
                FROM activity_ranking ar
                JOIN activity a ON a.id = ar.activity_id
                WHERE earth_distance(
                        ll_to_earth(CAST(:lat AS float), CAST(:lon AS float)),
                        ll_to_earth(CAST(a.latitude AS float), CAST(a.longitude AS float))
                      ) < 10000
                  AND ar.activity_id NOT IN (:excluded)
                ORDER BY ar.rank ASC
                LIMIT :limit
            """, nativeQuery = true)
    List<Long> fetchTopByDistance(double lat, double lon,
                                  int limit, Collection<Long> excluded);

}
