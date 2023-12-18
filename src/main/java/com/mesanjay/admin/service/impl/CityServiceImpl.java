package com.mesanjay.admin.service.impl;

import com.mesanjay.admin.model.City;
import com.mesanjay.admin.repository.CityRepository;
import com.mesanjay.admin.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }
}
