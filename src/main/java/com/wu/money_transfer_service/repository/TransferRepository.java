package com.wu.money_transfer_service.repository;


import com.wu.money_transfer_service.entity.Transfer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferRepository extends MongoRepository<Transfer, String> {
    Optional<Transfer> findByTransferId(String transferId);



}
