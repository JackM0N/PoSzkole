package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.model.WebsiteUser;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebsiteUserMapper {
    @Mapping(target = "isDeleted", constant = "false")
    WebsiteUser toEntity(WebsiteUserDTO websiteUserDTO);

    @Mapping(target = "classes", ignore = true)
    WebsiteUserDTO toDto(WebsiteUser websiteUser);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "hourlyRate", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "issueInvoice", ignore = true)
    @Mapping(target = "priceListId", ignore = true)
    @Mapping(target = "classes", ignore = true)
    WebsiteUserDTO toDtoWithoutSensitiveData(WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hourlyRate", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "guardianPhone", ignore = true)
    @Mapping(target = "guardianEmail", ignore = true)
    @Mapping(target = "priceList", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "isCashPayment", ignore = true)
    @Mapping(target = "issueInvoice", ignore = true)
    WebsiteUser partialUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hourlyRate", ignore = true)
    @Mapping(target = "priceList", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "isCashPayment", ignore = true)
    WebsiteUser partialProfileUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);
}