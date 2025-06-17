package com.unisew.account_service.mappers;

import com.unisew.account_service.models.Account;
import com.unisew.account_service.requests.AccountRequestDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper extends Mapper<AccountRequestDTO, Account> {
    
    @Override
    public AccountRequestDTO toDTO(Account entity) {
        if (entity == null) {
            return null;
        }
        
        return AccountRequestDTO.builder()
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }

    @Override
    public Account toEntity(AccountRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Account.builder()
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }

    @Override
    public Iterable<AccountRequestDTO> toDTOs(Iterable<Account> entities) {
        if (entities == null) {
            return null;
        }
        
        List<AccountRequestDTO> dtos = new ArrayList<>();
        for (Account entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    @Override
    public Iterable<Account> toEntities(Iterable<AccountRequestDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        List<Account> entities = new ArrayList<>();
        for (AccountRequestDTO dto : dtos) {
            entities.add(toEntity(dto));
        }
        return entities;
    }
}
