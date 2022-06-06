package de.hsflensburg.dataservice.domain.mapper;

import de.hsflensburg.dataservice.domain.dto.CategoryInfo;
import de.hsflensburg.dataservice.domain.dto.RequestResponse;
import de.hsflensburg.dataservice.domain.dto.ShareResponse;
import de.hsflensburg.dataservice.domain.model.Request;

import java.util.List;
import java.util.Map;

public class RequestMapper {


    /**
     * Maps request to request response
     * @param request the original request
     * @param categoryInfo the request category info
     * @return the request response object
     */
    public static RequestResponse requestToRequestResponse(Request request, CategoryInfo categoryInfo) {
        RequestResponse resp = requestToBasicRequestResponse(request, categoryInfo);

        List<ShareResponse> shares = request.getShares().stream().map((ShareMapper.INSTANCE::shareToShareResponse)).toList();
        resp.setShares(shares);

        return resp;
    }

    /**
     * Maps request to request response, but only includes subject in shares
     * @param request the original request
     * @param subject the subject to be filtered in shares
     * @param categoryInfo the request category info
     * @return the request response object
     */
    public static RequestResponse requestToFilteredRequestResponse(Request request, String subject, CategoryInfo categoryInfo) {
        RequestResponse resp = requestToBasicRequestResponse(request, categoryInfo);

        List<ShareResponse> shares = request.getShares().stream()
                .filter((share -> share.getSubject().equals(subject)))
                .map((ShareMapper.INSTANCE::shareToShareResponse)).toList();
        resp.setShares(shares);

        return resp;
    }

    /**
     * Maps request to request response, but does not include shares
     * @param request the original request
     * @param categoryInfo the request category info
     * @return the request response object
     */
    private static RequestResponse requestToBasicRequestResponse(Request request, CategoryInfo categoryInfo) {
        RequestResponse resp = new RequestResponse();

        resp.setId(request.getId().toHexString());
        resp.setTemplateId(request.getTemplateId().toHexString());
        resp.setCreatedAt(request.getCreatedAt());
        resp.setModifiedAt(request.getModifiedAt());
        resp.setOwner(request.getOwner());
        resp.setTitle(request.getTitle());
        resp.setCategory(categoryInfo);
        resp.setParticipants(request.getParticipants());
        resp.setComponents(request.getComponents());
        resp.setActive(request.isActive());

        return resp;
    }
}
