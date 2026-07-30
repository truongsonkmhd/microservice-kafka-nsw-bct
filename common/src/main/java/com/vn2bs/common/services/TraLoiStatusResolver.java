package com.vn2bs.common.services;

import org.springframework.stereotype.Component;

import com.vn2bs.common.domains.BusinessStatus;

@Component
public class TraLoiStatusResolver {

    public BusinessStatus resolve(String ketQua) {
        if (ketQua != null && ketQua.toLowerCase().contains("tu choi")) {
            return BusinessStatus.TU_CHOI;
        }
        return BusinessStatus.DA_PHE_DUYET;
    }
}
