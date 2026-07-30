package com.vn2bs.common.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.vn2bs.common.domains.BusinessStatus;

class TraLoiStatusResolverTest {

    private final TraLoiStatusResolver resolver = new TraLoiStatusResolver();

    @Test
    void resolve_tuChoiKeyword() {
        assertEquals(BusinessStatus.TU_CHOI, resolver.resolve("Tu choi: Thieu giay to"));
    }

    @Test
    void resolve_duyet() {
        assertEquals(BusinessStatus.DA_PHE_DUYET, resolver.resolve("Phe duyet ho so thanh cong"));
    }
}
