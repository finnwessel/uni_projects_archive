package de.hsflensburg.dataservice.unit.validation;

import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import de.hsflensburg.dataservice.domain.model.components.TextComp;
import de.hsflensburg.dataservice.domain.validation.TemplateValidator;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.NoTemplateMatchException;
import de.hsflensburg.dataservice.exceptions.TemplateNotFoundException;
import de.hsflensburg.dataservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class TestTemplateValidator {

    @Mock
    private TemplateService templateService;
    @Autowired
    private TemplateValidator templateValidator;

    @BeforeEach
    private void init() {
        templateValidator = new TemplateValidator(templateService);
    }

    @Test
    public void testValidate() throws BadFormatException {
        ObjectId templateId = new ObjectId();

        List<BaseComp> comps = new ArrayList<>();

        TextComp textComp = new TextComp();
        textComp.setType("text");
        textComp.setDescription(Map.of("de", "Beschreibung", "en", "description"));
        textComp.setRequired(true);
        textComp.setText("content");

        comps.add(textComp);

        Template template = new Template(Map.of("de", "Titel", "en", "Title"), new ObjectId(), comps);

        when(templateService.getById(templateId.toHexString())).thenReturn(Optional.of(template));

        assertDoesNotThrow(() -> templateValidator.validate(templateId.toHexString(), new ArrayList<>(comps)));
    }

    @Test
    public void testValidateNotSuccess() throws BadFormatException {
        ObjectId templateId = new ObjectId();

        List<BaseComp> comps = new ArrayList<>();

        TextComp textComp = new TextComp();
        textComp.setType("text");
        textComp.setDescription(Map.of("de", "Beschreibung", "en", "description"));
        textComp.setRequired(true);
        textComp.setText("content");

        comps.add(textComp);

        Template template = new Template(Map.of("de", "Titel", "en", "Title"), new ObjectId(), comps);

        when(templateService.getById(templateId.toHexString())).thenReturn(Optional.of(template));

        List<BaseComp> checkComps = new ArrayList<>();

        TextComp checkTextComp = new TextComp();
        checkTextComp.setType("text");
        checkTextComp.setDescription(Map.of("de", "test", "en", "123"));
        checkTextComp.setRequired(true);
        checkTextComp.setText("content");

        comps.add(textComp);

        assertThrows(NoTemplateMatchException.class, () -> templateValidator.validate(templateId.toHexString(), new ArrayList<>(checkComps)));
    }

    @Test
    public void testValidateNoTemplate() throws BadFormatException {
        ObjectId templateId = new ObjectId();

        when(templateService.getById(templateId.toHexString())).thenReturn(Optional.empty());

        assertThrows(TemplateNotFoundException.class, () -> templateValidator.validate(templateId.toHexString(), new ArrayList<>()));
    }
}
