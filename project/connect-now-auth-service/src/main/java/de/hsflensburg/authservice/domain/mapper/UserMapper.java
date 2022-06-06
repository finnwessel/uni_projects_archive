package de.hsflensburg.authservice.domain.mapper;

import de.hsflensburg.authservice.domain.dto.UserResponse;
import de.hsflensburg.authservice.domain.model.BasicUser;
import de.hsflensburg.authservice.domain.model.LdapUser;
import de.hsflensburg.authservice.domain.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse userToUserResponse(UserModel userModel);

    UserModel basicUserToUser(BasicUser basicUser);
    UserModel ldapUserToUser(LdapUser ldapUser);
}
