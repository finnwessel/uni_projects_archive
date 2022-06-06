package de.hsflensburg.dataservice.domain.model.components;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DateComp extends BaseComp implements Serializable {
    @NotNull
    Date date;
}
