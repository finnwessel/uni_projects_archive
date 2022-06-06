package social_media_platform.beans;

import lombok.Data;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Data
@Named("index")
@ViewScoped
public class IndexBean implements Serializable {

    private boolean editorVisible = false;

    public void toggleEditorVisible() {
        this.editorVisible = !this.editorVisible;
    }
}
