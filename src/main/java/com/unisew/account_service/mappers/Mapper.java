package com.unisew.account_service.mappers;

public abstract class Mapper<D,E> {

    public abstract D toDTO(E entity);

    public abstract E toEntity(D dto);

    public abstract Iterable<D> toDTOs(Iterable<E> entities);

    public abstract Iterable<E> toEntities(Iterable<D> dtos);
}
