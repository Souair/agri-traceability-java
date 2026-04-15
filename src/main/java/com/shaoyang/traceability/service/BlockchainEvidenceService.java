package com.shaoyang.traceability.service;

import org.springframework.stereotype.Service;

@Service
public class BlockchainEvidenceService {

    /**
     * 模拟上链存证，后续可替换为 Hyperledger Fabric / FISCO BCOS SDK 调用。
     */
    public String anchorHash(String hash) {
        String shortHash = hash.length() > 16 ? hash.substring(0, 16) : hash;
        return "SIM-" + shortHash.toUpperCase();
    }
}
