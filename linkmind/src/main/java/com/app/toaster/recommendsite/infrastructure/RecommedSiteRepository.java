package com.app.toaster.recommendsite.infrastructure;


import com.app.toaster.recommendsite.domain.RecommendSite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommedSiteRepository extends JpaRepository<RecommendSite, Long> {

}
