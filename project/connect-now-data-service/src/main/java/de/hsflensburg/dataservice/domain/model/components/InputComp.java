package de.hsflensburg.dataservice.domain.model.components;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InputComp extends BaseComp implements Serializable {
    @NotNull
    String text;
    @NotNull
    int maxChars;
}
