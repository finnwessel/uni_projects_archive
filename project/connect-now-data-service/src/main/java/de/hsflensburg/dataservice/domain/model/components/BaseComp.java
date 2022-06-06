package de.hsflensburg.dataservice.domain.model.components;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DateComp.class, name = "date"),
        @JsonSubTypes.Type(value = DateRangeComp.class, name = "dateRange"),
        @JsonSubTypes.Type(value = FileComp.class, name = "file"),
        @JsonSubTypes.Type(value = InputComp.class, name = "input"),
        @JsonSubTypes.Type(value = TextComp.class, name = "text")
})
public class BaseComp implements Serializable {

    @NotNull
    String type;
    @NotNull
    boolean required;
    @NotNull
    Map<String, String> description;
}
