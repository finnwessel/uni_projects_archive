package de.hsflensburg.dataservice.domain.model.components;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FileComp extends BaseComp implements Serializable {
    @NotNull
    List<String> files;
}
