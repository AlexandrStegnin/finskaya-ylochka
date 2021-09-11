package com.finskayaylochka.model.supporting.filters;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.UnderFacility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CashFilter extends AbstractFilter {
    private AppUser investor;
    private List<Facility> facilities;
    private List<UnderFacility> underFacilities;
    private boolean fromApi;
    private int filtered = 0;
    private boolean accepted = true;
}
