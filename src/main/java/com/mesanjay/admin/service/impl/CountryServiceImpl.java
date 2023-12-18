package com.mesanjay.admin.service.impl;

import com.mesanjay.admin.model.Country;
import com.mesanjay.admin.repository.CountryRepository;
import com.mesanjay.admin.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }
}
