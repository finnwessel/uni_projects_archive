package de.hsflensburg.dataservice.domain.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.NoTemplateMatchException;
import de.hsflensburg.dataservice.exceptions.TemplateNotFoundException;
import de.hsflensburg.dataservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TemplateValidator {

    private final TemplateService templateService;

    public void validate(String templateId, List<BaseComp> components) throws NoTemplateMatchException, BadFormatException, TemplateNotFoundException {
        Optional<Template> template = templateService.getById(templateId);

        if (template.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode templateNode = mapper.valueToTree(template.get().getComponents());
            JsonNode requestNode = mapper.valueToTree(components);

            List<List<String>> templateComponents = componentNodeToList(templateNode);
            List<List<String>> requestComponents = componentNodeToList(requestNode);

            if (!templateComponents.equals(requestComponents)) {
                throw new NoTemplateMatchException();
            }
        } else {
            throw new TemplateNotFoundException();
        }
    }

    private List<List<String>> componentNodeToList(JsonNode node) {
        List<List<String>> components = new ArrayList<>();
        Iterator<JsonNode> e = node.elements();
        while (e.hasNext()) {
            JsonNode elements = e.next();
            Iterator<JsonNode> descriptions = elements.get("description").elements();
            List<String> componentValues = new ArrayList<>();
            while (descriptions.hasNext()) {
                JsonNode description = descriptions.next();
                componentValues.add(description.textValue());
            }
            String type = elements.get("type").textValue();
            componentValues.add(type);
            componentValues.sort(Comparator.naturalOrder());
            components.add(componentValues);
        }
        return components;
    }
}
