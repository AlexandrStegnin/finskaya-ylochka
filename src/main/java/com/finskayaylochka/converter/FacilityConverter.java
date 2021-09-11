package com.finskayaylochka.converter;

import com.finskayaylochka.model.Facility;
import com.finskayaylochka.service.FacilityService;
import org.springframework.core.convert.converter.Converter;

public class FacilityConverter implements Converter<String, Facility> {

    private final FacilityService facilityService;

    public FacilityConverter(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    public Facility convert(String id) {
        Facility facility;
        try {
            Long IntId = Long.valueOf(id);
            facility = facilityService.findById(IntId);
        } catch (Exception ex) {
            facility = facilityService.findByName(id);
        }

        return facility;
    }
}
