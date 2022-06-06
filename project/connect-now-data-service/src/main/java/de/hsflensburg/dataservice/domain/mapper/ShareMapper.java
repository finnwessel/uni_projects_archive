package de.hsflensburg.dataservice.domain.mapper;

import de.hsflensburg.dataservice.domain.dto.ShareResponse;
import de.hsflensburg.dataservice.domain.model.Share;
import de.hsflensburg.dataservice.domain.model.ShareStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ShareMapper {
    ShareMapper INSTANCE = Mappers.getMapper(ShareMapper.class);

    @Mapping(source = "status", target = "status", qualifiedByName = "shareStatusToString")
    ShareResponse shareToShareResponse(Share share);

    @Named("shareStatusToString")
    static String shareStatusToString(ShareStatus status) {
        return status.toString();
    }

    static List<String> sharesToActiveShareList(List<Share> shares) {
        return shares.stream()
                .filter((share) -> share.isStudentActive() && share.isTeacherActive())
                .map(Share::getSubject).toList();
    }
}
