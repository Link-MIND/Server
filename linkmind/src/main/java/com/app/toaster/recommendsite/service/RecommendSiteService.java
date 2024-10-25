package com.app.toaster.recommendsite.service;

import com.app.toaster.recommendsite.domain.RecommendSite;
import com.app.toaster.recommendsite.infrastructure.RecommedSiteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendSiteService {
    private final RecommedSiteRepository recommedSiteRepository;

    public List<RecommendSite> getRecommendSites(){

        return recommedSiteRepository.findAll().subList(0, Math.min(9, recommedSiteRepository.findAll().size()));
    }

}
