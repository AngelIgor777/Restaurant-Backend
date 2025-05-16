package org.test.restaurant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.test.restaurant_service.entity.WorkingHours;

import java.util.List;

@Getter
@AllArgsConstructor
public class InfoResponseDTO {
    private List<TableResponseDTO> tables;
    private List<FeatureStatusResponseDTO> features;
    private List<WorkingHours> workingHours;
}
