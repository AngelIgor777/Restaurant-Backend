package org.test.restaurant_service.dto.request.table;

import lombok.Data;

import java.util.Set;

@Data
public class OpenTables {
    private Set<Integer> ids;
}
