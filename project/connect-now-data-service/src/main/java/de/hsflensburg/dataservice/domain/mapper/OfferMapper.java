package de.hsflensburg.dataservice.domain.mapper;

import de.hsflensburg.dataservice.domain.dto.OfferResponse;
import de.hsflensburg.dataservice.domain.model.Offer;

import java.util.Map;

public class OfferMapper {
    public static OfferResponse offerToOfferResponse(Offer offer, Map<String, String> categoryNames) {
        OfferResponse resp = new OfferResponse();

        resp.setId(offer.getId().toHexString());
        resp.setCreatedAt(offer.getCreatedAt());
        resp.setModifiedAt(offer.getModifiedAt());
        resp.setOwner(offer.getOwner());
        resp.setTitle(offer.getTitle());
        resp.setCategory(categoryNames);
        resp.setVisible(offer.isVisible());
        resp.setComponents(offer.getComponents());

        return resp;
    }
}
