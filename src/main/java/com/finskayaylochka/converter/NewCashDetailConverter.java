package com.finskayaylochka.converter;

import com.finskayaylochka.model.NewCashDetail;
import com.finskayaylochka.service.NewCashDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class NewCashDetailConverter implements Converter<String, NewCashDetail> {

    private final NewCashDetailService newCashDetailService;

    @Autowired
    public NewCashDetailConverter(NewCashDetailService newCashDetailService) {
        this.newCashDetailService = newCashDetailService;
    }

    public NewCashDetail convert(String id) {
        Long IntId = Long.valueOf(id);
        NewCashDetail newCashDetail;
        newCashDetail = newCashDetailService.findById(IntId);
        return newCashDetail;
    }
}
